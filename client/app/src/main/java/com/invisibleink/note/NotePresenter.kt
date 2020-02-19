package com.invisibleink.note

import android.util.Log
import com.invisibleink.R
import com.invisibleink.architecture.BasePresenter
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

    override fun onEvent(viewEvent: NoteViewEvent): Unit? = when (viewEvent) {
        is NoteViewEvent.Upload -> uploadNote(viewEvent.note)
        is NoteViewEvent.AddImage -> imageSelector?.onAddImageSelected()
    }

    override fun onAttach() {
        pushState(NoteViewState.Empty)
    }

    override fun onDetach() {
        disposable.dispose()
    }

    private fun uploadNote(note: Note) {
        val (isValid, error) = isValidNote(note)
        if (!isValid) {
            pushState(NoteViewState.Error(error))
            return
        }

        disposable.add(
            noteApi.uploadNote(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::parseResponse, this::showNetworkError)
        )
    }

    private fun isValidNote(note: Note): Pair<Boolean, Int> = when {
        note.title.isEmpty() -> false to R.string.invalid_note_title
        note.body.isEmpty() -> false to R.string.invalid_note_title
        else -> true to R.string.valid_note
    }

    private fun showNetworkError(throwable: Throwable) {
        Log.e(TAG, "Failed to upload note: $throwable")
        pushState(NoteViewState.Error(R.string.upload_error_generic))
    }

    private fun parseResponse(uploadResponse: Response<ResponseBody>?) {
        if (uploadResponse?.isSuccessful == true) {
            Log.d(TAG, "Note upload successful!")
            pushState(NoteViewState.Error(R.string.upload_success))
        } else {
            showNetworkError(Throwable())
        }
    }
}
