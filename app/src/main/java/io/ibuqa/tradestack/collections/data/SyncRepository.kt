package io.ibuqa.tradestack.collections.data

import java.io.IOException
import java.time.Instant
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * The seam between local storage and the server.
 */
@Singleton
class SyncRepository @Inject constructor(
    private val dao: CollectionDao,
    private val api: CollectionsApi,
    @Named("deviceId") private val deviceId: String,
) {
    suspend fun syncPending(): Result<Unit> {
        dao.resetStale()

        val pending = dao.pending()
        if (pending.isEmpty()) {
            return Result.success(Unit)
        }

        val uuids = pending.map { it.clientUuid }

        dao.markSyncing(uuids)

        val response = try {
            api.pushBatch(BatchDto(
                device_id = deviceId,
                receipts = pending.take(200).map { it.toDto() },
            ),
            )
        } catch (e: IOException) {
            pending.forEach { dao.setState(it.clientUuid, SyncState.NOT_SYNCED) }
            return Result.failure(e)
        }

        val body = response.body()
        if (!response.isSuccessful || body == null) {
            pending.forEach { dao.setState(it.clientUuid, SyncState.NOT_SYNCED) }
            return Result.failure(IOException("HTTP ${response.code()}"))
        }

        for (r in body.results) {
            val uuid = r.client_uuid ?: continue
            when (r.status) {
                "accepted", "duplicate" -> dao.setState(uuid, SyncState.SYNCED)
                "rejected" -> dao.markRejected(uuid, r.reason)
                else -> dao.setState(uuid, SyncState.NOT_SYNCED)
            }
        }

        dao.resetStale()
        return Result.success(Unit)
    }


    private fun CollectionEntity.toDto() = ReceiptDto(
        client_uuid = clientUuid,
        outlet_code = outletCode,
        invoice_no = invoiceNo,
        method = method,
        amount_kes = amountKes,
        receipt_ref = receiptRef,
        recorded_at = Instant.ofEpochMilli(recordedAtEpochMs).toString(),
    )
}
