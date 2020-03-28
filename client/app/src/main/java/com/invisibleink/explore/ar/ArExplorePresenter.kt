package com.invisibleink.explore.ar

import com.invisibleink.R
import com.invisibleink.architecture.BasePresenter
import com.invisibleink.location.LocationProvider
import com.invisibleink.models.Note
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import javax.inject.Inject

class ArExplorePresenter @Inject constructor(
    retrofit: Retrofit
) :
    BasePresenter<ArExploreViewState, ArExploreViewEvent, ArExploreDestination>() {

    private val exploreApi = retrofit.create(ArExploreApi::class.java)
    private val disposable = CompositeDisposable()
    var locationProvider: LocationProvider? = null
    private var recentNotes: List<Note> = listOf()

    override fun onEvent(viewEvent: ArExploreViewEvent) = when (viewEvent) {
        ArExploreViewEvent.FetchNotes -> fetchNotes()
    }

    override fun onAttach() {
        super.onAttach()
        pushState(ArExploreViewState.Loading)
        locationProvider?.addLocationChangeListener {
            // Only re-fetch notes if we have none. Otherwise just update the device
            // location on the map.
            if (recentNotes.isEmpty()) {
                fetchNotes()
            } else {
                showNotes(recentNotes)
            }
        }
    }

    override fun onDetach() {
        disposable.dispose()
    }

    private fun fetchNotes(searchFilter: SearchFilter? = null) {
        pushState(ArExploreViewState.Loading)

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
                    .subscribe(this::showNotes, this::showError)
            )
        } else {
            pushState(ArExploreViewState.Error(R.string.error_invalid_device_location))
        }
    }

    private fun showNotes(notes: List<Note>) {
        val deviceLocation = locationProvider?.getCurrentLocation()
        recentNotes = notes

        val viewState = if (deviceLocation != null) {
            ArExploreViewState.Success(deviceLocation, recentNotes)
        } else {
            ArExploreViewState.Error(R.string.error_invalid_device_location)
        }
        pushState(viewState)
    }

    private fun showError(throwable: Throwable) {
        pushState(ArExploreViewState.Error(R.string.error_fetch_notes_generic))
    }
}
