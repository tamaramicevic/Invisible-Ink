package com.invisibleink.explore.ar

import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.gms.maps.model.LatLng
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.rendering.ViewRenderable
import com.invisibleink.R
import com.invisibleink.architecture.BaseViewDelegate
import com.invisibleink.architecture.ViewProvider
import com.invisibleink.extensions.showSnackbar
import com.invisibleink.models.Note

class ArExploreViewDelegate(viewProvider: ViewProvider) :
    BaseViewDelegate<ArExploreViewState, ArExploreViewEvent, ArExploreDestination>(viewProvider) {

    lateinit var arView : ArSceneView
    private var renderable: ViewRenderable? = null

    override fun render(viewState: ArExploreViewState): Unit? = when (viewState) {
        is ArExploreViewState.Error -> showMessage(viewState.message)
        is ArExploreViewState.Success -> showNotes(viewState.deviceLocation, viewState.notes)
        is ArExploreViewState.Loading -> showLoading(true)
    }

    private fun showLoading(isLoading: Boolean) {
    }

    private fun showMessage(@StringRes message: Int) = arView?.showSnackbar(message)

    private fun showNotes(deviceLocation: LatLng, notes: List<Note>) {
//        ViewRenderable.builder()
//            .setView(arView.context, R.layout.fragment_ar_explore).build()
//            .thenAcceptAsync { renderable ->
//
//                // checks if buttons work correctly
//                renderable.view.findViewById<ImageButton>(R.id.noteReport).setOnClickListener {
//                    Toast.makeText(arView.context, "Report Note!", Toast.LENGTH_LONG).show()
//                }
//
//                renderable.view.findViewById<ImageButton>(R.id.noteUpvote).setOnClickListener {
//                    Toast.makeText(arView.context, "Upvote Note!", Toast.LENGTH_LONG).show()
//                }
//
//                renderable.view.findViewById<ImageButton>(R.id.noteDownvote).setOnClickListener {
//                    Toast.makeText(arView.context, "Downvote Note!", Toast.LENGTH_LONG).show()
//                }
//
//                this.renderable = renderable
//            }
//
//        arView.scene.addOnUpdateListener(this)
    }
}
