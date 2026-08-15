package io.ibuqa.tradestack.collections.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.ibuqa.tradestack.collections.data.SyncState

/**
 * Built to the spec in docs/design-note.md.
 *
 * Two states: saved and failed. A receipt that has been written to the local
 * database is shown as saved, with a green tick, immediately.
 */
@Composable
fun SyncStatusChip(state: SyncState, modifier: Modifier = Modifier) {
    val (icon, label, tint) = when (state) {
        SyncState.NOT_SYNCED,
        SyncState.SYNCING -> Triple(
            Icons.Outlined.CheckCircle,
            "Saved",
            MaterialTheme.colorScheme.outline
        )
        SyncState.SYNCED -> Triple(
            Icons.Default.CheckCircle,
            "Sent",
            MaterialTheme.colorScheme.primary
        )
        SyncState.REJECTED -> Triple(
            Icons.Default.Error,
            "Needs attention",
            MaterialTheme.colorScheme.error
        )
    }
    AssistChip(
        onClick = {},
        modifier = modifier,
        label = { Text(label) },
        leadingIcon = {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(AssistChipDefaults.IconSize))
        },
        colors = AssistChipDefaults.assistChipColors(labelColor = tint),
    )
}
