package com.invisibleink.explore.map

import androidx.annotation.StringRes
import com.google.android.gms.maps.model.LatLng
import com.invisibleink.architecture.Destination
import com.invisibleink.architecture.ViewEvent
import com.invisibleink.architecture.ViewState
import com.invisibleink.models.Note

enum class PrebuiltOptions {
    BEST, WORST, NEWEST;
}

data class SearchFilter(
    val keywords: String? = null,
    val limit: Int? = null,
    val withImage: Boolean? = null,
    val options: PrebuiltOptions? = null
) {
    companion object {
        val EMPTY_FILTER = SearchFilter()
    }
}

data class FetchNotesRequest(
    val location: LatLng,
    val filter: SearchFilter?
)

sealed class MapExploreViewState : ViewState {
    object Loading : MapExploreViewState()
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
    data class SearchNotes(
        val query: String?,
        val limit: Int? = null,
        val withImage: Boolean? = null,
        val options: PrebuiltOptions? = null
    ) : MapExploreViewEvent()
}

sealed class MapExploreDestination : Destination
