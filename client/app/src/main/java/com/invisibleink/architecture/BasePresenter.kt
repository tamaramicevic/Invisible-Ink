package com.invisibleink.architecture

import java.lang.ref.WeakReference

/**
 * Top-level presenter type for all concrete [Presenter]s to extend.
 * Handles attaching/detaching of the associated [ViewDelegate] and [Router].
 */
abstract class BasePresenter<TypeOfViewState : ViewState, TypeOfViewEvent : ViewEvent, TypeOfDestination : Destination> :
    Presenter<TypeOfViewState, TypeOfViewEvent, TypeOfDestination> {

    private lateinit var viewDelegate: WeakReference<ViewDelegate<TypeOfViewState, TypeOfViewEvent, TypeOfDestination>>
    private var router: Router<TypeOfDestination>? = null

    /**
     * Hooks for concrete presenters to perform initialization/clean-up.
     */
    open fun onAttach() {}

    open fun onDetach() {}

    final override fun attach(
        viewDelegate: ViewDelegate<TypeOfViewState, TypeOfViewEvent, TypeOfDestination>,
        router: Router<TypeOfDestination>?
    ) {
        this.viewDelegate = WeakReference(viewDelegate)

        if (router != null) {
            this.router = router
        }

        onAttach()
    }

    final override fun detach() {
        onDetach()

        viewDelegate.get()?.detach()
        viewDelegate.clear()
        router = null
    }

    override fun pushState(viewState: TypeOfViewState) = viewDelegate.get()?.render(viewState)
}
