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

    @Query("SELECT * FROM collections WHERE state != 'SYNCED' ORDER BY recordedAtEpochMs")
    suspend fun pending(): List<CollectionEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(row: CollectionEntity)

    @Update
    suspend fun update(row: CollectionEntity)

    @Query("SELECT COUNT(*) FROM collections WHERE state != 'SYNCED'")
    fun pendingCount(): Flow<Int>

    @Query("UPDATE collections SET state = :state WHERE clientUuid = :clientUuid")
    suspend fun setState(clientUuid: String, state: SyncState)

    @Query("UPDATE collections SET state = 'NOT_SYNCED' WHERE state = 'SYNCING'")
    suspend fun resetStale()

    @Query("UPDATE collections SET state = 'REJECTED', rejectReason = :reason WHERE clientUuid = :uuid")
    suspend fun markRejected(uuid: String, reason: String?)
}
