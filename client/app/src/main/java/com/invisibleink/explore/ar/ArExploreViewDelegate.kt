package com.invisibleink.explore.ar

import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.gms.maps.model.LatLng
import com.invisibleink.architecture.BaseViewDelegate
import com.invisibleink.architecture.ViewProvider
import com.invisibleink.models.Note

class ArExploreViewDelegate(viewProvider: ViewProvider) :
    BaseViewDelegate<ArExploreViewState, ArExploreViewEvent, ArExploreDestination>(viewProvider) {

    lateinit var arFragment : ArExploreFragment

    override fun render(viewState: ArExploreViewState): Unit? = when (viewState) {
        is ArExploreViewState.Message -> showMessage(viewState.message)
        is ArExploreViewState.Success -> showNotes(viewState.deviceLocation, viewState.notes)
        is ArExploreViewState.Loading -> showLoading(true)
    }

    private fun showLoading(isLoading: Boolean) {
        Log.i("RenderingTest", "Loading...")
    }

    private fun showMessage(@StringRes message: Int)  {
        Log.i("ReportingTest", "Error...")
        Toast.makeText(
            arFragment.context,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showNotes(deviceLocation: LatLng, notes: List<Note>) {
        arFragment.showNotes(deviceLocation, notes)
    }
}
