package com.invisibleink.explore.ar

import com.invisibleink.R
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.ar.core.*
import com.google.ar.core.Pose
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.invisibleink.architecture.ViewProvider
import com.invisibleink.extensions.findViewOrThrow
import com.invisibleink.image.ImageFragment
import com.invisibleink.injection.InvisibleInkApplication
import com.invisibleink.location.LocationProvider
import com.invisibleink.models.Note
import com.invisibleink.permissions.onLocationPermissionGranted
import com.invisibleink.permissions.requireLocationPermission
import javax.inject.Inject
import kotlin.math.sqrt

class ArExploreFragment : ArFragment(), ViewProvider, LocationProvider {

    companion object {
        private const val REQUEST_LOCATION = 0
    }

    open fun onLocationPermissionGranted() {}

    @Inject
    lateinit var presenter: ArExplorePresenter
    private lateinit var viewDelegate: ArExploreViewDelegate
    private lateinit var notesToRender: MutableMap<String, ViewRenderable>
    private lateinit var notesRendered: MutableMap<String, ViewRenderable>
    private lateinit var notePositions: MutableList<Pose>
    private lateinit var locationProvider: FusedLocationProviderClient
    private var lastLocation: LatLng? = null
    private var locationChangedListener: ((LatLng) -> Unit?)? = null

    override fun <T : View> findViewById(id: Int): T = findViewOrThrow(id)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireLocationPermission(REQUEST_LOCATION, this@ArExploreFragment::setUpLocationListener)
        notesToRender = mutableMapOf()
        notesRendered = mutableMapOf()
        notePositions = mutableListOf()
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

        arSceneView.scene.addOnUpdateListener(this)

        return view
    }

    fun showNotes(deviceLocation: LatLng, notes: List<Note>) {
        notes.forEach loop@{ note ->
            Log.i("RenderingTest", note.id)

            if (notesRendered.containsKey(note.id!!) || notesToRender.containsKey(note.id!!)) {
                Log.i("RenderingTest", "NOTE ALREADY ACCOUNTED FOR - SKIP")
                return@loop
            }

            ViewRenderable.builder()
                .setView(requireActivity().baseContext, R.layout.ar_note_view).build()
                .thenAcceptAsync { renderable ->

                    if (note.imageUrl != null) {
                        renderable.view.findViewById<RelativeLayout>(R.id.noteLayout)
                            .setOnClickListener {
                                note.imageUrl?.let { it1 -> showImage(it1) }
                            }
                    }

                    var noteTitle = renderable.view.findViewById<TextView>(R.id.noteTitle)
                    noteTitle.text = note.title

                    val noteBody: TextView = renderable.view.findViewById(R.id.noteBody) as TextView
                    noteBody.text = note.body

                    val noteScore: TextView =
                        renderable.view.findViewById(R.id.noteScore) as TextView
                    noteScore.text = note.score.toString()

                    val noteExpiration: TextView =
                        renderable.view.findViewById(R.id.noteExpiry) as TextView
                    noteExpiration.text = note.expiration.toString()


                    renderable.view.findViewById<ImageButton>(R.id.noteReport).setOnClickListener {
                        Toast.makeText(
                            requireActivity().baseContext,
                            "Report Note!",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    renderable.view.findViewById<ImageButton>(R.id.noteUpvote).setOnClickListener {
                        Toast.makeText(
                            requireActivity().baseContext,
                            "Upvote Note!",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    renderable.view.findViewById<ImageButton>(R.id.noteDownvote)
                        .setOnClickListener {
                            Toast.makeText(
                                requireActivity().baseContext,
                                "Downvote Note!",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    Log.i("RenderingTest", "NOTE NOT RENDERED - ADDING")

                    notesToRender[note.id] = renderable
                    if (!this.notesRendered.containsKey(note.id!!)) {
                        note.id?.let { this.notesToRender.put(it, renderable) }
                    }

                }
                .exceptionally {
                    Toast.makeText(
                        requireActivity().baseContext,
                        "Unable to render note: " + note.id,
                        Toast.LENGTH_LONG
                    ).show()

                    Log.i("RenderingTest", "COULDNT RENDER NOTE: "+note.id)

                    return@exceptionally null;
                }
        }
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
            val trackables = frame.getUpdatedTrackables(Plane::class.java).iterator()

            while(trackables.hasNext()) {
                val plane = trackables.next() as Plane

                if (plane.trackingState == TrackingState.TRACKING) {
                    planeDiscoveryController.hide()

                    val notesToRenderItr = notesToRender.iterator()
                    while (notesToRenderItr.hasNext()) {
                        val renderable = notesToRenderItr.next()
                        val iterableAnchor = frame.updatedAnchors.iterator()

                        if(!iterableAnchor.hasNext()) {
                            Log.i("RenderingTest", "Found no previous anchor")
                            val hitTest = frame.hitTest(frame.screenCenter().x, frame.screenCenter().y)

                            val hitTestIterator = hitTest.iterator()
                            while(hitTestIterator.hasNext()) {
                                Log.i("RenderingTest", "Hit test successful")

                                val hitResult = hitTestIterator.next()

                                Log.i("DistanceTest", "Note Positions: $notePositions")
                                // checks for distances between all currently rendered notes
                                if (notePositions.isNotEmpty()) {
                                    val distances : MutableList<Double> = mutableListOf()
                                    notePositions.forEach { position ->

                                        // Compute the difference vector between the two hit locations.
                                        val dx = position.tx() - hitResult.hitPose.tx();
                                        val dy = position.ty() - hitResult.hitPose.ty();
                                        val dz = position.tz() - hitResult.hitPose.tz();

                                        // Compute the straight-line distance.
                                        val distanceMeters =  sqrt((dx*dx + dy*dy + dz*dz).toDouble());
                                        distances.add(distanceMeters)
                                    }

                                    Log.i("DistanceTest", "Distances: $distances")

                                    if (distances.any { it < 2.0 }) {
                                        Log.i("DistanceTest", "TOO CLOSE TOGETHER")
                                        continue
                                    }
                                }

                                val anchor = plane.createAnchor(hitResult.hitPose)

                                val anchorNode = AnchorNode(anchor)
                                anchorNode.setParent(arSceneView.scene)

                                val transformableNode = TransformableNode(transformationSystem)
                                transformableNode.setParent(anchorNode)

                                Log.i("RenderingTest", "RENDERABLE: $renderable")
                                transformableNode.renderable = renderable.value
                                notesRendered[renderable.key] = renderable.value

                                notePositions.add(hitResult.hitPose)

                                // alter the real world position to ensure object renders on the table top. Not somewhere inside.
                                transformableNode.worldPosition = Vector3(anchor.pose.tx(),
                                    anchor.pose.compose(Pose.makeTranslation(0f, 0.05f, 0.0f)).ty(),
                                    anchor.pose.tz())

                            }
                        }
//                        notesToRenderItr.remove()
                    }
                     notesToRender = notesToRender.filter{
                        !notesRendered.contains(it.key)
                    }.toMutableMap()

                    Log.i("RenderingTest", "NOTES RENDERED: "+ notesRendered.keys)
                    Log.i("RenderingTest", "NOTES TO RENDER: "+ notesToRender.keys)
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
        presenter.locationProvider = this
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

    private fun showImage(imageUrl: String) {
        val bundle = Bundle()
        bundle.putString("IMAGE-URL", imageUrl)
        Log.i("ImageTest", imageUrl)

        val imageFragment = ImageFragment()
        imageFragment.arguments = bundle

        val fragmentTransaction: FragmentTransaction? = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.fragmentContainer, imageFragment)?.addToBackStack(null)
        val commit = fragmentTransaction?.commit()
    }

}
