package com.invisibleink.explore.ar

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.*
import android.widget.*
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.Pose
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.invisibleink.R
import com.invisibleink.architecture.Router
import com.invisibleink.architecture.ViewProvider
import com.invisibleink.dashboard.NavigationActivity
import com.invisibleink.dashboard.NavigationDestination
import com.invisibleink.explore.SearchFilter
import com.invisibleink.explore.vote.VoteGateway
import com.invisibleink.explore.vote.createVoteDatabase
import com.invisibleink.extensions.doNothingOnBackPress
import com.invisibleink.extensions.findViewOrThrow
import com.invisibleink.injection.InvisibleInkApplication
import com.invisibleink.location.LocationProvider
import com.invisibleink.models.Note
import com.invisibleink.permissions.onLocationPermissionGranted
import com.invisibleink.permissions.requireLocationPermission
import com.invisibleink.report.ReportDialogBuilder
import com.invisibleink.report.ReportGateway
import javax.inject.Inject
import kotlin.math.sqrt

class ArExploreFragment : ArFragment(), ViewProvider, LocationProvider,
    NavigationActivity.BackPressHandler {

    companion object {
        private const val REQUEST_LOCATION = 0
        private const val EXTRA_AR_SEARCH_FILTER =
            "com.invisibleink.explore.ar.extra_search_filter"

        fun constructBundle(searchFilter: SearchFilter = SearchFilter.EMPTY_FILTER) =
            Bundle().apply {
                putSerializable(EXTRA_AR_SEARCH_FILTER, searchFilter)
            }
    }

    @Inject
    lateinit var presenter: ArExplorePresenter
    @Inject
    lateinit var voteGateway: VoteGateway
    @Inject
    lateinit var reportGateway: ReportGateway

    private lateinit var viewDelegate: ArExploreViewDelegate
    private lateinit var searchFilter: SearchFilter
    private var navigationRouter: Router<NavigationDestination>? = null
    private lateinit var notesToRender: MutableMap<String, ViewRenderable>
    private lateinit var notesRendered: MutableMap<String, ViewRenderable>
    private lateinit var notePositions: MutableList<Pose>
    private lateinit var locationProvider: FusedLocationProviderClient
    private var lastLocation: LatLng? = null
    private var locationChangedListener: ((LatLng) -> Unit?)? = null
    private var DISTANCE_BETWEEN_NOTES = 1.0

    override fun <T : View> findViewById(id: Int): T = findViewOrThrow(id)
    override fun onBackPress() = doNothingOnBackPress()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        requireLocationPermission(REQUEST_LOCATION, this@ArExploreFragment::setUpLocationListener)
        notesToRender = mutableMapOf()
        notesRendered = mutableMapOf()
        notePositions = mutableListOf()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        menu.findItem(R.id.refreshItem).isVisible = true
        menu.findItem(R.id.mapExploreItem).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.refreshItem -> {
            // TODO (Tamara): Refresh to notes!
            true
        }
        R.id.mapExploreItem -> {
            navigationRouter?.routeTo(NavigationDestination.MapExploreTab(searchFilter))
            true
        }
        else -> super.onOptionsItemSelected(item)
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
        searchFilter = (arguments?.getSerializable(EXTRA_AR_SEARCH_FILTER) as? SearchFilter)
            ?: SearchFilter.EMPTY_FILTER

        arSceneView.scene.addOnUpdateListener(this)

        return view
    }

    fun showNotes(deviceLocation: LatLng, notes: List<Note>) {
        notes.forEach loop@{ note ->

            if (notesRendered.containsKey(note.id!!) || notesToRender.containsKey(note.id!!)) {

                if (notesRendered.containsKey(note.id!!)) {
                    // check if score needs updating
                    var rendered: ViewRenderable? = notesRendered[note.id]

                    val noteScore = rendered?.view?.findViewById<EditText>(R.id.noteScore)
                    val currentScore = Integer.parseInt(noteScore?.text.toString())

                    if (currentScore != note.score) {
                        noteScore?.setText(note.score.toString())
                    }
                }

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

                    val noteScore: EditText =
                        renderable.view.findViewById(R.id.noteScore) as EditText
                    noteScore.setText(note.score.toString())

                    val noteExpiration: TextView =
                        renderable.view.findViewById(R.id.noteExpiry) as TextView
                    noteExpiration.text = note.expiration.toString()


                    renderable.view.findViewById<ImageButton>(R.id.noteReport).setOnClickListener {
                        reportNote(note.id)
                    }

                    renderable.view.findViewById<ImageButton>(R.id.noteUpvote).setOnClickListener {
                        viewDelegate.pushEvent(ArExploreViewEvent.UpvoteNote(note.id))
                    }

                    renderable.view.findViewById<ImageButton>(R.id.noteDownvote)
                        .setOnClickListener {
                            viewDelegate.pushEvent(ArExploreViewEvent.DownvoteNote(note.id))
                        }

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

            while (trackables.hasNext()) {
                val plane = trackables.next() as Plane

                if (plane.trackingState == TrackingState.TRACKING) {
                    planeDiscoveryController.hide()

                    val notesToRenderItr = notesToRender.iterator()
                    while (notesToRenderItr.hasNext()) {
                        val renderable = notesToRenderItr.next()
                        val iterableAnchor = frame.updatedAnchors.iterator()

                        if (!iterableAnchor.hasNext()) {
                            val hitTest =
                                frame.hitTest(frame.screenCenter().x, frame.screenCenter().y)

                            val hitTestIterator = hitTest.iterator()
                            while (hitTestIterator.hasNext()) {

                                val hitResult = hitTestIterator.next()

                                // checks for distances between all currently rendered notes
                                if (notePositions.isNotEmpty()) {
                                    val distances: MutableList<Double> = mutableListOf()
                                    notePositions.forEach { position ->

                                        // Compute the difference vector between the two hit locations.
                                        val dx = position.tx() - hitResult.hitPose.tx();
                                        val dy = position.ty() - hitResult.hitPose.ty();
                                        val dz = position.tz() - hitResult.hitPose.tz();

                                        // Compute the straight-line distance.
                                        val distanceMeters =
                                            sqrt((dx * dx + dy * dy + dz * dz).toDouble());
                                        distances.add(distanceMeters)
                                    }

                                    if (distances.any { it < DISTANCE_BETWEEN_NOTES }) {
                                        continue
                                    }
                                }

                                val anchor = plane.createAnchor(hitResult.hitPose)

                                val anchorNode = AnchorNode(anchor)
                                anchorNode.setParent(arSceneView.scene)

                                val transformableNode = TransformableNode(transformationSystem)
                                transformableNode.setParent(anchorNode)

                                transformableNode.renderable = renderable.value
                                notesRendered[renderable.key] = renderable.value

                                notePositions.add(hitResult.hitPose)

                                // alter the real world position to ensure object renders on the table top. Not somewhere inside.
                                transformableNode.worldPosition = Vector3(
                                    anchor.pose.tx(),
                                    anchor.pose.compose(Pose.makeTranslation(0f, 0.05f, 0.0f)).ty(),
                                    anchor.pose.tz()
                                )

                            }
                        }
                    }

                    notesToRender = notesToRender.filter {
                        !notesRendered.contains(it.key)
                    }.toMutableMap()
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        requireActivity().window.decorView.systemUiVisibility = 0
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
        navigationRouter = requireActivity() as? Router<NavigationDestination>
        presenter.voteGateway = voteGateway
        presenter.voteGateway.voteDatabase =
            createVoteDatabase(application = requireActivity().application)

        presenter.reportGateway = reportGateway
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
        navigationRouter?.routeTo(NavigationDestination.ImageTab(imageUrl))
    }

    private fun reportNote(noteID: String) {
        ReportDialogBuilder().buildReportDialog(requireContext(),
            { reportType, reportComment ->
                if (reportType != null) {
                    reportComment?.let { it1 ->
                        ArExploreViewEvent.ReportNote(
                            noteID, reportType,
                            it1
                        )
                    }?.let { it2 -> viewDelegate.pushEvent(it2) }
                } else {
                    presenter.pushState(ArExploreViewState.Message(R.string.error_type_report_failed))
                }
            },
            { presenter.pushState(ArExploreViewState.Message(R.string.upload_report_cancelled)) }
        ).show()
    }

    open fun onLocationPermissionGranted() {}
}
