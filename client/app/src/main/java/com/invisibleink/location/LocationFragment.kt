package com.invisibleink.location

import android.location.Location
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.invisibleink.permissions.onLocationPermissionGranted
import com.invisibleink.permissions.requireLocationPermission

/**
 * Abstract fragment that provides a wrapper around the Android [FusedLocationProviderClient]
 * service.
 *
 * Extend this fragment if your fragment is interested in:
 *
 *   - Getting the most recent device location, see [getCurrentLocation].
 *   - Easy verification of location permissions, see [onLocationPermissionGranted].
 *   - Receive location updates whenever the device moves, see [addLocationChangeListener].
 */
abstract class LocationFragment : Fragment(), LocationProvider {

    companion object {
        private const val REQUEST_LOCATION = 0
    }

    /**
     * Override this hook to implement custom behavior when location permissions are checked and
     * granted after the fragment is created.
     */
    open fun onLocationPermissionGranted() {}

    /**
     * Retrieve the device's most recently known location, if available.
     */
    override fun getCurrentLocation(): LatLng? = lastLocation

    /**
     * Receive a callback, via [onLocationChangeCallback], whenever the Android system detects
     * that the device location has changed.
     */
    override fun addLocationChangeListener(onLocationChangeCallback: (LatLng) -> Unit?) {
        locationChangedListener = onLocationChangeCallback
    }

    private lateinit var locationProvider: FusedLocationProviderClient
    private var locationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private var locationChangedListener: ((LatLng) -> Unit?)? = null
    private var lastLocation: LatLng? = null

    // Forward location changes to the optional locationChangedListener
    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            val lastLatLng =
                LatLng(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude)
            this@LocationFragment.lastLocation = lastLatLng
            locationChangedListener?.invoke(lastLatLng)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireLocationPermission(REQUEST_LOCATION, ::setUpLocationListener)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION -> {
                grantResults.onLocationPermissionGranted(this::setUpLocationListener)
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setUpLocationListener() {
        locationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationProvider.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                lastLocation = LatLng(location.latitude, location.longitude)
            }
            onLocationPermissionGranted()
        }
        locationProvider.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
}
