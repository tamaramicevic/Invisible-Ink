package com.invisibleink.architecture

/**
 * Top-level type representing an object that is capable of rendering a screen within the app.
 * Knows how to render a particular set of [ViewState]s and listen for a particular set of
 * [ViewEvent]. Requires an associated [Presenter].
 */
interface ViewDelegate<TypeOfViewState : ViewState, TypeOfViewEvent : ViewEvent, TypeOfDestination : Destination> {

    /**
     * Render a view state on screen.
     */
    fun render(viewState: TypeOfViewState): Unit?

    /**
     * Relay an event to the associated [Presenter].
     */
    fun pushEvent(viewEvent: TypeOfViewEvent): Unit?

    /**
     * Attach the associated presenter to this [ViewDelegate] instance.
     */
    fun attach(presenter: Presenter<TypeOfViewState, TypeOfViewEvent, TypeOfDestination>): Unit?

    /**
     * Notifies this [ViewDelegate] that it is being detached from the associated presenter.
     */
    fun detach(): Unit?
}