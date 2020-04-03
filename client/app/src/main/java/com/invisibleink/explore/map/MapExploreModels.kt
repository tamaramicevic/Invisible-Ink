package com.invisibleink.explore.map

import androidx.annotation.StringRes
import com.google.android.gms.maps.model.LatLng
import com.invisibleink.architecture.Destination
import com.invisibleink.architecture.ViewEvent
import com.invisibleink.architecture.ViewState
import com.invisibleink.explore.SearchFilter
import com.invisibleink.models.Note

data class FetchNotesRequest(
    val location: LatLng,
    val filter: SearchFilter?
)

sealed class MapExploreViewState : ViewState {
    object Loading : MapExploreViewState()
    object ExtractFilter : MapExploreViewState()
    data class NoteUpdate(val deviceLocation: LatLng, val notes: List<Note>) : MapExploreViewState()
    data class DeviceLocationUpdate(
        val deviceLocation: LatLng,
        val notes: List<Note>
    ) : MapExploreViewState()

    data class Error(@StringRes val message: Int) : MapExploreViewState()
}

sealed class MapExploreViewEvent : ViewEvent {
    object FetchNotes : MapExploreViewEvent()
    object RefreshNotes : MapExploreViewEvent()
    object InitiateAr : MapExploreViewEvent()
    data class FilterExtracted(val searchFilter: SearchFilter) : MapExploreViewEvent()
    data class SearchNotes(val searchFilter: SearchFilter) : MapExploreViewEvent()
}

sealed class MapExploreDestination : Destination
