package com.invisibleink.location

import com.google.android.gms.maps.model.LatLng

interface LocationProvider {

    fun getCurrentLocation(): LatLng?

    fun addLocationChangeListener(onLocationChangeCallback: (LatLng) -> Unit?)
}