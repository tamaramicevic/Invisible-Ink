package com.invisibleink.note

import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import com.invisibleink.R
import com.invisibleink.architecture.BasePresenter
import com.invisibleink.architecture.Router
import com.invisibleink.dashboard.NavigationDestination
import com.invisibleink.location.LocationProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import java.io.File
import javax.inject.Inject

class NotePresenter @Inject constructor(retrofit: Retrofit) :
    BasePresenter<NoteViewState, NoteViewEvent, NoteDestination>(),
    Router<NoteDestination> {

    interface ImageHandler {
        fun onAddImageSelected()
    }

    companion object {
        private val TAG = NotePresenter::class.java.canonicalName
    }

    private val noteApi = retrofit.create(NoteApi::class.java)
    @VisibleForTesting
    internal var disposable = CompositeDisposable()
    var imageHandler: ImageHandler? = null
    var locationProvider: LocationProvider? = null
    var navigationRouter: Router<NavigationDestination>? = null

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

        uploadNoteContent(noteSeed.apply { this.location = location })
    }

    private fun uploadImageContent(noteId: String, validImagePath: String) {
        val imageFile = File(validImagePath)
        val fileBody = imageFile.asRequestBody(("image".toMediaTypeOrNull()))
        val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, fileBody)

        disposable.add(
            noteApi.uploadImage(noteId, imagePart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::parseImageUploadResponse, this::showNetworkError)
        )
    }

    private fun uploadNoteContent(noteSeed: NoteSeed) {
        disposable.add(
            noteApi.uploadNote(noteSeed)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { noteResponse: NoteUploadResponse? ->
                        this.parseNoteUploadResponse(noteResponse, noteSeed.imagePath)
                    },
                    this::showNetworkError
                )
        )
    }

    @VisibleForTesting
    internal fun isValidNote(noteSeed: NoteSeed): Pair<Boolean, Int> = when {
        noteSeed.title.isEmpty() -> false to R.string.invalid_note_title
        noteSeed.body.isEmpty() -> false to R.string.invalid_note_body
        else -> true to R.string.empty_string
    }

    private fun showNetworkError(throwable: Throwable) {
        pushState(NoteViewState.Error(R.string.upload_error_generic))
    }

    private fun parseNoteUploadResponse(uploadResponse: NoteUploadResponse?, imagePath: String?) {
        when {
            uploadResponse == null -> showNetworkError(Throwable())
            uploadResponse.success -> {
                if (imagePath != null && uploadResponse.noteId != null) {
                    uploadImageContent(uploadResponse.noteId, imagePath)
                } else {
                    pushState(NoteViewState.Message(R.string.upload_note_success))
                    routeTo(NoteDestination.MapExplore)
                }
            }
            else -> {
                val errorMessage = parseNoteUploadErrorMessage(uploadResponse.error)
                pushState(NoteViewState.Error(errorMessage))
            }
        }
    }

    private fun parseImageUploadResponse(uploadResponse: ImageUploadResponse?) {
        if (uploadResponse != null && uploadResponse.success) {
            pushState(NoteViewState.Message(R.string.upload_image_success))
            routeTo(NoteDestination.MapExplore)
        } else {
            pushState(NoteViewState.Error(R.string.upload_image_error_generic))
        }
    }

    @StringRes
    private fun parseNoteUploadErrorMessage(error: NoteUploadErrorType?) =
        when (error) {
            null -> R.string.upload_error_generic
            NoteUploadErrorType.PII_DETECTED -> R.string.upload_error_pii
            NoteUploadErrorType.BAD_SENTIMENT_DETECTED -> R.string.upload_error_bad_sentiment
            else -> R.string.upload_error_generic
        }

    override fun routeTo(destination: NoteDestination) {
        when (destination) {
            is NoteDestination.EmptyNote -> pushState(NoteViewState.Empty)
            is NoteDestination.MapExplore -> navigationRouter?.routeTo(NavigationDestination.MapExploreTab)
        }
    }
}
