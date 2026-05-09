package app.lumora.gallery.data.local.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("SELECT * FROM media_items ORDER BY dateTakenMillis DESC")
    fun observeAll(): Flow<List<MediaEntity>>

    @Upsert
    suspend fun upsertAll(items: List<MediaEntity>)

    @Query("UPDATE media_items SET isFavourite = NOT isFavourite WHERE id = :id")
    suspend fun toggleFavourite(id: Long)

    @Query("UPDATE media_items SET isDeleted = 1, deletedAtMillis = :deletedAtMillis WHERE id IN (:ids)")
    suspend fun moveToRecycleBin(ids: Set<Long>, deletedAtMillis: Long)

    @Query("UPDATE media_items SET isDeleted = 0, deletedAtMillis = NULL WHERE id IN (:ids)")
    suspend fun restore(ids: Set<Long>)

    @Query("DELETE FROM media_items WHERE id IN (:ids)")
    suspend fun permanentlyDelete(ids: Set<Long>)
}
