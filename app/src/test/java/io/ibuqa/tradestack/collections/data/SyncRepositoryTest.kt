package io.ibuqa.tradestack.collections.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response

class SyncRepositoryTest {

    private val dao = FakeCollectionDao()
    private val api = FakeCollectionsApi()
    private val repository = SyncRepository(dao, api, "test-device")

    private fun entity(
        uuid: String,
        state: SyncState = SyncState.NOT_SYNCED,
    ) = CollectionEntity(
        clientUuid = uuid,
        outletCode = "O1",
        outletName = "Outlet",
        invoiceNo = "I1",
        method = "Cash",
        amountKes = 100.0,
        receiptRef = "R1",
        recordedAtEpochMs = 1000L,
        state = state,
    )

    private fun batchOf(vararg results: ReceiptResultDto) =
        Response.success(207, BatchResultDto(results.toList()))

    @Test
    fun `accepted response marks receipt synced`() = runTest {
        dao.insert(entity("u1"))
        api.nextResponse = batchOf(ReceiptResultDto("u1", "accepted", null))

        val result = repository.syncPending()

        assertEquals(true, result.isSuccess)
        assertEquals(SyncState.SYNCED, dao.items["u1"]?.state)
    }

    @Test
    fun `duplicate response counts as synced`() = runTest {
        dao.insert(entity("u2"))
        api.nextResponse = batchOf(ReceiptResultDto("u2", "duplicate", null))

        val result = repository.syncPending()

        assertEquals(true, result.isSuccess)
        assertEquals(SyncState.SYNCED, dao.items["u2"]?.state)
    }

    @Test
    fun `rejected receipt is terminal with reason and does not fail the sync`() = runTest {
        dao.insert(entity("u3"))
        api.nextResponse = batchOf(ReceiptResultDto("u3", "rejected", "Invalid outlet"))

        val result = repository.syncPending()

        assertEquals(true, result.isSuccess)
        assertEquals(SyncState.REJECTED, dao.items["u3"]?.state)
        assertEquals("Invalid outlet", dao.items["u3"]?.rejectReason)
    }

    @Test
    fun `network error resets rows and fails the sync`() = runTest {
        dao.insert(entity("u4"))
        api.shouldThrow = true

        val result = repository.syncPending()

        assertEquals(true, result.isFailure)
        assertEquals(SyncState.NOT_SYNCED, dao.items["u4"]?.state)
    }

    @Test
    fun `stale syncing row from a killed push is healed and delivered`() = runTest {
        dao.insert(entity("u5", state = SyncState.SYNCING))
        api.nextResponse = batchOf(ReceiptResultDto("u5", "accepted", null))

        val result = repository.syncPending()

        assertEquals(true, result.isSuccess)
        assertEquals(SyncState.SYNCED, dao.items["u5"]?.state)
    }

    @Test
    fun `row unanswered by the server ends NOT_SYNCED not stuck SYNCING`() = runTest {
        dao.insert(entity("u6"))
        dao.insert(entity("u7"))
        api.nextResponse = batchOf(ReceiptResultDto("u6", "accepted", null))

        val result = repository.syncPending()

        assertEquals(true, result.isSuccess)
        assertEquals(SyncState.SYNCED, dao.items["u6"]?.state)
        assertEquals(SyncState.NOT_SYNCED, dao.items["u7"]?.state)
    }
}

class FakeCollectionDao : CollectionDao {
    val items = mutableMapOf<String, CollectionEntity>()
    private val _flow = MutableStateFlow<List<CollectionEntity>>(emptyList())

    override fun observeAll(): Flow<List<CollectionEntity>> = _flow

    override suspend fun pending(): List<CollectionEntity> {
        return items.values.filter { it.state != SyncState.SYNCED }.sortedBy { it.recordedAtEpochMs }
    }

    override suspend fun insert(row: CollectionEntity) {
        items[row.clientUuid] = row
        _flow.value = items.values.toList()
    }

    override suspend fun update(row: CollectionEntity) {
        items[row.clientUuid] = row
        _flow.value = items.values.toList()
    }

    override fun pendingCount(): Flow<Int> = _flow.map { it.count { e -> e.state != SyncState.SYNCED } }

    override suspend fun setState(clientUuid: String, state: SyncState) {
        items[clientUuid]?.let {
            items[clientUuid] = it.copy(state = state)
        }
        _flow.value = items.values.toList()
    }

    override suspend fun resetStale() {
        items.forEach { (uuid, entity) ->
            if (entity.state == SyncState.SYNCING) {
                items[uuid] = entity.copy(state = SyncState.NOT_SYNCED)
            }
        }
        _flow.value = items.values.toList()
    }

    override suspend fun markRejected(uuid: String, reason: String?) {
        items[uuid]?.let {
            items[uuid] = it.copy(state = SyncState.REJECTED, rejectReason = reason)
        }
        _flow.value = items.values.toList()
    }

    override suspend fun markSyncing(uuids: List<String>) {
        uuids.forEach { uuid ->
            items[uuid]?.let {
                items[uuid] = it.copy(state = SyncState.SYNCING)
            }
        }
        _flow.value = items.values.toList()
    }
}

class FakeCollectionsApi : CollectionsApi {
    var nextResponse: Response<BatchResultDto>? = null
    var shouldThrow = false

    override suspend fun outlets(): OutletListDto = OutletListDto(emptyList())

    override suspend fun pushBatch(body: BatchDto): Response<BatchResultDto> {
        if (shouldThrow) throw java.io.IOException("Network error")
        return nextResponse ?: throw IllegalStateException("No response set")
    }
}
