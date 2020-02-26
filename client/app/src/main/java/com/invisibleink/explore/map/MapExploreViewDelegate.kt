package com.invisibleink.explore.map

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.invisibleink.R
import com.invisibleink.architecture.BaseViewDelegate
import com.invisibleink.architecture.ViewProvider


class MapExploreViewDelegate(viewProvider: ViewProvider) :
    BaseViewDelegate<MapExploreViewState, MapExploreViewEvent, MapExploreDestination>(viewProvider) {

    val mapView: MapView? = viewProvider.findViewById(R.id.exploreMapView)
    private lateinit var map: GoogleMap

    init {
        mapView?.getMapAsync {
            map = it
            val quad = LatLng(53.527290, -113.527823)
            map.addMarker(MarkerOptions().position(quad).title("Marker Title").snippet("Marker Description"))
            map.moveCamera(CameraUpdateFactory.newLatLng(quad))
        }
    }

    override fun render(viewState: MapExploreViewState): Unit? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
