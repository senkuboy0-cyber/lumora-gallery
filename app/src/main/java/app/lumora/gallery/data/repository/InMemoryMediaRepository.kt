package app.lumora.gallery.data.repository

import android.net.Uri
import app.lumora.gallery.domain.model.Album
import app.lumora.gallery.domain.model.MediaItem
import app.lumora.gallery.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class InMemoryMediaRepository : MediaRepository {
    private val media = MutableStateFlow(sampleMedia())

    override fun observeMedia(): Flow<List<MediaItem>> = media

    override fun observeAlbums(): Flow<List<Album>> = media.map { items ->
        items.filterNot { it.isDeleted }
            .groupBy { it.albumName }
            .map { (name, albumItems) -> Album(name = name, count = albumItems.size, coverItems = albumItems.take(3)) }
    }

    override suspend fun refresh() = Unit

    override suspend fun toggleFavourite(id: Long) {
        media.value = media.value.map { if (it.id == id) it.copy(isFavourite = !it.isFavourite) else it }
    }

    override suspend fun moveToRecycleBin(ids: Set<Long>) {
        val now = System.currentTimeMillis()
        media.value = media.value.map { if (it.id in ids) it.copy(isDeleted = true, deletedAtMillis = now) else it }
    }

    override suspend fun restore(ids: Set<Long>) {
        media.value = media.value.map { if (it.id in ids) it.copy(isDeleted = false, deletedAtMillis = null) else it }
    }

    override suspend fun permanentlyDelete(ids: Set<Long>) {
        media.value = media.value.filterNot { it.id in ids }
    }

    private fun sampleMedia(): List<MediaItem> = (1L..36L).map { index ->
        MediaItem(
            id = index,
            uri = Uri.parse("https://picsum.photos/seed/lumora$index/900/900"),
            fileName = "Lumora_$index.jpg",
            albumName = when {
                index % 5L == 0L -> "Screenshots"
                index % 4L == 0L -> "Videos"
                index % 3L == 0L -> "Downloads"
                else -> "Camera Roll"
            },
            mimeType = if (index % 4L == 0L) "video/mp4" else "image/jpeg",
            dateTakenMillis = System.currentTimeMillis() - index * 86_400_000L,
            sizeBytes = 1_400_000L + index * 50_000L,
            durationMillis = if (index % 4L == 0L) 72_000L else null,
            width = 3000,
            height = 3000,
            isFavourite = index % 7L == 0L
        )
    }
}
