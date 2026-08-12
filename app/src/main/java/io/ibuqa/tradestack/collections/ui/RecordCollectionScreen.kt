package io.ibuqa.tradestack.collections.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * TODO(candidate): outlet, amount, method. Must work with the radio off and
 *  must survive the process being killed.
 */
@Composable
fun RecordCollectionScreen(onDone: () -> Unit) {
    Column(Modifier.padding(16.dp)) {
        Text("Record a collection")
    }
}
