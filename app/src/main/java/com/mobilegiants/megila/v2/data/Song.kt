package com.mobilegiants.megila.v2.data

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.datasource.RawResourceDataSource

data class Song(
    val id: String,
    val title: String,
    val resourceId: Int? = null,
    val remoteUrl: String? = null,
    val order: Int = 0,
    val isLocal: Boolean = true
) {
    val mediaItem: MediaItem
        get() = when {
            resourceId != null -> MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(resourceId))
            remoteUrl != null -> MediaItem.fromUri(Uri.parse(remoteUrl))
            else -> throw IllegalStateException("Song must have either resourceId or remoteUrl")
        }
}
