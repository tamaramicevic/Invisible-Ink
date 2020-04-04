package com.invisibleink.explore.map

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Spinner
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
import com.invisibleink.explore.PrebuiltOptions
import com.invisibleink.explore.SearchFilter
import com.invisibleink.extensions.showSnackbarWithRetryAction
import com.invisibleink.models.Note


class MapExploreViewDelegate(viewProvider: ViewProvider) :
    BaseViewDelegate<MapExploreViewState, MapExploreViewEvent, MapExploreDestination>(viewProvider) {

    companion object {
        private const val MAP_BOUNDS_PADDING = 128
    }

    val mapView: MapView? = viewProvider.findViewById(R.id.exploreMapView)
    private val searchButton: ImageButton = viewProvider.findViewById(R.id.searchButton)
    private val searchKeywords: EditText = viewProvider.findViewById(R.id.searchKeywords)
    private val searchOptionsToggle: ImageButton =
        viewProvider.findViewById(R.id.optionsToggleButton)
    private val searchOptions: ViewGroup = viewProvider.findViewById(R.id.searchOptions)
    private val searchImageOptions: Spinner = viewProvider.findViewById(R.id.imageFilterOption)
    private val searchLimitOptions: Spinner = viewProvider.findViewById(R.id.limitFilterOption)
    private val searchRankingOptions: Spinner = viewProvider.findViewById(R.id.rankingFilterOption)
    private val loadingSpinner: ProgressBar = viewProvider.findViewById(R.id.exploreMapProgressBar)
    private var map: GoogleMap? = null
    private val context: Context = searchButton.context

    init {
        mapView?.getMapAsync {
            map = it
            pushEvent(MapExploreViewEvent.FetchNotes)
        }
        searchButton.setOnClickListener {
            pushEvent(
                MapExploreViewEvent.SearchNotes(extractFilter())
            )
        }

        searchOptions.visibility = View.GONE
        searchOptionsToggle.setOnClickListener {
            val optionsVisible = searchOptions.visibility == View.VISIBLE
            val (toggledVisibility, toggledResource) = if (optionsVisible) {
                View.GONE to R.drawable.ic_arrow_drop_down_black_24dp
            } else {
                View.VISIBLE to R.drawable.ic_arrow_drop_up_black_24dp
            }
            searchOptions.visibility = toggledVisibility
            searchOptionsToggle.setImageResource(toggledResource)
        }
    }

    override fun render(viewState: MapExploreViewState): Unit? = when (viewState) {
        is MapExploreViewState.Error -> showErrorMessageWithRetry(viewState.message)
        is MapExploreViewState.NoteUpdate -> showNotes(viewState.deviceLocation, viewState.notes)
        is MapExploreViewState.ExtractFilter -> pushEvent(MapExploreViewEvent.FilterExtracted(extractFilter()))
        is MapExploreViewState.DeviceLocationUpdate -> showNotes(
            viewState.deviceLocation,
            viewState.notes,
            false
        )
        is MapExploreViewState.Loading -> showLoading(true)
    }

    private fun showLoading(isLoading: Boolean) {
        loadingSpinner.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    private fun showErrorMessageWithRetry(@StringRes message: Int) {
        mapView?.showSnackbarWithRetryAction(message) {
            pushEvent(MapExploreViewEvent.FetchNotes)
        }
        showLoading(false)
    }

    private fun showNotes(
        deviceLocation: LatLng,
        notes: List<Note>,
        moveCameraToBounds: Boolean = true
    ) {
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

            if (moveCameraToBounds) {
                moveCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        boundsBuilder.build(),
                        MAP_BOUNDS_PADDING
                    )
                )
            }
        }
    }

    private fun extractFilter() = SearchFilter(
        keywords = parseQuery(searchKeywords.text.toString()),
        withImage = parseImageOptions(searchImageOptions.selectedItem.toString()),
        limit = parseLimitOptions(searchLimitOptions.selectedItem.toString()),
        options = parseRankOptions(searchRankingOptions.selectedItem.toString())
    )

    private fun parseQuery(keywords: String?): String? = when (keywords) {
        "" -> null
        else -> keywords
    }

    private fun parseImageOptions(selectedItem: String): Boolean? = when (selectedItem) {
        context.getString(R.string.image_options_with_image) -> true
        context.getString(R.string.image_options_without_image) -> false
        else -> null
    }

    private fun parseRankOptions(selectedItem: String): PrebuiltOptions? = when (selectedItem) {
        context.getString(R.string.rank_options_newest) -> PrebuiltOptions.NEWEST
        context.getString(R.string.rank_options_best) -> PrebuiltOptions.BEST
        context.getString(R.string.rank_options_worst) -> PrebuiltOptions.WORST
        else -> null
    }

    private fun parseLimitOptions(selectedItem: String): Int? =
        selectedItem.split(" ").first().toIntOrNull()
}
