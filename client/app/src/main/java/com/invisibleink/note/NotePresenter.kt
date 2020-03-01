package com.invisibleink.note

import com.invisibleink.R
import com.invisibleink.architecture.BasePresenter
import com.invisibleink.location.LocationProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class NotePresenter @Inject constructor(retrofit: Retrofit) :
    BasePresenter<NoteViewState, NoteViewEvent, NoteDestination>() {

    interface ImageSelector {
        fun onAddImageSelected()
    }

    companion object {
        private val TAG = NotePresenter::class.java.canonicalName
    }

    private val noteApi = retrofit.create(NoteApi::class.java)
    private val disposable = CompositeDisposable()
    var imageSelector: ImageSelector? = null
    var locationProvider: LocationProvider? = null

    override fun onEvent(viewEvent: NoteViewEvent): Unit? = when (viewEvent) {
        is NoteViewEvent.Upload -> uploadNote(viewEvent.noteContent)
        is NoteViewEvent.AddImage -> imageSelector?.onAddImageSelected()
    }

    override fun onAttach() {
        pushState(NoteViewState.Empty)
    }

    override fun onDetach() {
        disposable.dispose()
    }

    private fun uploadNote(noteContent: NoteContent) {
        val (isValid, error) = isValidNote(noteContent)
        if (!isValid) {
            pushState(NoteViewState.Error(error))
            return
        }

        val location = locationProvider?.getCurrentLocation()
        if (location != null) {
            disposable.add(
                noteApi.uploadNote(noteContent.createNote(location))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::parseResponse, this::showNetworkError)
            )
        } else {
            pushState(NoteViewState.Error(R.string.error_invalid_device_location))
        }
    }

    private fun isValidNote(noteContent: NoteContent): Pair<Boolean, Int> = when {
        noteContent.title.isEmpty() -> false to R.string.invalid_note_title
        noteContent.body.isEmpty() -> false to R.string.invalid_note_title
        else -> true to R.string.valid_note
    }

    private fun showNetworkError(throwable: Throwable) {
        pushState(NoteViewState.Error(R.string.upload_error_generic))
    }

    private fun parseResponse(uploadResponse: Response<ResponseBody>?) {
        if (uploadResponse?.isSuccessful == true) {
            pushState(NoteViewState.Error(R.string.upload_success))
        } else {
            showNetworkError(Throwable())
        }
    }
}
