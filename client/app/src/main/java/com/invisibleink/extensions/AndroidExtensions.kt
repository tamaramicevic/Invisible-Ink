package com.invisibleink.extensions

import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.invisibleink.architecture.ViewProvider

/**
 * Extension function for implementing the [ViewProvider] interface in fragments.
 */
fun <T : View> Fragment.findViewOrThrow(@IdRes id: Int): T {
    val validView = view ?: throw IllegalStateException(
        "View must be instantiated before calling findViewById"
    )
    return validView.findViewById(id) ?: throw IllegalStateException()
}

fun View.showSnackbar(@StringRes text: Int) =
    Snackbar.make(this, text, Snackbar.LENGTH_LONG).show()
