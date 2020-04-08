package com.invisibleink.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Software Requirements Specification Coverage
 *  - Covers System Feature 4.4, Functional Requirement: AR-GPS-Permissions
 */

// Require permissions
private const val LOCATION_PERMISSION = android.Manifest.permission.ACCESS_FINE_LOCATION
private const val PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED

fun Context?.hasLocationPermission(): Boolean = this?.let {
    ContextCompat.checkSelfPermission(this, LOCATION_PERMISSION) == PERMISSION_GRANTED
} ?: false

fun Fragment.requireLocationPermission(requestCode: Int, alreadyGranted: () -> Unit?) {
    if (context.hasLocationPermission()) {
        alreadyGranted.invoke()
    } else {
        requestLocationPermission(requestCode)
    }
}

fun Fragment.requestLocationPermission(requestCode: Int) {
    if (!context.hasLocationPermission()) {
        this.requestPermissions(arrayOf(LOCATION_PERMISSION), requestCode)
    }
}

fun IntArray.onLocationPermissionGranted(
    onLocationGranted: () -> Unit?,
    otherwise: (() -> Unit?)? = null
) {
    if (isGrantLocationPermission()) {
        onLocationGranted.invoke()
    } else {
        otherwise?.invoke()
    }
}

private fun IntArray.isGrantLocationPermission() = isNotEmpty() && get(0) == PERMISSION_GRANTED