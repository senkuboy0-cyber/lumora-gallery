package app.lumora.gallery.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_items")
data class MediaEntity(
    @PrimaryKey val id: Long,
    val uri: String,
    val fileName: String,
    val albumName: String,
    val mimeType: String,
    val dateTakenMillis: Long,
    val sizeBytes: Long,
    val durationMillis: Long?,
    val width: Int,
    val height: Int,
    val isFavourite: Boolean,
    val isDeleted: Boolean,
    val deletedAtMillis: Long?
)
