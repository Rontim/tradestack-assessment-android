package io.ibuqa.tradestack.collections.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * TODO(candidate): list every collection on the handset, newest first, with
 *  its state, plus a way to trigger a sync and some honest indication of what
 *  is outstanding.
 */
@Composable
fun CollectionListScreen(onRecord: () -> Unit) {
    Column(Modifier.padding(16.dp)) {
        Text("Collections")
        Button(onClick = onRecord) { Text("Record a collection") }
    }
}
