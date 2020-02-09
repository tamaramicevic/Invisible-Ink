package com.invisibleink.note

import com.invisibleink.architecture.Destination
import com.invisibleink.architecture.ViewEvent
import com.invisibleink.architecture.ViewState

sealed class NoteViewState : ViewState

sealed class NoteViewEvent : ViewEvent

sealed class NoteDestination : Destination
