package com.invisibleink.architecture

/**
 * Top-level type representing an object that encapsulates the business logic associated with
 * a single screen. Delegates render tasks to an associated [ViewDelegate] and navigation tasks
 * to an associated, optional [Router].
 */
interface Presenter<TypeOfViewState : ViewState, TypeOfViewEvent : ViewEvent, TypeOfDestination : Destination> {

    /**
     * Create a reference to [ViewDelegate] instance for rendering [ViewState]s and listening
     * for [ViewEvent]s.
     */
    fun attach(
        viewDelegate: ViewDelegate<TypeOfViewState, TypeOfViewEvent, TypeOfDestination>,
        router: Router<TypeOfDestination>? = null
    )

    /**
     * Remove any references to the attached [ViewDelegate] and [Router].
     */
    fun detach()

    /**
     * Implement the logic for handling [ViewEvent]s.
     */
    fun onEvent(viewEvent: TypeOfViewEvent)

    /**
     * Relay [ViewState]s to be rendering to the associated [ViewDelegate].
     */
    fun pushState(viewState: TypeOfViewState)
}