package com.invisibleink.explore.map

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.MapView
import com.invisibleink.R
import com.invisibleink.architecture.Router
import com.invisibleink.architecture.ViewProvider
import com.invisibleink.dashboard.NavigationActivity
import com.invisibleink.dashboard.NavigationDestination
import com.invisibleink.explore.SearchFilter
import com.invisibleink.extensions.doNothingOnBackPress
import com.invisibleink.extensions.findViewOrThrow
import com.invisibleink.injection.InvisibleInkApplication
import com.invisibleink.location.LocationFragment
import javax.inject.Inject

class MapExploreFragment : LocationFragment(), ViewProvider, NavigationActivity.BackPressHandler {

    @Inject
    lateinit var presenter: MapExplorePresenter
    private lateinit var viewDelegate: MapExploreViewDelegate
    private var navigationRouter: Router<NavigationDestination>? = null
    private lateinit var searchFilter: SearchFilter

    override fun <T : View> findViewById(id: Int): T = findViewOrThrow(id)
    override fun onBackPress() = doNothingOnBackPress()

    companion object {
        private const val EXTRA_MAP_SEARCH_FILTER =
            "com.invisibleink.explore.map.extra_search_filter"

        fun constructBundle(searchFilter: SearchFilter = SearchFilter.EMPTY_FILTER) =
            Bundle().apply {
                putSerializable(EXTRA_MAP_SEARCH_FILTER, searchFilter)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map_explore, container, false)
        searchFilter = (arguments?.getSerializable(EXTRA_MAP_SEARCH_FILTER) as? SearchFilter)
            ?: SearchFilter.EMPTY_FILTER

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.refreshItem -> {
            presenter.onEvent(MapExploreViewEvent.RefreshNotes)
            true
        }
        R.id.arExploreItem -> {
            presenter.onEvent(MapExploreViewEvent.InitiateAr)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as InvisibleInkApplication).appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDelegate = MapExploreViewDelegate(this)
        viewDelegate.mapView?.onCreate(savedInstanceState)
        navigationRouter = requireActivity() as? Router<NavigationDestination>
        presenter.locationProvider = this
        presenter.router = navigationRouter
        presenter.attach(viewDelegate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detach()
    }

    /**
     * When using a [MapView] all life-cycle methods must be forwarded.
     * See: https://developers.google.com/android/reference/com/google/android/gms/maps/MapView
     */
    override fun onStart() {
        super.onStart()
        viewDelegate.mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        viewDelegate.mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewDelegate.mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        viewDelegate.mapView?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewDelegate.mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewDelegate.mapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        viewDelegate.mapView?.onLowMemory()
    }
}
