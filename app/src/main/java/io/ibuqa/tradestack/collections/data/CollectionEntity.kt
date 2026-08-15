package io.ibuqa.tradestack.collections.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SyncState {
    NOT_SYNCED,
    SYNCING,
    SYNCED,
    REJECTED
}

/**
 * A collection as the handset knows it.
 *
 * `clientUuid` is generated on the device and is the identity the server keys
 * on. It must be stable across retries - that is the whole point of it.
 */
@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey val clientUuid: String,
    val outletCode: String,
    val outletName: String,
    val invoiceNo: String,
    val method: String,
    val amountKes: Double,
    val receiptRef: String,
    val recordedAtEpochMs: Long,
    val state: SyncState = SyncState.NOT_SYNCED,
    val rejectReason: String? = null,
)
