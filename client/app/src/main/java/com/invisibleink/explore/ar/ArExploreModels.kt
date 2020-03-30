package com.invisibleink.explore.ar

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

sealed class ArExploreViewState : ViewState {
    object Loading : ArExploreViewState()
    data class Success(val deviceLocation: LatLng, val notes: List<Note>) : ArExploreViewState()
    data class Error(@StringRes val message: Int) : ArExploreViewState()
}

sealed class ArExploreViewEvent : ViewEvent {
    object FetchNotes : ArExploreViewEvent()
    data class UpvoteNote(val noteId: String) : ArExploreViewEvent()
    data class DownvoteNote(val noteId: String) : ArExploreViewEvent()
}

sealed class ArExploreDestination : Destination
