package com.invisibleink.report

import android.content.Context
import com.google.gson.annotations.SerializedName
import com.invisibleink.R
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Contains methods for reporting notes.
 */
interface ReportApi {

    @POST("report")
    fun reportNote(@Body reportRequest: ReportRequest): Observable<ReportResponse>
}

data class ReportResponse(val success: Boolean, val error: String?)

enum class ReportType {
    HARASSMENT, VIOLENCE, SEXUAL_CONTENT;

    fun asString(context: Context): String {
        val stringRes = when (this) {
            HARASSMENT -> R.string.report_type_harassment
            VIOLENCE -> R.string.report_type_violence
            SEXUAL_CONTENT -> R.string.report_type_sexual
        }
        return context.getString(stringRes)
    }
}

data class ReportRequest(
    @SerializedName("ReportType")
    val reportType: ReportType,
    @SerializedName("NoteId")
    val noteId: String,
    @SerializedName("Comment")
    val comment: String?
)