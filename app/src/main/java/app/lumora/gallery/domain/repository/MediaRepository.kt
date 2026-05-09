package app.lumora.gallery.domain.repository

import app.lumora.gallery.domain.model.Album
import app.lumora.gallery.domain.model.MediaItem
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun observeMedia(): Flow<List<MediaItem>>
    fun observeAlbums(): Flow<List<Album>>
    suspend fun refresh()
    suspend fun toggleFavourite(id: Long)
    suspend fun moveToRecycleBin(ids: Set<Long>)
    suspend fun restore(ids: Set<Long>)
    suspend fun permanentlyDelete(ids: Set<Long>)
}
