package com.invisibleink.explore.ar

import androidx.annotation.StringRes
import com.google.android.gms.maps.model.LatLng
import com.invisibleink.architecture.Destination
import com.invisibleink.architecture.ViewEvent
import com.invisibleink.architecture.ViewState
import com.invisibleink.explore.SearchFilter
import com.invisibleink.models.Note
import com.invisibleink.report.ReportType
import java.io.Serializable

data class FetchNotesRequest(
    val location: LatLng,
    val filter: SearchFilter?
)

sealed class ArExploreViewState : ViewState {
    data class Success(val deviceLocation: LatLng, val notes: List<Note>) : ArExploreViewState()
    data class Message(@StringRes val message: Int) : ArExploreViewState()
}

sealed class ArExploreViewEvent : ViewEvent {
    object FetchNotes : ArExploreViewEvent()
    data class UpvoteNote(val noteId: String) : ArExploreViewEvent()
    data class DownvoteNote(val noteId: String) : ArExploreViewEvent()
    data class ReportNote(
        val noteId: String,
        val reportType: ReportType,
        val reportComment: String
    ) : ArExploreViewEvent()
}

sealed class ArExploreDestination : Destination
