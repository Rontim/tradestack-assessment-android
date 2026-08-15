package io.ibuqa.tradestack.collections.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.ibuqa.tradestack.collections.data.SyncRepository

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val repo: SyncRepository,
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result =
        if (repo.syncPending().isSuccess) Result.success() else Result.retry()
}