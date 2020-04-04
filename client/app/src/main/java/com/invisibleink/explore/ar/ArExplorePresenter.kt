package com.invisibleink.explore.ar

import androidx.annotation.StringRes
import com.invisibleink.R
import com.invisibleink.architecture.BasePresenter
import com.invisibleink.explore.SearchFilter
import com.invisibleink.explore.vote.VoteGateway
import com.invisibleink.location.LocationProvider
import com.invisibleink.models.Note
import com.invisibleink.report.ReportGateway
import com.invisibleink.report.ReportType
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
    lateinit var voteGateway: VoteGateway
    lateinit var reportGateway: ReportGateway
    var locationProvider: LocationProvider? = null
    var searchFilter: SearchFilter? = SearchFilter.EMPTY_FILTER
    private var recentNotes: List<Note> = listOf()

    private var DUPLICATE_VOTE: String = "DUPLICATE"

    override fun onEvent(viewEvent: ArExploreViewEvent) = when (viewEvent) {
        is ArExploreViewEvent.FetchNotes -> fetchNotes()
        is ArExploreViewEvent.UpvoteNote -> upvoteNote(viewEvent.noteId)
        is ArExploreViewEvent.DownvoteNote -> downvoteNote(viewEvent.noteId)
        is ArExploreViewEvent.ReportNote -> reportNote(viewEvent.noteId, viewEvent.reportType, viewEvent.reportComment)
    }

    override fun onAttach() {
        super.onAttach()
        pushState(ArExploreViewState.Message(R.string.loading))
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

    private fun fetchNotes(@StringRes message: Int = R.string.loading) {
        pushState(ArExploreViewState.Message(message))

        val deviceLocation = locationProvider?.getCurrentLocation()
        if (deviceLocation != null) {
            disposable.add(
                exploreApi.fetchNotes(
                    FetchNotesRequest(
                        deviceLocation,
                        searchFilter
                    )
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ noteContainer: NoteContainer? ->
                        showNotes(noteContainer?.notes)
                    }
                        , this::showError
                    )
            )
        } else {
            pushState(ArExploreViewState.Message(R.string.error_invalid_device_location))
        }
    }

    private fun showNotes(notes: List<Note>?) {
        if (notes == null) {
            ArExploreViewState.Message(R.string.error_fetch_notes_generic)
            return
        }

        val deviceLocation = locationProvider?.getCurrentLocation()
        recentNotes = notes

        val viewState = if (deviceLocation != null) {
            ArExploreViewState.Success(deviceLocation, recentNotes)
        } else {
            ArExploreViewState.Message(R.string.error_invalid_device_location)
        }
        pushState(viewState)
    }

    private fun upvoteNote(noteId: String) {
        disposable.add(
            voteGateway.upVoteNote(noteId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        if (it.toString() == DUPLICATE_VOTE) {
                            pushState(ArExploreViewState.Message(R.string.error_duplicate_vote))
                        } else {
                            fetchNotes(R.string.upvote_success)
                        }
                    },
                    { pushState(ArExploreViewState.Message(R.string.error_upvote_failed)) }
                )
        )
    }

    private fun downvoteNote(noteId: String) {
        disposable.add(
            voteGateway.downvoteNote(noteId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        if (it.toString() == DUPLICATE_VOTE) {
                            pushState(ArExploreViewState.Message(R.string.error_duplicate_vote))
                        } else {
                            fetchNotes(R.string.downvote_success)
                        }
                    },
                    { pushState(ArExploreViewState.Message(R.string.error_downvote_failed)) }
                )
        )
    }

    private fun reportNote(noteId: String, reportType: ReportType, reportComment: String) {
        disposable.add(reportGateway.reportNote(
            noteId = noteId,
            reportType = reportType,
            comment = reportComment
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { pushState(ArExploreViewState.Message(R.string.upload_report_success)) },
                { pushState(ArExploreViewState.Message(R.string.error_report_failed)) }
            ))
    }

    private fun showError(throwable: Throwable) {
        pushState(ArExploreViewState.Message(R.string.error_fetch_notes_generic))
    }
}
