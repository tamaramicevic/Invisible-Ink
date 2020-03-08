package com.invisibleink.explore.map

import android.view.View
import android.widget.ProgressBar
import androidx.annotation.StringRes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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
    private val loadingSpinner: ProgressBar = viewProvider.findViewById(R.id.exploreMapProgressBar)
    private var map: GoogleMap? = null

    init {
        mapView?.getMapAsync { map = it }
    }

    override fun render(viewState: MapExploreViewState): Unit? = when (viewState) {
        is MapExploreViewState.Error -> showMessage(viewState.message)
        is MapExploreViewState.Success -> showNotes(viewState.deviceLocation, viewState.notes)
        is MapExploreViewState.Loading -> showLoading(true)
    }

    private fun showLoading(isLoading: Boolean) {
        loadingSpinner.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    private fun showMessage(@StringRes message: Int) = mapView?.showSnackbar(message)

    private fun showNotes(deviceLocation: LatLng, notes: List<Note>) {
        map?.apply {
            showLoading(false)
            clear()

            val boundsBuilder = LatLngBounds.Builder().include(deviceLocation)
            val locations = notes
                .map { MarkerOptions().position(it.location) }
                .toMutableList()
                .also { markers ->
                    markers.add(
                        MarkerOptions()
                            .position(deviceLocation)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    )
                }

            locations.forEach { marker ->
                addMarker(marker)
                boundsBuilder.include(marker.position)
            }

            moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    boundsBuilder.build(),
                    MAP_BOUNDS_PADDING
                )
            )
        }
    }
}
