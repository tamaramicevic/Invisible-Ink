package com.invisibleink.explore.map

import androidx.annotation.StringRes
import com.google.android.gms.maps.model.LatLng
import com.invisibleink.architecture.Destination
import com.invisibleink.architecture.ViewEvent
import com.invisibleink.architecture.ViewState
import com.invisibleink.models.Note

sealed class MapExploreViewState : ViewState {
    data class Success(val deviceLocation: LatLng, val notes: List<Note>): MapExploreViewState()
    data class Error(@StringRes val message: Int) : MapExploreViewState()
}

sealed class MapExploreViewEvent : ViewEvent {
    object FetchNotes: MapExploreViewEvent()
}

sealed class MapExploreDestination : Destination
