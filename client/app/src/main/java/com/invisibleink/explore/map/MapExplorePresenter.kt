package com.invisibleink.explore.map

import com.invisibleink.architecture.BasePresenter
import retrofit2.Retrofit
import javax.inject.Inject

class MapExplorePresenter @Inject constructor(
    private val retrofit: Retrofit
) :
    BasePresenter<MapExploreViewState, MapExploreViewEvent, MapExploreDestination>() {
    override fun onEvent(viewEvent: MapExploreViewEvent): Unit? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
