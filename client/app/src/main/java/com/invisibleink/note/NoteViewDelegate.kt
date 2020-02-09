package com.invisibleink.note

import com.invisibleink.architecture.BaseViewDelegate
import com.invisibleink.architecture.ViewProvider

class NoteViewDelegate(viewProvider: ViewProvider) :
    BaseViewDelegate<NoteViewState, NoteViewEvent, NoteDestination>(viewProvider) {
    override fun render(viewState: NoteViewState): Unit? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
