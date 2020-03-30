package com.invisibleink.report

import io.reactivex.Observable
import retrofit2.Retrofit
import javax.inject.Inject

enum class ReportResult {
    SUCCESS, FAILURE, DUPLICATE
}

class ReportGateway @Inject constructor(
    retrofit: Retrofit
) {

    private val reportApi = retrofit.create(ReportApi::class.java)

    fun reportNote(
        noteId: String,
        reportType: ReportType,
        comment: String?
    ): Observable<ReportResult> =
        sendReport(ReportRequest(reportType, noteId, comment))

    private fun sendReport(reportRequest: ReportRequest): Observable<ReportResult> {
        return reportApi.reportNote(reportRequest)
            .map {
                if (it.success) {
                    ReportResult.SUCCESS
                } else {
                    ReportResult.FAILURE
                }
            }
    }
}