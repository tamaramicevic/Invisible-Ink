package com.invisibleink.architecture

import android.view.View
import androidx.annotation.IdRes

/**
 * Interface for [ViewDelegate]s to bind to views.
 */
interface ViewProvider {
    fun <T : View> findViewById(@IdRes id: Int): T
}
