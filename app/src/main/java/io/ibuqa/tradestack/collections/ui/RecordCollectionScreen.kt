package io.ibuqa.tradestack.collections.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ibuqa.tradestack.collections.data.CollectionDao
import io.ibuqa.tradestack.collections.data.CollectionEntity
import io.ibuqa.tradestack.collections.worker.SyncScheduler
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val dao: CollectionDao,
    private val scheduler: SyncScheduler,
) : ViewModel() {

    fun save(
        outletCode: String, outletName: String, invoiceNo: String,
        method: String, amountKes: Double, receiptRef: String,
        onDone: () -> Unit,
    ) = viewModelScope.launch {
        dao.insert(
            CollectionEntity(
                clientUuid = UUID.randomUUID().toString(),
                outletCode = outletCode,
                outletName = outletName,
                invoiceNo = invoiceNo,
                method = method,
                amountKes = amountKes,
                receiptRef = receiptRef,
                recordedAtEpochMs = System.currentTimeMillis(),
            )
        )
        scheduler.enqueue()
        onDone()
    }
}

@Composable
fun RecordCollectionScreen(onDone: () -> Unit) {
    Column(Modifier.padding(16.dp)) {
        Text("Record a collection")
    }
}
