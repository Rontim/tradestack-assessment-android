package io.ibuqa.tradestack.collections.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Built to the spec in docs/design-note.md.
 *
 * Two states: saved and failed. A receipt that has been written to the local
 * database is shown as saved, with a green tick, immediately.
 */
@Composable
fun SyncStatusChip(saved: Boolean, modifier: Modifier = Modifier) {
    Row(modifier.padding(vertical = 2.dp)) {
        AssistChip(
            onClick = {},
            label = { Text(if (saved) "Saved" else "Failed") },
            leadingIcon = {
                Icon(
                    imageVector = if (saved) Icons.Filled.Check else Icons.Filled.Close,
                    contentDescription = null,
                )
            },
            colors = AssistChipDefaults.assistChipColors(
                labelColor = if (saved) Color(0xFF1B4332) else Color(0xFF991B1B),
                leadingIconContentColor =
                    if (saved) Color(0xFF2C7A6B) else Color(0xFF991B1B),
            ),
        )
    }
}
