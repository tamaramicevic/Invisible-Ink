package com.invisibleink.explore.map

import androidx.annotation.StringRes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.invisibleink.R
import com.invisibleink.architecture.BaseViewDelegate
import com.invisibleink.architecture.ViewProvider
import com.invisibleink.extensions.showSnackbar
import com.invisibleink.models.Note


class MapExploreViewDelegate(viewProvider: ViewProvider) :
    BaseViewDelegate<MapExploreViewState, MapExploreViewEvent, MapExploreDestination>(viewProvider) {

    companion object {
        private const val MAP_BOUNDS_PADDING = 128
    }

    val mapView: MapView? = viewProvider.findViewById(R.id.exploreMapView)
    private lateinit var map: GoogleMap

    init {
        mapView?.getMapAsync {
            map = it
            val quad = LatLng(53.527290, -113.527823)
            map.addMarker(MarkerOptions().position(quad).title("Marker Title").snippet("Marker Description"))
            map.moveCamera(CameraUpdateFactory.newLatLng(quad))
            pushEvent(MapExploreViewEvent.FetchNotes)
        }
    }

    override fun render(viewState: MapExploreViewState): Unit? = when (viewState) {
        is MapExploreViewState.Error -> showMessage(viewState.message)
        is MapExploreViewState.Success -> showNotes(viewState.notes)
    }

    private fun showNotes(notes: List<Note>) {
        val boundsBuilder = LatLngBounds.Builder()
        notes.forEach { note ->
            map.addMarker(MarkerOptions().position(note.location))
            boundsBuilder.include(note.location)
        }
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), MAP_BOUNDS_PADDING))
    }

    private fun showMessage(@StringRes message: Int) = mapView?.showSnackbar(message)
}
