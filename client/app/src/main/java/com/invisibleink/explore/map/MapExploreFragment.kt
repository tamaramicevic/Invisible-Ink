package com.invisibleink.explore.map

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.MapView
import com.invisibleink.R
import com.invisibleink.architecture.ViewProvider
import com.invisibleink.extensions.findViewOrThrow
import com.invisibleink.injection.InvisibleInkApplication
import com.invisibleink.location.LocationFragment
import javax.inject.Inject

class MapExploreFragment : LocationFragment(), ViewProvider {

    @Inject
    lateinit var presenter: MapExplorePresenter
    private lateinit var viewDelegate: MapExploreViewDelegate

    override fun <T : View> findViewById(id: Int): T = findViewOrThrow(id)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map_explore, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as InvisibleInkApplication).appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDelegate = MapExploreViewDelegate(this)
        viewDelegate.mapView?.onCreate(savedInstanceState)
        presenter.locationProvider = this
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
