package io.ibuqa.tradestack.collections.data

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromSyncState(value: SyncState): String = value.name

    @TypeConverter
    fun toSyncState(value: String): SyncState = SyncState.valueOf(value)
}