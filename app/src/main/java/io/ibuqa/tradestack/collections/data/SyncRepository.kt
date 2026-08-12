package io.ibuqa.tradestack.collections.data

import javax.inject.Inject
import javax.inject.Singleton

/**
 * The seam between local storage and the server.
 *
 * TODO(candidate): this is the centre of the exercise. Decide what "sync"
 *  means here, what it does when the network is gone, what it does when the
 *  server answers 503, and what it does when the app is killed after the
 *  request left the handset but before the response came back.
 *
 *  Whether you drive it from WorkManager, a coroutine scope, or a button is
 *  your call. WorkManager is already on the classpath.
 */
@Singleton
class SyncRepository @Inject constructor(
    private val dao: CollectionDao,
    private val api: CollectionsApi,
) {
    suspend fun syncPending(): Result<Unit> {
        TODO("candidate: push pending collections to the server")
    }
}
