package com.invisibleink.explore.ar

import android.R.attr.fragment
import android.annotation.SuppressLint
import com.invisibleink.R
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.ar.core.*
import com.google.ar.core.Pose
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.invisibleink.architecture.Router
import com.invisibleink.architecture.ViewProvider
import com.invisibleink.extensions.findViewOrThrow
import com.invisibleink.image.ImageFragment
import com.invisibleink.injection.InvisibleInkApplication
import com.invisibleink.location.LocationFragment
import com.invisibleink.location.LocationProvider
import com.invisibleink.note.NoteFragment
import com.invisibleink.permissions.onLocationPermissionGranted
import com.invisibleink.permissions.requireLocationPermission
import javax.inject.Inject
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


class ArExploreFragment : ArFragment(), ViewProvider, Router<ArExploreDestination>, LocationProvider {

    companion object {
        private const val REQUEST_LOCATION = 0
    }

    open fun onLocationPermissionGranted() {}

    @Inject
    lateinit var presenter: ArExplorePresenter
    private lateinit var viewDelegate: ArExploreViewDelegate
    private var renderable: ViewRenderable? = null
    private lateinit var locationProvider: FusedLocationProviderClient
    private var lastLocation: LatLng? = null
    private var locationChangedListener: ((LatLng) -> Unit?)? = null

    override fun <T : View> findViewById(id: Int): T = findViewOrThrow(id)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireLocationPermission(REQUEST_LOCATION, this@ArExploreFragment::setUpLocationListener)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION -> {
                grantResults.onLocationPermissionGranted(this::setUpLocationListener)
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private var locationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun setUpLocationListener() {
        locationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationProvider.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                lastLocation = LatLng(location.latitude, location.longitude)
            }
            onLocationPermissionGranted()
        }
        locationProvider.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        ViewRenderable.builder()
            .setView(requireActivity().baseContext, R.layout.ar_note_view).build()
            .thenAcceptAsync { renderable ->

                renderable.view.findViewById<RelativeLayout>(R.id.noteLayout).setOnClickListener {
                    viewDelegate.pushEvent(ArExploreViewEvent.ShowImage)
                }

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
        viewDelegate.arFragment = this
        viewDelegate.arView = arSceneView
        presenter.locationProvider = this
        presenter.router = this
        presenter.attach(viewDelegate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detach()
    }

    override fun getCurrentLocation(): LatLng? = lastLocation

    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            val lastLatLng =
                LatLng(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude)
            this@ArExploreFragment.lastLocation = lastLatLng
            locationChangedListener?.invoke(lastLatLng)
        }
    }

    override fun addLocationChangeListener(onLocationChangeCallback: (LatLng) -> Unit?) {
        locationChangedListener = onLocationChangeCallback
    }

    @SuppressLint("ResourceType")
    override fun routeTo(destination: ArExploreDestination): Unit = when (destination) {
        ArExploreDestination.ShowImage -> {

            val imageURL: String = "https://source.unsplash.com/random" // TODO: remove url placeholder

            val imageFragment : ImageFragment = ImageFragment()
            val bundle = Bundle()
            bundle.putString("IMAGE-URL", imageURL)
            imageFragment.arguments = bundle

            val fragmentTransaction: FragmentTransaction? = fragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.fragmentContainer, imageFragment)?.addToBackStack(null)
            val commit = fragmentTransaction?.commit()
        }
    }

}
