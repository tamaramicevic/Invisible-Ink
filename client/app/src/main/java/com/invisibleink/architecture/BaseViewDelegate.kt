package com.invisibleink.architecture

import java.lang.ref.WeakReference

/**
 * Top-level view delegate type for all concrete [ViewDelegate]s to extend.
 * Handles attaching/detaching of the associated [Presenter] and relaying of [ViewEvent]s.
 */
abstract class BaseViewDelegate<TypeOfViewState : ViewState, TypeOfViewEvent : ViewEvent, TypeOfDestination : Destination> :
    ViewDelegate<TypeOfViewState, TypeOfViewEvent, TypeOfDestination> {

    private lateinit var presenter: WeakReference<Presenter<TypeOfViewState, TypeOfViewEvent, TypeOfDestination>>

    /**
     * Hooks for concrete view delegates to perform initialization/clean-up.
     */
    open fun onAttach() {}

    open fun onDetach() {}

    override fun pushEvent(viewEvent: TypeOfViewEvent) = presenter.get()?.onEvent(viewEvent)

    final override fun attach(presenter: Presenter<TypeOfViewState, TypeOfViewEvent, TypeOfDestination>) {
        this.presenter = WeakReference(presenter)

        onAttach()
    }

    final override fun detach() {
        onDetach()
        presenter.clear()
    }
}
