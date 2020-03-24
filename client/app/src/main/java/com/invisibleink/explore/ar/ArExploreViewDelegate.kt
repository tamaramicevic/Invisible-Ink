package com.invisibleink.explore.ar

import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.gms.maps.model.LatLng
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.Pose
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.invisibleink.R
import com.invisibleink.architecture.BaseViewDelegate
import com.invisibleink.architecture.ViewProvider
import com.invisibleink.extensions.showSnackbar
import com.invisibleink.models.Note

class ArExploreViewDelegate(viewProvider: ViewProvider) :
    BaseViewDelegate<ArExploreViewState, ArExploreViewEvent, ArExploreDestination>(viewProvider) {

    lateinit var arFragment : ArFragment
    lateinit var arView : ArSceneView
    private var renderable: ViewRenderable? = null

    override fun render(viewState: ArExploreViewState): Unit? = when (viewState) {
        is ArExploreViewState.Error -> showMessage(viewState.message)
        is ArExploreViewState.Success -> showNotes(viewState.deviceLocation, viewState.notes)
        is ArExploreViewState.Loading -> showLoading(true)
    }

    private fun showLoading(isLoading: Boolean) {
        Log.i("RenderingTest", "Loading...")
    }

    private fun showMessage(@StringRes message: Int)  = arView?.showSnackbar(message)

    private fun Frame.screenCenter(): Vector3 {
        return Vector3(arView.width / 2f, arView.height / 2f, 0f);
    }

    private fun renderNote() {
        val frame = arView.arFrame
        Log.i("RenderingTest", "enter Rendering..")

        while (frame == null) { Log.i("RenderingTest", "Waiting to render..") /** do nothing **/}

        if (frame != null) {
            Log.i("RenderingTest", "Rendering.. ")
            // get the trackables to ensure planes are detected
            val trackables = frame.getUpdatedTrackables(Plane::class.java).iterator()
            while (trackables.hasNext()) {
                val plane = trackables.next() as Plane

                if (plane.trackingState == TrackingState.TRACKING) {

                    // dhde the plane discovery helper animation
                    arFragment.planeDiscoveryController.hide()

                    // get all added anchors to the frame
                    val iterableAnchor = frame.updatedAnchors.iterator()

                    // place the first object only if no previous anchors were added
                    if (!iterableAnchor.hasNext()) {
                        //Perform a hit test at the center of the screen to place an object without tapping
                        val hitTest = frame.hitTest(frame.screenCenter().x, frame.screenCenter().y)

                        //iterate through all hits
                        val hitTestIterator = hitTest.iterator()
                        while (hitTestIterator.hasNext()) {
                            val hitResult = hitTestIterator.next()

                            val anchor = plane.createAnchor(hitResult.hitPose)

                            val anchorNode = AnchorNode(anchor)
                            anchorNode.setParent(arView.scene)

                            // create a new TranformableNode that will carry our object
                            val transformableNode = TransformableNode( arFragment.transformationSystem)
                            transformableNode.setParent(anchorNode)
                            transformableNode.renderable = this.renderable

                            // alter the real world position to ensure object renders on the table top. Not somewhere inside.
                            transformableNode.worldPosition = Vector3(
                                anchor.pose.tx(),
                                anchor.pose.compose(Pose.makeTranslation(0f, 0.05f, 0f)).ty(),
                                anchor.pose.tz()
                            )
                        }
                    }
                }
            }
        }
    }

    private fun showNotes(deviceLocation: LatLng, notes: List<Note>) {

        Log.i("RenderingTest", notes.toString())

        notes.forEach { note ->
            ViewRenderable.builder()
                .setView(arView.context, R.layout.ar_note_view).build()
                .thenAcceptAsync { renderable ->

                    val noteTitle: TextView = viewProvider.findViewById(R.id.noteTitle) as TextView
                    noteTitle.text = note.title

                    val noteBody: TextView = viewProvider.findViewById(R.id.noteBody) as TextView
                    noteBody.text = note.body

                    val noteScore: TextView = viewProvider.findViewById(R.id.noteScore) as TextView
                    noteScore.text = note.score.toString()

                    val noteExpiration: TextView = viewProvider.findViewById(R.id.noteExpiry) as TextView
                    noteExpiration.text = note.expiration.toString()

                    // checks if buttons work correctly
                    renderable.view.findViewById<ImageButton>(R.id.noteReport).setOnClickListener {
                        Toast.makeText(arView.context, "Report Note!", Toast.LENGTH_LONG).show()
                    }

                    renderable.view.findViewById<ImageButton>(R.id.noteUpvote).setOnClickListener {
                        Toast.makeText(arView.context, "Upvote Note!", Toast.LENGTH_LONG).show()
                    }

                    renderable.view.findViewById<ImageButton>(R.id.noteDownvote).setOnClickListener {
                        Toast.makeText(arView.context, "Downvote Note!", Toast.LENGTH_LONG).show()
                    }

                    this.renderable = renderable
                    Log.i("RenderingTest", "About to render..")
                    renderNote();

//                    arView.scene.addOnUpdateListener { renderNote() }
                }
        }

//        arView.scene.addOnUpdateListener(this)
    }
}
