package com.invisibleink.explore.ar

import android.util.Log
import androidx.annotation.StringRes
import com.google.android.gms.maps.model.LatLng
import com.invisibleink.architecture.BaseViewDelegate
import com.invisibleink.architecture.ViewProvider
import com.invisibleink.models.Note

class ArExploreViewDelegate(viewProvider: ViewProvider) :
    BaseViewDelegate<ArExploreViewState, ArExploreViewEvent, ArExploreDestination>(viewProvider) {

    lateinit var arFragment : ArExploreFragment

    override fun render(viewState: ArExploreViewState): Unit? = when (viewState) {
        is ArExploreViewState.Error -> showMessage(viewState.message)
        is ArExploreViewState.Success -> showNotes(viewState.deviceLocation, viewState.notes)
        is ArExploreViewState.Loading -> showLoading(true)
    }

    private fun showLoading(isLoading: Boolean) {
        Log.i("RenderingTest", "Loading...")
    }

    private fun showMessage(@StringRes message: Int) /** = arView?.showSnackbar(message) **/ {
        Log.i("RenderingTest", "Error...")
    }

    private fun showNotes(deviceLocation: LatLng, notes: List<Note>) {
        arFragment.showNotes(deviceLocation, notes)
    }
}
