package io.ibuqa.tradestack.collections.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A collection as the handset knows it.
 *
 * `clientUuid` is generated on the device and is the identity the server keys
 * on. It must be stable across retries - that is the whole point of it.
 *
 * TODO(candidate): this entity carries one field for state and it is a
 *  Boolean. Decide whether a Boolean is enough to describe what can happen to
 *  a receipt between the rep's thumb and our database, and change it if it is
 *  not. Whatever you decide, say why in DECISIONS.md.
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
    val synced: Boolean = false,
)
