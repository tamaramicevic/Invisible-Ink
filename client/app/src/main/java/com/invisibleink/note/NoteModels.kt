package com.invisibleink.note

import androidx.annotation.StringRes
import com.google.android.gms.maps.model.LatLng
import com.invisibleink.architecture.Destination
import com.invisibleink.architecture.ViewEvent
import com.invisibleink.architecture.ViewState
import org.joda.time.DateTime

data class NoteSeed(
    val title: String,
    val body: String,
    val imagePath: String? = null,
    val expiration: DateTime? = null,
    var location: LatLng? = null
)

sealed class NoteViewState : ViewState {
    object Empty : NoteViewState()
    data class ImageSelected(val imagePath: String) : NoteViewState()
    data class Draft(val draft: NoteSeed) : NoteViewState()
    data class Error(@StringRes val message: Int) : NoteViewState()
    data class Message(@StringRes val message: Int) : NoteViewState()
}

sealed class NoteViewEvent : ViewEvent {
    object AddImage : NoteViewEvent()
    data class Upload(val noteSeed: NoteSeed) : NoteViewEvent()
}

sealed class NoteDestination : Destination
