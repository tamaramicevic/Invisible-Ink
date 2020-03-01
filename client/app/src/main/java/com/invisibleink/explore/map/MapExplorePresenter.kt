package com.invisibleink.explore.map

import android.util.Log
import com.invisibleink.R
import com.invisibleink.architecture.BasePresenter
import com.invisibleink.models.Note
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import javax.inject.Inject

class MapExplorePresenter @Inject constructor(
    private val retrofit: Retrofit
) :
    BasePresenter<MapExploreViewState, MapExploreViewEvent, MapExploreDestination>() {

    private val exploreApi = retrofit.create(MapExploreApi::class.java)
    private val disposable = CompositeDisposable()

    override fun onEvent(viewEvent: MapExploreViewEvent) = when (viewEvent) {
        MapExploreViewEvent.FetchNotes -> fetchNotes()
    }

    override fun onDetach() {
        disposable.dispose()
    }

    private fun fetchNotes() {
        disposable.add(
            exploreApi.fetchNotes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::parseNotes, this::showError)
        )
    }

    private fun parseNotes(notes: List<Note>) {
        Log.d("MapExplorePresenter", "Parsing ${notes.size} notes!\nNOTE -> ${notes.joinToString("\nNOTE -> ")}")
        pushState(MapExploreViewState.Success(notes))
    }

    private fun showError(throwable: Throwable) {
        pushState(MapExploreViewState.Error(R.string.error_fetch_notes_generic))
    }
}
