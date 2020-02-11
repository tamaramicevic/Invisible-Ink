package com.invisibleink.explore

import com.invisibleink.architecture.BasePresenter
import javax.inject.Inject

class ExplorePresenter @Inject constructor() :
    BasePresenter<ExploreViewState, ExploreViewEvent, ExploreDestination>() {
    override fun onEvent(viewEvent: ExploreViewEvent): Unit? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
