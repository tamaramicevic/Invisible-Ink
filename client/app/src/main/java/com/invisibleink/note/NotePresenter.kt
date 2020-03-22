package com.invisibleink.note

import com.invisibleink.R
import com.invisibleink.architecture.BasePresenter
import com.invisibleink.location.LocationProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import java.io.File
import javax.inject.Inject

class NotePresenter @Inject constructor(retrofit: Retrofit) :
    BasePresenter<NoteViewState, NoteViewEvent, NoteDestination>() {

    interface ImageHandler {
        fun onAddImageSelected()
    }

    companion object {
        private val TAG = NotePresenter::class.java.canonicalName
    }

    private val noteApi = retrofit.create(NoteApi::class.java)
    private val disposable = CompositeDisposable()
    var imageHandler: ImageHandler? = null
    var locationProvider: LocationProvider? = null

    override fun onEvent(viewEvent: NoteViewEvent): Unit? = when (viewEvent) {
        is NoteViewEvent.Upload -> uploadNote(viewEvent.noteSeed)
        is NoteViewEvent.AddImage -> imageHandler?.onAddImageSelected()
    }

    override fun onAttach() {
        pushState(NoteViewState.Empty)
    }

    override fun onDetach() {
        disposable.dispose()
    }

    private fun uploadNote(noteSeed: NoteSeed) {
        val (isValid, error) = isValidNote(noteSeed)
        if (!isValid) {
            pushState(NoteViewState.Error(error))
            return
        }

        val location = locationProvider?.getCurrentLocation()
        if (location == null) {
            pushState(NoteViewState.Error(R.string.error_invalid_device_location))
            return
        }

        noteSeed.apply { this.location = location }
        val validImagePath = noteSeed.imagePath
        if (validImagePath != null) {
            uploadNoteWithImage(noteSeed, validImagePath)
        } else {
            uploadNoteWithoutImage(noteSeed)
        }
    }

    private fun uploadNoteWithImage(noteSeed: NoteSeed, validImagePath: String) {
        val imageFile = File(validImagePath)
        val requestFile = imageFile.asRequestBody(("multipart/form-data".toMediaTypeOrNull()))
        val requestBody = MultipartBody.Part.createFormData("image", imageFile.path, requestFile)
        val noteBody = noteSeed.toString().toRequestBody("application/json".toMediaTypeOrNull())

        disposable.add(
            noteApi.uploadNoteWithImage(requestBody, noteBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::parseResponse, this::showNetworkError)
        )
    }

    private fun uploadNoteWithoutImage(noteSeed: NoteSeed) {
        disposable.add(
            noteApi.uploadNote(noteSeed)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::parseResponse, this::showNetworkError)
        )
    }

    private fun isValidNote(noteSeed: NoteSeed): Pair<Boolean, Int> = when {
        noteSeed.title.isEmpty() -> false to R.string.invalid_note_title
        noteSeed.body.isEmpty() -> false to R.string.invalid_note_title
        else -> true to R.string.valid_note
    }

    private fun showNetworkError(throwable: Throwable) {
        pushState(NoteViewState.Error(R.string.upload_error_generic))
    }

    private fun parseResponse(uploadResponse: NoteUploadResponse?) {
        if (uploadResponse != null) {
            pushState(NoteViewState.Error(R.string.upload_success))
        } else {
            showNetworkError(Throwable())
        }
    }
}
