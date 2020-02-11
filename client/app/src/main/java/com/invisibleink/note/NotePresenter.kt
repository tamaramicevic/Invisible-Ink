package com.invisibleink.note

import com.invisibleink.architecture.BasePresenter
import javax.inject.Inject

class NotePresenter @Inject constructor() :
    BasePresenter<NoteViewState, NoteViewEvent, NoteDestination>() {
    override fun onEvent(viewEvent: NoteViewEvent): Unit? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
