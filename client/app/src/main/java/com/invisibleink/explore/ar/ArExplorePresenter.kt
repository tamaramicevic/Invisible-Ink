package com.invisibleink.explore.ar

import com.invisibleink.architecture.BasePresenter
import retrofit2.Retrofit
import javax.inject.Inject

class ArExplorePresenter @Inject constructor(
    private val retrofit: Retrofit
) :
    BasePresenter<ArExploreViewState, ArExploreViewEvent, ArExploreDestination>() {
    override fun onEvent(viewEvent: ArExploreViewEvent): Unit? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
