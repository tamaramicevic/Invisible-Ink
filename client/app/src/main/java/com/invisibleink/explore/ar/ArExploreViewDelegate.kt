package com.invisibleink.explore.ar

import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.gms.maps.model.LatLng
import com.invisibleink.R
import com.invisibleink.architecture.BaseViewDelegate
import com.invisibleink.architecture.ViewProvider
import com.invisibleink.extensions.showSnackbar
import com.invisibleink.models.Note

class ArExploreViewDelegate(viewProvider: ViewProvider) :
    BaseViewDelegate<ArExploreViewState, ArExploreViewEvent, ArExploreDestination>(viewProvider) {

    lateinit var arFragment : ArExploreFragment

    override fun render(viewState: ArExploreViewState): Unit? = when (viewState) {
        is ArExploreViewState.Loading -> showMessage()
        is ArExploreViewState.Message -> showMessage(viewState.message)
        is ArExploreViewState.Success -> showNotes(viewState.deviceLocation, viewState.notes)
    }

    private fun showMessage(@StringRes message: Int = R.string.loading)  {
        arFragment.view?.showSnackbar(message)
    }

    private fun showNotes(deviceLocation: LatLng, notes: List<Note>) {
        arFragment.showNotes(deviceLocation, notes)
    }
}
