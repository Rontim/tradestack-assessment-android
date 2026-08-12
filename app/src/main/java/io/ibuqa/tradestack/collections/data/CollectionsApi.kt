package io.ibuqa.tradestack.collections.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class OutletDto(
    val outlet_code: String,
    val outlet_name: String,
    val route_code: String?,
)

data class OutletListDto(val results: List<OutletDto>)

data class ReceiptDto(
    val client_uuid: String,
    val outlet_code: String,
    val invoice_no: String,
    val method: String,
    val amount_kes: Double,
    val receipt_ref: String,
    val recorded_at: String,
)

data class BatchDto(val device_id: String, val receipts: List<ReceiptDto>)

data class ReceiptResultDto(
    val client_uuid: String?,
    val status: String,
    val reason: String?,
)

data class BatchResultDto(val results: List<ReceiptResultDto>)

interface CollectionsApi {

    @GET("api/v1/outlets/")
    suspend fun outlets(): OutletListDto

    /**
     * The server answers 207 with a per-receipt outcome. It may also answer
     * 503, and it may take longer than you expect.
     *
     * TODO(candidate): a Response wrapper is used here rather than the bare
     *  body so you can see the status code. Whether that is the right shape
     *  for your call site is up to you.
     */
    @POST("api/v1/collections/batch/")
    suspend fun pushBatch(@Body body: BatchDto): Response<BatchResultDto>
}
