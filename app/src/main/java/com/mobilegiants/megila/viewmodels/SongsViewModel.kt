package com.mobilegiants.megila.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.mobilegiants.megila.data.Song
import com.mobilegiants.megila.data.SongRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SongsViewModel(application: Application) : AndroidViewModel(application) {

    data class UiState(
        val songs: List<Song> = emptyList(),
        val currentSongIndex: Int = -1,
        val isPlaying: Boolean = false,
        val currentPosition: Long = 0,
        val duration: Long = 0,
        val isBuffering: Boolean = false
    ) {
        val currentSong: Song? get() = songs.getOrNull(currentSongIndex)
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val repository = SongRepository(application)
    var player: ExoPlayer? = null
        private set

    init {
        loadSongs()
    }

    private fun loadSongs() {
        viewModelScope.launch {
            repository.getAllSongs().collect { songs ->
                _uiState.value = _uiState.value.copy(songs = songs)
            }
        }
    }

    fun initPlayer() {
        if (player != null) return
        val context = getApplication<Application>()
        player = ExoPlayer.Builder(context).build().also { exo ->
            exo.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
                }

                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_BUFFERING -> {
                            _uiState.value = _uiState.value.copy(isBuffering = true)
                        }
                        Player.STATE_READY -> {
                            _uiState.value = _uiState.value.copy(
                                isBuffering = false,
                                duration = exo.duration.coerceAtLeast(0)
                            )
                        }
                        Player.STATE_ENDED -> {
                            playNext()
                        }
                        else -> {
                            _uiState.value = _uiState.value.copy(isBuffering = false)
                        }
                    }
                }
            })
        }
        startPositionUpdates()
    }

    private fun startPositionUpdates() {
        viewModelScope.launch {
            while (true) {
                delay(500)
                player?.let { exo ->
                    if (exo.isPlaying) {
                        _uiState.value = _uiState.value.copy(
                            currentPosition = exo.currentPosition.coerceAtLeast(0)
                        )
                    }
                }
            }
        }
    }

    fun playSong(index: Int) {
        val songs = _uiState.value.songs
        if (index !in songs.indices) return
        val song = songs[index]

        _uiState.value = _uiState.value.copy(currentSongIndex = index, currentPosition = 0)
        player?.apply {
            setMediaItem(song.mediaItem)
            prepare()
            play()
        }
    }

    fun playRandom() {
        val songs = _uiState.value.songs
        if (songs.isEmpty()) return
        val randomIndex = songs.indices.random()
        playSong(randomIndex)
    }

    fun togglePlayPause() {
        val exo = player ?: return
        if (_uiState.value.currentSongIndex == -1) {
            playRandom()
            return
        }
        if (exo.isPlaying) {
            exo.pause()
        } else {
            exo.play()
        }
    }

    fun playNext() {
        val songs = _uiState.value.songs
        if (songs.isEmpty()) return
        val nextIndex = (_uiState.value.currentSongIndex + 1) % songs.size
        playSong(nextIndex)
    }

    fun playPrevious() {
        val songs = _uiState.value.songs
        if (songs.isEmpty()) return
        val prevIndex = (_uiState.value.currentSongIndex - 1 + songs.size) % songs.size
        playSong(prevIndex)
    }

    fun seekTo(position: Long) {
        player?.seekTo(position)
        _uiState.value = _uiState.value.copy(currentPosition = position)
    }

    override fun onCleared() {
        player?.release()
        player = null
    }
}
