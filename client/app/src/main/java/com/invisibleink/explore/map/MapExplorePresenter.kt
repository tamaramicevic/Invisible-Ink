package com.invisibleink.explore.map

import com.google.android.gms.maps.model.LatLng
import com.invisibleink.R
import com.invisibleink.architecture.BasePresenter
import com.invisibleink.location.LocationProvider
import com.invisibleink.models.Note
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import javax.inject.Inject

class MapExplorePresenter @Inject constructor(
    retrofit: Retrofit
) :
    BasePresenter<MapExploreViewState, MapExploreViewEvent, MapExploreDestination>() {

    private val exploreApi = retrofit.create(MapExploreApi::class.java)
    private val disposable = CompositeDisposable()
    var locationProvider: LocationProvider? = null
    private var recentNoteStatus: NoteStatus = NoteStatus.Uninitialized

    sealed class NoteStatus {
        object Uninitialized : NoteStatus()
        data class Initialized(val notes: List<Note>) : NoteStatus()
    }

    override fun onEvent(viewEvent: MapExploreViewEvent) = when (viewEvent) {
        is MapExploreViewEvent.FetchNotes -> fetchNotes()
        is MapExploreViewEvent.RefreshNotes -> fetchNotes()
        is MapExploreViewEvent.SearchNotes -> searchNotes(viewEvent)
    }

    override fun onAttach() {
        super.onAttach()
        pushState(MapExploreViewState.Loading)
        locationProvider?.addLocationChangeListener {
            showLocationUpdate(it)
        }
    }

    override fun onDetach() {
        disposable.dispose()
    }

    private fun showLocationUpdate(updatedLocation: LatLng) {
        val notes = (recentNoteStatus as? NoteStatus.Initialized)?.notes
        if (notes != null) {
            pushState(MapExploreViewState.DeviceLocationUpdate(updatedLocation, notes))
        }
    }

    private fun fetchNotes(searchFilter: SearchFilter? = null) {
        pushState(MapExploreViewState.Loading)

        val deviceLocation = locationProvider?.getCurrentLocation()
        if (deviceLocation != null) {
            disposable.add(
                exploreApi.fetchNotes(
                    FetchNotesRequest(
                        deviceLocation,
                        searchFilter ?: SearchFilter.EMPTY_FILTER
                    )
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ noteContainer: NoteContainer? ->
                        showNotes(noteContainer?.notes, deviceLocation)
                    }
                        , this::showError
                    )
            )
        } else {
            pushState(MapExploreViewState.Error(R.string.error_invalid_device_location))
        }
    }

    private fun searchNotes(searchEvent: MapExploreViewEvent.SearchNotes) = fetchNotes(
        SearchFilter(
            keywords = searchEvent.query,
            limit = searchEvent.limit,
            withImage = searchEvent.withImage,
            options = searchEvent.options
        )
    )

    private fun showNotes(notes: List<Note>?, deviceLocation: LatLng) {
        val (viewState, noteStatus) = if (notes == null) {
            MapExploreViewState.Error(R.string.error_fetch_notes_generic) to NoteStatus.Uninitialized
        } else {
            MapExploreViewState.NoteUpdate(deviceLocation, notes) to NoteStatus.Initialized(notes)
        }

        recentNoteStatus = noteStatus
        pushState(viewState)
    }

    private fun showError(throwable: Throwable) {
        pushState(MapExploreViewState.Error(R.string.error_fetch_notes_generic))
    }
}
