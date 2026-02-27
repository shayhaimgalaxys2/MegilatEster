package com.mobilegiants.megila.data

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mobilegiants.megila.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class SongRepository(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()

    private val localSongs: List<Song> by lazy {
        listOf(
            Song("local_0", context.getString(R.string.song_name_new_machrozet), R.raw.porim_songs_set, order = 0),
            Song("local_1", context.getString(R.string.song_name_mishenichnas_adar), R.raw.song1, order = 1),
            Song("local_2", context.getString(R.string.song_name_ve_nahafocho), R.raw.song2, order = 2),
            Song("local_3", context.getString(R.string.song_name_layeodim_haita), R.raw.song3, order = 3),
            Song("local_4", context.getString(R.string.song_name_hag_purim), R.raw.song4, order = 4),
            Song("local_5", context.getString(R.string.song_name_mordechai_yaza), R.raw.song5, order = 5),
            Song("local_6", context.getString(R.string.song_name_hayav_einish), R.raw.song6, order = 6),
            Song("local_7", context.getString(R.string.song_name_machrozet), R.raw.song7, order = 7),
        )
    }

    fun getAllSongs(): Flow<List<Song>> = flow {
        emit(localSongs)
        try {
            val snapshot = firestore.collection("songs")
                .orderBy("order")
                .get()
                .await()
            val onlineSongs = snapshot.documents.mapNotNull { doc ->
                val title = doc.getString("title") ?: return@mapNotNull null
                val url = doc.getString("url") ?: return@mapNotNull null
                Song(
                    id = doc.id,
                    title = title,
                    remoteUrl = url,
                    order = doc.getLong("order")?.toInt() ?: 0,
                    isLocal = false
                )
            }
            if (onlineSongs.isNotEmpty()) {
                emit(localSongs + onlineSongs)
            }
        } catch (e: Exception) {
            Log.w("SongRepository", "Failed to fetch online songs", e)
        }
    }
}
