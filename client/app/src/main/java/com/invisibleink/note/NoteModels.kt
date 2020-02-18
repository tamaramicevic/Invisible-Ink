package com.invisibleink.note

import android.graphics.Bitmap
import androidx.annotation.StringRes
import com.invisibleink.architecture.Destination
import com.invisibleink.architecture.ViewEvent
import com.invisibleink.architecture.ViewState
import org.joda.time.DateTime

data class Note(
    val title: String,
    val body: String,
    val image: Bitmap? = null,
    val expiration: DateTime? = null
)

sealed class NoteViewState : ViewState {
    object Empty : NoteViewState()
    data class Draft(val draft: Note) : NoteViewState()
    data class InvalidNote(@StringRes val message: Int) : NoteViewState()
    data class UploadError(@StringRes val message: Int) : NoteViewState()
    data class UploadSuccess(@StringRes val message: Int) : NoteViewState()
}

sealed class NoteViewEvent : ViewEvent {
    data class Upload(val note: Note) : NoteViewEvent()
}

sealed class NoteDestination : Destination
