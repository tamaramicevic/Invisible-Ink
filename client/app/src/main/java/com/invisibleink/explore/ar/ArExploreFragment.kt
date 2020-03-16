package com.invisibleink.explore.ar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.invisibleink.R
import com.invisibleink.architecture.ViewProvider
import com.invisibleink.extensions.findViewOrThrow
import com.invisibleink.injection.InvisibleInkApplication
import javax.inject.Inject


class ArExploreFragment : ArFragment(), ViewProvider {

    @Inject
    lateinit var presenter: ArExplorePresenter
    private lateinit var viewDelegate: ArExploreViewDelegate
    private var renderable: ViewRenderable? = null

    override fun <T : View> findViewById(id: Int): T = findViewOrThrow(id)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        ViewRenderable.builder()
            .setView(requireActivity().baseContext, R.layout.fragment_ar_explore).build()
            .thenAcceptAsync { renderable ->

                // checks if buttons work correctly
                renderable.view.findViewById<ImageButton>(R.id.noteReport).setOnClickListener {
                    Toast.makeText(requireActivity().baseContext, "Report Note!", Toast.LENGTH_LONG).show()
                }

                renderable.view.findViewById<ImageButton>(R.id.noteUpvote).setOnClickListener {
                    Toast.makeText(requireActivity().baseContext, "Upvote Note!", Toast.LENGTH_LONG).show()
                }

                renderable.view.findViewById<ImageButton>(R.id.noteDownvote).setOnClickListener {
                    Toast.makeText(requireActivity().baseContext, "Downvote Note!", Toast.LENGTH_LONG).show()
                }

                this.renderable = renderable
            }

        arSceneView.scene.addOnUpdateListener(this)

        return view
    }

    private fun Frame.screenCenter(): Vector3 {
        return Vector3(arSceneView.width / 2f, arSceneView.height / 2f, 0f);
    }

    /**
     * Renders a note object at the center of the screen without tapping
     */
    override fun onUpdate(frameTime: FrameTime?) {
        val frame = arSceneView.arFrame

        if (frame != null) {
            // get the trackables to ensure planes are detected
            val trackables = frame.getUpdatedTrackables(Plane::class.java).iterator()
            while(trackables.hasNext()) {
                val plane = trackables.next() as Plane

                if (plane.trackingState == TrackingState.TRACKING) {

                    // dhde the plane discovery helper animation
                    planeDiscoveryController.hide()

                    // get all added anchors to the frame
                    val iterableAnchor = frame.updatedAnchors.iterator()

                    // place the first object only if no previous anchors were added
                    if(!iterableAnchor.hasNext()) {
                        //Perform a hit test at the center of the screen to place an object without tapping
                        val hitTest = frame.hitTest(frame.screenCenter().x, frame.screenCenter().y)

                        //iterate through all hits
                        val hitTestIterator = hitTest.iterator()
                        while(hitTestIterator.hasNext()) {
                            val hitResult = hitTestIterator.next()

                            val anchor = plane.createAnchor(hitResult.hitPose)

                            val anchorNode = AnchorNode(anchor)
                            anchorNode.setParent(arSceneView.scene)

                            // create a new TranformableNode that will carry our object
                            val transformableNode = TransformableNode(transformationSystem)
                            transformableNode.setParent(anchorNode)
                            transformableNode.renderable = this.renderable

                            // alter the real world position to ensure object renders on the table top. Not somewhere inside.
                            transformableNode.worldPosition = Vector3(anchor.pose.tx(),
                                anchor.pose.compose(Pose.makeTranslation(0f, 0.05f, 0f)).ty(),
                                anchor.pose.tz())
                        }
                    }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as InvisibleInkApplication).appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDelegate = ArExploreViewDelegate(this)
        presenter.attach(viewDelegate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detach()
    }
}
