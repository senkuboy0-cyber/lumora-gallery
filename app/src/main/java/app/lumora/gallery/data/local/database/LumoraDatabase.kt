package app.lumora.gallery.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MediaEntity::class], version = 1, exportSchema = false)
abstract class LumoraDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
}
