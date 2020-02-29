package com.invisibleink.note

import android.graphics.Bitmap
import androidx.annotation.StringRes
import com.google.android.gms.maps.model.LatLng
import com.invisibleink.architecture.Destination
import com.invisibleink.architecture.ViewEvent
import com.invisibleink.architecture.ViewState
import com.invisibleink.models.Note
import org.joda.time.DateTime

data class NoteContent(
    val title: String,
    val body: String,
    val image: Bitmap? = null,
    val expiration: DateTime? = null
) {
    fun createNote(location: LatLng) = Note(
        title = title,
        body = body,
        location = location,
        score = 0,
        expiration = expiration
    )
}

sealed class NoteViewState : ViewState {
    object Empty : NoteViewState()
    data class ImageSelected(val image: Bitmap) : NoteViewState()
    data class Draft(val draft: NoteContent) : NoteViewState()
    data class Error(@StringRes val message: Int) : NoteViewState()
}

sealed class NoteViewEvent : ViewEvent {
    object AddImage : NoteViewEvent()
    data class Upload(val noteContent: NoteContent) : NoteViewEvent()
}

sealed class NoteDestination : Destination
