package com.invisibleink.models

import com.google.android.gms.maps.model.LatLng
import org.joda.time.DateTime

data class Note(
    val id: String? = null,
    val title: String,
    val body: String,
    val expiration: DateTime? = null,
    val imageUrl: String? = null,
    val location: LatLng,
    val score: Int = 0
)