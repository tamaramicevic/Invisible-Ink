package com.invisibleink.extensions

import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import com.invisibleink.R

fun View.showSnackbar(@StringRes text: Int) =
    Snackbar.make(this, text, Snackbar.LENGTH_LONG).show()

fun View.showSnackbar(text: String) =
    Snackbar.make(this, text, Snackbar.LENGTH_LONG).show()

fun View.showSnackbarWithRetryAction(@StringRes text: Int, retryAction: () -> Unit?) {
    Snackbar.make(this, text, Snackbar.LENGTH_LONG)
        .setAction(R.string.retry) { retryAction.invoke() }
        .show()
}