package com.invisibleink.note

import com.invisibleink.architecture.BasePresenter
import javax.inject.Inject

class NotePresenter @Inject constructor() :
    BasePresenter<NoteViewState, NoteViewEvent, NoteDestination>() {
    override fun onEvent(viewEvent: NoteViewEvent): Unit? = when (viewEvent) {
        is NoteViewEvent.Upload -> uploadNote(viewEvent.note)
    }

    override fun onAttach() {
        pushState(NoteViewState.Empty)
    }

    private fun uploadNote(note: Note) = Unit // TODO: Validate and then upload
}
