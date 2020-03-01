package com.invisibleink.location

import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.invisibleink.permissions.onLocationPermissionGranted
import com.invisibleink.permissions.requireLocationPermission

/**
 * Abstract fragment that caches updates from the device's [FusedLocationProviderClient]
 * to keep track of the "current" location.
 */
abstract class LocationFragment : Fragment(), LocationProvider {

    companion object {
        private const val REQUEST_LOCATION = 0
    }

    private lateinit var locationProvider: FusedLocationProviderClient
    private var lastLocation: LatLng? = null

    /**
     * Hook for subclasses to perform an action once location permissions are
     * checked and granted.
     */
    open fun onLocationPermissionGranted() {}

    override fun getCurrentLocation(): LatLng? = lastLocation

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
    }
}
