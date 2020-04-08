package com.invisibleink.explore.ar

import com.google.android.gms.maps.model.LatLng
import com.invisibleink.R
import com.invisibleink.architecture.Router
import com.invisibleink.dashboard.NavigationDestination
import com.invisibleink.explore.PrebuiltOptions
import com.invisibleink.explore.SearchFilter
import com.invisibleink.explore.ar.NoteContainer
import com.invisibleink.explore.vote.VoteGateway
import com.invisibleink.explore.vote.VoteResponse
import com.invisibleink.explore.vote.VoteResult
import com.invisibleink.location.LocationProvider
import com.invisibleink.models.Note
import com.invisibleink.report.ReportGateway
import com.invisibleink.report.ReportResult
import com.invisibleink.report.ReportType
import com.nhaarman.mockitokotlin2.*
import com.testutils.SchedulerRule
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit

/**
 * Tests all business logic for Ar Explore functional requirements.
 */
class ArExplorePresenterTest {
    @get:Rule
    val rule: SchedulerRule = SchedulerRule()

    private lateinit var arPresenter: ArExplorePresenter
    private lateinit var retrofit: Retrofit
    private lateinit var exploreApi: ArExploreApi
    private lateinit var disposable: CompositeDisposable
    private lateinit var locationProvider: LocationProvider
    private lateinit var arViewDelegate: ArExploreViewDelegate
    private lateinit var voteGateway: VoteGateway
    private lateinit var reportGateway: ReportGateway

    // State-less, helper objects re-used among tests
    companion object {
        private val validLocation: LatLng? = LatLng(0.0, 0.0)
        private val invalidLocation: LatLng? = null
        private val defaultSearchNotesFilter = SearchFilter("Some query", 12, false, PrebuiltOptions.WORST)
        private val locationOnlyFetchRequest = FetchNotesRequest(validLocation!!, SearchFilter.EMPTY_FILTER)
        private val defaultSearchNotesRequest = FetchNotesRequest(validLocation!!, defaultSearchNotesFilter)
        private const val validNoteId = "1"
        private val reportType = ReportType.HARASSMENT
    }

    @Before
    fun setUp() {
        // Initialize dependencies as mocks
        locationProvider = mock()
        arViewDelegate = mock()
        disposable = mock()
        exploreApi = mock()
        voteGateway = mock()
        reportGateway = mock()
        retrofit = mock {
            on { create(ArExploreApi::class.java) } doReturn exploreApi
        }
        setUpNoteApiReturns(null)
        setUpVoteApiReturns(null)
        setDownVoteApiReturns(null)
        setReportApiReturns(null)

        // Instantiate class under test
        arPresenter = ArExplorePresenter(retrofit)
        arPresenter.disposable = disposable
        arPresenter.locationProvider = locationProvider
        arPresenter.voteGateway = voteGateway
        arPresenter.reportGateway = reportGateway
        arPresenter.searchFilter = SearchFilter.EMPTY_FILTER
        arPresenter.attach(arViewDelegate)
        clearInvocations(arViewDelegate)

        // Capture the location call back for testing
        argumentCaptor<(LatLng) -> Unit?>().apply {
            verify(locationProvider).addLocationChangeListener(capture())
        }
    }

    @Test
    fun `verify presenter pushes loading state on attach`() {
        arPresenter.detach()

        arPresenter.attach(arViewDelegate)
        verify(arViewDelegate).render(ArExploreViewState.Loading)
    }

    @Test
    fun `verify presenter pushes loading state on fetch event`() {
        arPresenter.onEvent(ArExploreViewEvent.FetchNotes)
        verify(arViewDelegate).render(ArExploreViewState.Message(R.string.loading))
    }

    @Test
    fun `verify presenter pushes error state when location is not available on fetch`() {
        setUpLocationProvider(invalidLocation)

        arPresenter.onEvent(ArExploreViewEvent.FetchNotes)
        verify(arViewDelegate).render(ArExploreViewState.Message(R.string.error_invalid_device_location))
    }

    @Test
    fun `verify presenter invokes explore api when location available on fetch`() {
        setUpLocationProvider(validLocation)

        arPresenter.onEvent(ArExploreViewEvent.FetchNotes)
        verify(exploreApi).fetchNotes(locationOnlyFetchRequest)
    }

    @Test
    fun `verify presenter pushes loading state on refresh event`() {
        arPresenter.onEvent(ArExploreViewEvent.RefreshNotes)
        verify(arViewDelegate).render(ArExploreViewState.Message(R.string.loading))
    }

    @Test
    fun `verify presenter pushes error state when location is not available on refresh event`() {
        setUpLocationProvider(invalidLocation)

        arPresenter.onEvent(ArExploreViewEvent.RefreshNotes)
        verify(arViewDelegate).render(ArExploreViewState.Message(R.string.error_invalid_device_location))
    }

    @Test
    fun `verify presenter invokes explore api when location available on refresh event`() {
        setUpLocationProvider(validLocation)

        arPresenter.onEvent(ArExploreViewEvent.RefreshNotes)
        verify(exploreApi).fetchNotes(locationOnlyFetchRequest)
    }

    @Test
    fun `verify presenter invokes explore api when location available on fetch event with search inputs`() {
        setUpLocationProvider(validLocation)

        arPresenter.searchFilter = defaultSearchNotesFilter
        arPresenter.onEvent(ArExploreViewEvent.FetchNotes)
        verify(exploreApi).fetchNotes(defaultSearchNotesRequest)
    }

    @Test
    fun `verify presenter pushes generic error state on note api failure`() {
        setUpLocationProvider(validLocation)
        whenever(exploreApi.fetchNotes(any())) doReturn Single.error<NoteContainer>(Throwable()).toObservable()

        arPresenter.onEvent(ArExploreViewEvent.FetchNotes)
        verify(arViewDelegate).render(ArExploreViewState.Message(R.string.error_fetch_notes_generic))
    }

    @Test
    fun `verify presenter invokes voteGateway to up-vote on a note`() {
        arPresenter.onEvent(ArExploreViewEvent.UpvoteNote(validNoteId))

        verify(voteGateway).upVoteNote(validNoteId)
    }

    @Test
    fun `verify presenter pushes success message on up-voting a note`() {
        setUpVoteApiReturns(VoteResult.SUCCESS)

        arPresenter.onEvent(ArExploreViewEvent.UpvoteNote(validNoteId))
        verify(arViewDelegate).render(ArExploreViewState.Message(R.string.upvote_success))
    }

    @Test
    fun `verify presenter pushes duplicate message on up-voting a note`() {
        setUpVoteApiReturns(VoteResult.DUPLICATE)

        arPresenter.onEvent(ArExploreViewEvent.UpvoteNote(validNoteId))
        verify(arViewDelegate).render(ArExploreViewState.Message(R.string.error_duplicate_vote))
    }

    @Test
    fun `verify presenter pushes failure message on up-voting a note`() {
        setUpVoteApiReturns(VoteResult.FAILURE)

        arPresenter.onEvent(ArExploreViewEvent.UpvoteNote(validNoteId))
        verify(arViewDelegate).render(ArExploreViewState.Message(R.string.error_upvote_failed))
    }

    @Test
    fun `verify presenter invokes voteGateway to down-vote on a note`() {
        arPresenter.onEvent(ArExploreViewEvent.DownvoteNote(validNoteId))

        verify(voteGateway).downVoteNote(validNoteId)
    }

    @Test
    fun `verify presenter pushes success message on down-voting a note`() {
        setDownVoteApiReturns(VoteResult.SUCCESS)

        arPresenter.onEvent(ArExploreViewEvent.DownvoteNote(validNoteId))
        verify(arViewDelegate).render(ArExploreViewState.Message(R.string.downvote_success))
    }

    @Test
    fun `verify presenter pushes failure message on down-voting a note`() {
        setDownVoteApiReturns(VoteResult.FAILURE)

        arPresenter.onEvent(ArExploreViewEvent.DownvoteNote(validNoteId))
        verify(arViewDelegate).render(ArExploreViewState.Message(R.string.error_downvote_failed))
    }

    @Test
    fun `verify presenter pushes duplicate message on down-voting a note`() {
        setDownVoteApiReturns(VoteResult.DUPLICATE)

        arPresenter.onEvent(ArExploreViewEvent.DownvoteNote(validNoteId))
        verify(arViewDelegate).render(ArExploreViewState.Message(R.string.error_duplicate_vote))
    }

    @Test
    fun `verify presenter invokes reportGateway to report a note`() {
        arPresenter.onEvent(ArExploreViewEvent.ReportNote(validNoteId, reportType, ""))

        verify(reportGateway).reportNote(validNoteId, reportType, "")
    }

    @Test
    fun `verify presenter pushes success message for reporting a note`() {
        setReportApiReturns(ReportResult.SUCCESS)
        arPresenter.onEvent(ArExploreViewEvent.ReportNote(validNoteId, reportType, ""))

        verify(arViewDelegate).render(ArExploreViewState.Message(R.string.upload_report_success))
    }

    @Test
    fun `verify presenter pushes failure message for reporting a note`() {
        setReportApiReturns(ReportResult.FAILURE)
        arPresenter.onEvent(ArExploreViewEvent.ReportNote(validNoteId, reportType, ""))

        verify(arViewDelegate).render(ArExploreViewState.Message(R.string.error_report_failed))
    }

    private fun setUpLocationProvider(location: LatLng?) =
        whenever(locationProvider.getCurrentLocation()) doReturn location

    private fun setUpNoteApiReturns(fetchNotesResponse: NoteContainer?) {
        val fetchNoteResp = if (fetchNotesResponse != null) {
            Single.just(fetchNotesResponse).toObservable()
        } else {
            Observable.never<NoteContainer>()
        }

        whenever(exploreApi.fetchNotes(any())) doReturn fetchNoteResp
    }

    private fun setUpVoteApiReturns(voteResponse: VoteResult?) {
        val voteResp = if (voteResponse != null) {
            Single.just(voteResponse).toObservable()
        } else {
            Observable.never<VoteResult>()
        }

        whenever(voteGateway.upVoteNote(any())) doReturn voteResp
    }

    private fun setDownVoteApiReturns(voteResponse: VoteResult?) {
        val voteResponseObsv = if (voteResponse != null) {
            Single.just(voteResponse).toObservable()
        } else {
            Observable.never<VoteResult>()
        }

        whenever(voteGateway.downVoteNote(any())) doReturn voteResponseObsv
    }

    private fun setReportApiReturns(reportResponse: ReportResult?) {
        val reportResp = if (reportResponse != null) {
            Single.just(reportResponse).toObservable()
        } else {
            Observable.never<ReportResult>()
        }

        whenever(reportGateway.reportNote(any(), any(), any())) doReturn reportResp
    }
}