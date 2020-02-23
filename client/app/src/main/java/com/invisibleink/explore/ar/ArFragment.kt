package com.invisibleink.explore.ar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.invisibleink.R
import com.invisibleink.architecture.ViewProvider
import com.invisibleink.extensions.findViewOrThrow
import com.invisibleink.injection.InvisibleInkApplication
import javax.inject.Inject

class ArExploreFragment : ArFragment(), ViewProvider {

    @Inject
    lateinit var presenter: ArExplorePresenter
    private lateinit var viewDelegate: ArExploreViewDelegate

    override fun <T : View> findViewById(id: Int): T = findViewOrThrow(id)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        /**
         * Render a text view when a plane is tapped
         */
        setOnTapArPlaneListener { hitResult, _, _ ->
            ViewRenderable.builder()
                .setView(requireActivity().baseContext, R.layout.fragment_ar_explore).build()
                .thenAccept { renderable ->
                    val anchor = hitResult.createAnchor()
                    val anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(arSceneView.scene)

                    Node().run {
                        this.renderable = renderable
                        setParent(anchorNode)
                    }

                    arSceneView.scene.addChild(anchorNode)
                }
        }

        return view
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
