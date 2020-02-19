package com.invisibleink.extensions

import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(@StringRes text: Int) =
    Snackbar.make(this, text, Snackbar.LENGTH_LONG).show()
