package io.ibuqa.tradestack.collections.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CollectionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun collections(): CollectionDao
}
