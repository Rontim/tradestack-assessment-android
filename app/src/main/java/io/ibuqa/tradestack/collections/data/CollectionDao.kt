package io.ibuqa.tradestack.collections.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {

    @Query("SELECT * FROM collections ORDER BY recordedAtEpochMs DESC")
    fun observeAll(): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM collections WHERE synced = 0 ORDER BY recordedAtEpochMs")
    suspend fun pending(): List<CollectionEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(row: CollectionEntity)

    @Update
    suspend fun update(row: CollectionEntity)

    @Query("SELECT COUNT(*) FROM collections WHERE synced = 0")
    fun pendingCount(): Flow<Int>
}
