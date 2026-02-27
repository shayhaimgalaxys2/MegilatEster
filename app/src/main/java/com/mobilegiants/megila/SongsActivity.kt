package com.mobilegiants.megila

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.mobilegiants.megila.managers.AdManager
import com.mobilegiants.megila.ui.adapters.SongsAdapter
import com.mobilegiants.megila.viewmodels.SongsViewModel
import kotlinx.coroutines.launch

class SongsActivity : AppCompatActivity() {

    private val viewModel: SongsViewModel by viewModels()

    private lateinit var songsRecyclerView: RecyclerView
    private lateinit var animationView: LottieAnimationView
    private lateinit var seekBar: SeekBar
    private lateinit var currentPositionTextView: TextView
    private lateinit var durationTextView: TextView
    private lateinit var playPauseButton: ImageButton
    private lateinit var rewindButton: ImageButton
    private lateinit var forwardButton: ImageButton

    private lateinit var songsAdapter: SongsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_songs)
        initViews()
        initTopBar()
        initAdapter()
        initBanner()
        initPlayerControls()
        viewModel.initPlayer()
        observeState()
    }

    private fun initTopBar() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = getColor(R.color.blue)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
    }

    private fun initViews() {
        songsRecyclerView = findViewById(R.id.songsRecyclerView)
        animationView = findViewById(R.id.animationView)
        seekBar = findViewById(R.id.seekBar)
        currentPositionTextView = findViewById(R.id.currentPositionTextView)
        durationTextView = findViewById(R.id.durationTextView)
        playPauseButton = findViewById(R.id.playPauseButton)
        rewindButton = findViewById(R.id.rewindButton)
        forwardButton = findViewById(R.id.forwardButton)
    }

    private fun initAdapter() {
        songsAdapter = SongsAdapter { position ->
            viewModel.playSong(position)
        }
        songsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SongsActivity)
            adapter = songsAdapter
        }
    }

    private fun initPlayerControls() {
        playPauseButton.setOnClickListener { viewModel.togglePlayPause() }
        forwardButton.setOnClickListener { viewModel.playNext() }
        rewindButton.setOnClickListener { viewModel.playPrevious() }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.seekTo(progress.toLong())
                    currentPositionTextView.text = formatTime(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // Update song list
                    songsAdapter.submitList(state.songs)

                    // Update selected position
                    songsAdapter.setSelectedPosition(state.currentSongIndex)

                    // Update play/pause button
                    playPauseButton.setImageResource(
                        if (state.isPlaying) android.R.drawable.ic_media_pause
                        else android.R.drawable.ic_media_play
                    )

                    // Update animation
                    animationView.visibility = if (state.isPlaying) View.VISIBLE else View.GONE

                    // Update seek bar
                    seekBar.max = state.duration.toInt()
                    seekBar.progress = state.currentPosition.toInt()
                    currentPositionTextView.text = formatTime(state.currentPosition.toInt())
                    durationTextView.text = formatTime(state.duration.toInt())
                }
            }
        }
    }

    private fun initBanner() {
        AdManager.initialize(this)
        val bannerLayout: FrameLayout = findViewById(R.id.bannerContainer)
        val adView = AdView(this).apply {
            setAdUnitId(AdManager.getBannerAdUnitId())
            setAdSize(AdSize.LARGE_BANNER)
        }
        bannerLayout.addView(adView)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.i(TAG, adError.toString())
            }
            override fun onAdLoaded() {
                Log.i(TAG, "Banner ad loaded")
            }
        }
    }

    private fun formatTime(milliseconds: Int): String {
        val seconds = milliseconds / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%d:%02d", minutes, remainingSeconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.player?.release()
    }

    companion object {
        private const val TAG = "SongsActivity"
    }
}
