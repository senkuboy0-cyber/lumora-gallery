package app.lumora.gallery.data.local.media

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.MediaStore
import app.lumora.gallery.data.local.database.MediaEntity

class MediaStoreScanner(private val contentResolver: ContentResolver) {
    fun scan(): List<MediaEntity> {
        val items = mutableListOf<MediaEntity>()
        val collection = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATE_TAKEN,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.WIDTH,
            MediaStore.Files.FileColumns.HEIGHT,
            MediaStore.Video.VideoColumns.DURATION,
            MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME
        )
        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} IN (?, ?)"
        val args = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )
        contentResolver.query(collection, projection, selection, args, "${MediaStore.Files.FileColumns.DATE_TAKEN} DESC")?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val mimeIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
            val dateIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_TAKEN)
            val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val widthIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.WIDTH)
            val heightIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.HEIGHT)
            val durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION)
            val albumIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val uri = ContentUris.withAppendedId(collection, id).toString()
                items += MediaEntity(
                    id = id,
                    uri = uri,
                    fileName = cursor.getString(nameIndex).orEmpty(),
                    albumName = cursor.getString(albumIndex).orEmpty().ifBlank { "Camera Roll" },
                    mimeType = cursor.getString(mimeIndex).orEmpty(),
                    dateTakenMillis = cursor.getLong(dateIndex),
                    sizeBytes = cursor.getLong(sizeIndex),
                    durationMillis = cursor.getLong(durationIndex).takeIf { it > 0L },
                    width = cursor.getInt(widthIndex),
                    height = cursor.getInt(heightIndex),
                    isFavourite = false,
                    isDeleted = false,
                    deletedAtMillis = null
                )
            }
        }
        return items
    }
}
