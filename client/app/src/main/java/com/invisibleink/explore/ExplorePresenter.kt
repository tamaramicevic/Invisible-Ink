package com.invisibleink.explore

import com.invisibleink.architecture.BasePresenter
import retrofit2.Retrofit
import javax.inject.Inject

class ExplorePresenter @Inject constructor(
    private val retrofit: Retrofit
) :
    BasePresenter<ExploreViewState, ExploreViewEvent, ExploreDestination>() {
    override fun onEvent(viewEvent: ExploreViewEvent): Unit? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
