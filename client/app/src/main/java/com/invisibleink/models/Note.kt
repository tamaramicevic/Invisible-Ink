package com.invisibleink.models

import com.google.android.gms.maps.model.LatLng
import org.joda.time.DateTime

data class Note(
    val title: String,
    val body: String,
    val location: LatLng,
    val score: Int = 0,
    val imageUrl: String? = null,
    val expiration: DateTime? = null
)