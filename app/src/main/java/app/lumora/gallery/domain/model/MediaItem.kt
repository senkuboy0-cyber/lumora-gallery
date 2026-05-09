package app.lumora.gallery.domain.model

import android.net.Uri

data class MediaItem(
    val id: Long,
    val uri: Uri,
    val fileName: String,
    val albumName: String,
    val mimeType: String,
    val dateTakenMillis: Long,
    val sizeBytes: Long,
    val durationMillis: Long? = null,
    val width: Int = 0,
    val height: Int = 0,
    val isFavourite: Boolean = false,
    val isDeleted: Boolean = false,
    val deletedAtMillis: Long? = null
) {
    val isVideo: Boolean = mimeType.startsWith("video")
}
