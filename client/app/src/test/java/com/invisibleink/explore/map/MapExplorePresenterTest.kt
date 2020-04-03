package com.invisibleink.explore.map

import com.google.android.gms.maps.model.LatLng
import com.invisibleink.R
import com.invisibleink.architecture.Router
import com.invisibleink.dashboard.NavigationDestination
import com.invisibleink.explore.PrebuiltOptions
import com.invisibleink.explore.SearchFilter
import com.invisibleink.location.LocationProvider
import com.invisibleink.models.Note
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
 * Tests all business logic for Map Explore functional requirements.
 */
class MapExplorePresenterTest {

    @get:Rule
    val rule: SchedulerRule = SchedulerRule()

    private lateinit var mapPresenter: MapExplorePresenter
    private lateinit var retrofit: Retrofit
    private lateinit var exploreApi: MapExploreApi
    private lateinit var disposable: CompositeDisposable
    private lateinit var locationProvider: LocationProvider
    private lateinit var mapViewDelegate: MapExploreViewDelegate
    private lateinit var locationUpdateListener: (LatLng) -> Unit?
    private lateinit var navigationRouter: Router<NavigationDestination>

    // State-less, helper objects re-used among tests
    companion object {
        private val validLocation: LatLng? = LatLng(0.0, 0.0)
        private val invalidLocation: LatLng? = null
        private val defaultSearchNotesFilter = SearchFilter("Some query", 12, false, PrebuiltOptions.WORST)
        private val locationOnlyFetchRequest = FetchNotesRequest(validLocation!!, SearchFilter.EMPTY_FILTER)
        private val defaultSearchNotesEvent = MapExploreViewEvent.SearchNotes(defaultSearchNotesFilter)
        private val defaultSearchNotesRequest = FetchNotesRequest(validLocation!!, defaultSearchNotesFilter)
        private val emptyNoteList = listOf<Note>()
        private val defaultNoteList = listOf(Note(
            id = "1",
            title = "title",
            body = "body",
            location = validLocation!!,
            score = 0
        ))
    }

    @Before
    fun setUp() {
        // Initialize dependencies as mocks
        locationProvider = mock()
        mapViewDelegate = mock()
        disposable = mock()
        exploreApi = mock()
        navigationRouter = mock()
        retrofit = mock {
            on { create(MapExploreApi::class.java) } doReturn exploreApi
        }
        setUpNoteApiReturns(null)

        // Instantiate class under test
        mapPresenter = MapExplorePresenter(retrofit)
        mapPresenter.disposable = disposable
        mapPresenter.locationProvider = locationProvider
        mapPresenter.router = navigationRouter
        mapPresenter.attach(mapViewDelegate)
        clearInvocations(mapViewDelegate)

        // Capture the location call back for testing
        argumentCaptor<(LatLng) -> Unit?>().apply {
            verify(locationProvider).addLocationChangeListener(capture())
            locationUpdateListener = firstValue
        }
    }

    @Test
    fun `verify presenter pushes loading state on attach`() {
        mapPresenter.detach()

        mapPresenter.attach(mapViewDelegate)
        verify(mapViewDelegate).render(MapExploreViewState.Loading)
    }

    @Test
    fun `verify presenter pushes loading state on fetch event`() {
        mapPresenter.onEvent(MapExploreViewEvent.FetchNotes)
        verify(mapViewDelegate).render(MapExploreViewState.Loading)
    }

    @Test
    fun `verify presenter pushes error state when location is not available on fetch`() {
        setUpLocationProvider(invalidLocation)

        mapPresenter.onEvent(MapExploreViewEvent.FetchNotes)
        verify(mapViewDelegate).render(MapExploreViewState.Error(R.string.error_invalid_device_location))
    }

    @Test
    fun `verify presenter invokes explore api when location available on fetch`() {
        setUpLocationProvider(validLocation)

        mapPresenter.onEvent(MapExploreViewEvent.FetchNotes)
        verify(exploreApi).fetchNotes(locationOnlyFetchRequest)
    }

    @Test
    fun `verify presenter pushes loading state on refresh event`() {
        mapPresenter.onEvent(MapExploreViewEvent.RefreshNotes)
        verify(mapViewDelegate).render(MapExploreViewState.Loading)
    }

    @Test
    fun `verify presenter pushes error state when location is not available on refresh event`() {
        setUpLocationProvider(invalidLocation)

        mapPresenter.onEvent(MapExploreViewEvent.RefreshNotes)
        verify(mapViewDelegate).render(MapExploreViewState.Error(R.string.error_invalid_device_location))
    }

    @Test
    fun `verify presenter invokes explore api when location available on refresh event`() {
        setUpLocationProvider(validLocation)

        mapPresenter.onEvent(MapExploreViewEvent.RefreshNotes)
        verify(exploreApi).fetchNotes(locationOnlyFetchRequest)
    }

    @Test
    fun `verify presenter pushes loading state on refresh search event`() {
        mapPresenter.onEvent(defaultSearchNotesEvent)
        verify(mapViewDelegate).render(MapExploreViewState.Loading)
    }

    @Test
    fun `verify presenter pushes error state when location is not available on search event`() {
        setUpLocationProvider(invalidLocation)

        mapPresenter.onEvent(defaultSearchNotesEvent)
        verify(mapViewDelegate).render(MapExploreViewState.Error(R.string.error_invalid_device_location))
    }

    @Test
    fun `verify presenter invokes explore api when location available on search event`() {
        setUpLocationProvider(validLocation)

        mapPresenter.onEvent(defaultSearchNotesEvent)
        verify(exploreApi).fetchNotes(defaultSearchNotesRequest)
    }

    @Test
    fun `presenter pushes generic error state on note api failure`() {
        setUpLocationProvider(validLocation)
        whenever(exploreApi.fetchNotes(any())) doReturn Single.error<NoteContainer>(Throwable()).toObservable()

        mapPresenter.onEvent(MapExploreViewEvent.FetchNotes)
        verify(mapViewDelegate).render(MapExploreViewState.Error(R.string.error_fetch_notes_generic))
    }

    @Test
    fun `presenter pushes note update state when api returns empty list of notes`() {
        setUpLocationProvider(validLocation)
        setUpNoteApiReturns(NoteContainer(emptyNoteList))

        mapPresenter.onEvent(MapExploreViewEvent.FetchNotes)
        verify(mapViewDelegate).render(MapExploreViewState.NoteUpdate(validLocation!!, emptyNoteList))
    }

    @Test
    fun `presenter pushes note update state when api returns non-empty list of notes`() {
        setUpLocationProvider(validLocation)
        setUpNoteApiReturns(NoteContainer(defaultNoteList))

        mapPresenter.onEvent(MapExploreViewEvent.FetchNotes)
        verify(mapViewDelegate).render(MapExploreViewState.NoteUpdate(validLocation!!, defaultNoteList))
    }

    @Test
    fun `presenter does not push device update when location provider sends new location from uninitialized state`() {
        val newLocation = LatLng(1.0, 1.0)
        mapPresenter.recentNoteStatus = MapExplorePresenter.NoteStatus.Uninitialized

        clearInvocations(mapViewDelegate)
        locationUpdateListener.invoke(newLocation)
        verifyNoMoreInteractions(mapViewDelegate)
    }

    @Test
    fun `presenter pushes device update when location provider sends new location from initialized state`() {
        val newLocation = LatLng(1.0, 1.0)
        mapPresenter.recentNoteStatus = MapExplorePresenter.NoteStatus.Initialized(emptyNoteList)

        locationUpdateListener.invoke(newLocation)
        verify(mapViewDelegate).render(MapExploreViewState.DeviceLocationUpdate(newLocation, emptyNoteList))
    }

    @Test
    fun `presenter extracts filter when receiving initiate AR event`() {
        mapPresenter.onEvent(MapExploreViewEvent.InitiateAr)
        verify(mapViewDelegate).render(MapExploreViewState.ExtractFilter)
    }

    @Test
    fun `presenter routes to ArExplore tab when receiving FilterExtracted event`() {
        mapPresenter.onEvent(MapExploreViewEvent.FilterExtracted(defaultSearchNotesFilter))
        verify(navigationRouter).routeTo(NavigationDestination.ArExploreTab(defaultSearchNotesFilter))
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
}
