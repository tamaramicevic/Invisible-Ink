package com.invisibleink.explore.ar

import androidx.annotation.StringRes
import com.google.android.gms.maps.model.LatLng
import com.invisibleink.architecture.Destination
import com.invisibleink.architecture.ViewEvent
import com.invisibleink.architecture.ViewState
import com.invisibleink.models.Note

sealed class ArExploreViewState : ViewState {
    object Loading: ArExploreViewState()
    data class Success(val deviceLocation: LatLng, val notes: List<Note>): ArExploreViewState()
    data class Error(@StringRes val message: Int) : ArExploreViewState()
}

sealed class ArExploreViewEvent : ViewEvent {
    object FetchNotes: ArExploreViewEvent()
}

sealed class ArExploreDestination : Destination
