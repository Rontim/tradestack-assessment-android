package io.ibuqa.tradestack.collections.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
                outletCode = outletCode.trim(),
                outletName = outletName.trim(),
                invoiceNo = invoiceNo.trim(),
                method = method,
                amountKes = amountKes,
                receiptRef = receiptRef.trim(),
                recordedAtEpochMs = System.currentTimeMillis(),
            )
        )
        scheduler.enqueue()
        onDone()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordCollectionScreen(
    onDone: () -> Unit,
    viewModel: RecordViewModel = hiltViewModel(),
) {
    var outletCode by remember { mutableStateOf("") }
    var outletName by remember { mutableStateOf("") }
    var invoiceNo by remember { mutableStateOf("") }
    var receiptRef by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var method by remember { mutableStateOf("cash") }

    val amount = amountText.toDoubleOrNull()
    val canSave = outletCode.isNotBlank() && amount != null && amount > 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Record collection") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = outletCode, onValueChange = { outletCode = it },
                label = { Text("Outlet code") },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = outletName, onValueChange = { outletName = it },
                label = { Text("Outlet name") },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = invoiceNo, onValueChange = { invoiceNo = it },
                label = { Text("Invoice no") },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = amountText, onValueChange = { amountText = it },
                label = { Text("Amount (KES)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = amountText.isNotBlank() && amount == null,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = receiptRef, onValueChange = { receiptRef = it },
                label = { Text("Receipt ref") },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("cash", "mpesa", "cheque").forEach { m ->
                    FilterChip(
                        selected = method == m,
                        onClick = { method = m },
                        label = { Text(m) },
                    )
                }
            }

            Button(
                onClick = {
                    viewModel.save(
                        outletCode, outletName, invoiceNo,
                        method, amount ?: return@Button, receiptRef,
                        onDone = onDone,
                    )
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            ) {
                Text("Save")
            }
        }
    }
}