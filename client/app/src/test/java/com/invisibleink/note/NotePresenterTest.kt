package com.invisibleink.note

import com.google.android.gms.maps.model.LatLng
import com.invisibleink.R
import com.invisibleink.architecture.Router
import com.invisibleink.dashboard.NavigationDestination
import com.invisibleink.location.LocationProvider
import com.nhaarman.mockitokotlin2.*
import com.testutils.SchedulerRule
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit


/**
 * Tests all business logic for NoteUpload functional requirements.
 */
class NotePresenterTest {

    @get:Rule
    val rule: SchedulerRule = SchedulerRule()

    private lateinit var notePresenter: NotePresenter
    private lateinit var retrofit: Retrofit
    private lateinit var noteApi: NoteApi
    private lateinit var disposable: CompositeDisposable
    private lateinit var locationProvider: LocationProvider
    private lateinit var imageHandler: NotePresenter.ImageHandler
    private lateinit var noteViewDelegate: NoteViewDelegate
    private lateinit var navigationRouter: Router<NavigationDestination>

    // State-less, helper objects re-used among tests
    companion object {
        private val validLocation: LatLng? = LatLng(0.0, 0.0)
        private val invalidLocation: LatLng? = null
        private val validNoteSeed = NoteSeed(title = "some title", body = "some body")
        private val validNoteSeedWithImage =
            NoteSeed(title = "some title", body = "some body", imagePath = "some/path")
        private val validNoteSeedWithLocation =
            validNoteSeed.apply { location = validLocation }
        private val emptyTitleNote = NoteSeed(title = "", body = "some body")
        private val emptyBodyNote = NoteSeed(title = "some title", body = "")
        private val successfulNoteUploadResponse =
            NoteUploadResponse(success = true, noteId = "1", error = null)
        private val successfulImageUploadResponse =
            ImageUploadResponse(success = true, error = null)
    }


    @Before
    fun setUp() {
        // Initialize dependencies as mocks
        locationProvider = mock()
        imageHandler = mock()
        noteViewDelegate = mock()
        disposable = mock()
        noteApi = mock()
        navigationRouter = mock()
        setUpNoteApiReturns(null, null)

        retrofit = mock {
            on { create(NoteApi::class.java) } doReturn noteApi
        }

        // Instantiate class under test
        notePresenter = NotePresenter(retrofit)
        notePresenter.disposable = disposable
        notePresenter.locationProvider = locationProvider
        notePresenter.imageHandler = imageHandler
        notePresenter.navigationRouter = navigationRouter

        notePresenter.attach(noteViewDelegate, notePresenter)
        clearInvocations(noteViewDelegate)
    }

    @Test
    fun `verify presenter sends empty note content on attach`() {
        notePresenter.detach()

        notePresenter.attach(noteViewDelegate)
        verify(noteViewDelegate).render(NoteViewState.Empty)
    }

    @Test
    fun `verify presenter disposes on detach empty note content on attach`() {
        notePresenter.detach()
        verify(disposable).dispose()
    }

    @Test
    fun `verify error state is pushed when uploading invalid note`() {
        notePresenter.onEvent(NoteViewEvent.Upload(emptyTitleNote))
        verify(noteViewDelegate).render(NoteViewState.Error(R.string.invalid_note_title))
    }

    @Test
    fun `verify error state is pushed when location is not available`() {
        setUpLocationProvider(invalidLocation)

        notePresenter.onEvent(NoteViewEvent.Upload(validNoteSeed))
        verify(noteViewDelegate).render(NoteViewState.Error(R.string.error_invalid_device_location))
    }

    @Test
    fun `verify noteApi uploadNote is invoked for valid note without image url`() {
        setUpLocationProvider(validLocation)

        notePresenter.onEvent(NoteViewEvent.Upload(validNoteSeedWithLocation))
        verify(noteApi).uploadNote(validNoteSeedWithLocation)
        verify(noteApi, times(0)).uploadImage(any(), any())
    }

    @Test
    fun `verify noteApi uploadImage is invoked for valid note with image url`() {
        setUpLocationProvider(validLocation)
        val validNoteSeedWithLocation = validNoteSeedWithImage.apply { location = validLocation }
        setUpNoteApiReturns(NoteUploadResponse(success = true, noteId = "1", error = null), null)

        notePresenter.onEvent(NoteViewEvent.Upload(validNoteSeedWithImage))
        verify(noteApi).uploadNote(validNoteSeedWithLocation)
        verify(noteApi).uploadImage(any(), any())
    }

    @Test
    fun `verify imageSelector is invoked when AddImage event is pushed`() {
        notePresenter.onEvent(NoteViewEvent.AddImage)
        verify(imageHandler).onAddImageSelected()
    }

    @Test
    fun `verify note with empty title is invalid`() {
        val (isValid, _) = notePresenter.isValidNote(emptyTitleNote)
        assertFalse(isValid)
    }

    @Test
    fun `verify note with empty body is invalid`() {
        val (isValid, _) = notePresenter.isValidNote(emptyBodyNote)
        assertFalse(isValid)
    }

    @Test
    fun `verify note with title and body are invalid`() {
        val (isValid, _) = notePresenter.isValidNote(validNoteSeed)
        assertTrue(isValid)
    }

    @Test
    fun `verify note with PII detected shows error message`() {
        val piiErrorResponse = NoteUploadResponse(
            success = false,
            noteId = "1",
            error = NoteUploadErrorType.PII_DETECTED
        )
        setUpLocationProvider(validLocation)
        setUpNoteApiReturns(piiErrorResponse, null)

        notePresenter.onEvent(NoteViewEvent.Upload(validNoteSeedWithLocation))
        verify(noteViewDelegate).render(NoteViewState.Error(R.string.upload_error_pii))
    }

    @Test
    fun `verify note with bad sentiment detected shows error message`() {
        val badSentimentResponse = NoteUploadResponse(
            success = false,
            noteId = "1",
            error = NoteUploadErrorType.BAD_SENTIMENT_DETECTED
        )
        setUpLocationProvider(validLocation)
        setUpNoteApiReturns(badSentimentResponse, null)

        notePresenter.onEvent(NoteViewEvent.Upload(validNoteSeedWithLocation))
        verify(noteViewDelegate).render(NoteViewState.Error(R.string.upload_error_bad_sentiment))
    }

    @Test
    fun `verify note with generic error detected shows error message`() {
        val genericErrorResponse = NoteUploadResponse(
            success = false,
            noteId = "1",
            error = NoteUploadErrorType.UPLOAD_FAILED
        )
        setUpLocationProvider(validLocation)
        setUpNoteApiReturns(genericErrorResponse, null)

        notePresenter.onEvent(NoteViewEvent.Upload(validNoteSeedWithLocation))
        verify(noteViewDelegate).render(NoteViewState.Error(R.string.upload_error_generic))
    }

    @Test
    fun `verify successful note upload without image routes to Map explore`() {
        setUpLocationProvider(validLocation)
        setUpNoteApiReturns(successfulNoteUploadResponse, null)

        notePresenter.onEvent(NoteViewEvent.Upload(validNoteSeedWithLocation))
        verify(navigationRouter).routeTo(NavigationDestination.MapExploreTab)
    }

    @Test
    fun `verify successful note upload with image routes to Map explore`() {
        setUpLocationProvider(validLocation)
        setUpNoteApiReturns(successfulNoteUploadResponse, successfulImageUploadResponse)

        notePresenter.onEvent(NoteViewEvent.Upload(validNoteSeedWithLocation))
        verify(navigationRouter).routeTo(NavigationDestination.MapExploreTab)
    }

    private fun setUpLocationProvider(location: LatLng?) =
        whenever(locationProvider.getCurrentLocation()) doReturn location

    private fun setUpNoteApiReturns(
        noteResponse: NoteUploadResponse?,
        imageResponse: ImageUploadResponse?
    ) {
        val noteResponseObsv = if (noteResponse != null) {
            Single.just(noteResponse).toObservable()
        } else {
            Observable.never<NoteUploadResponse>()
        }

        val imageResponseObsv = if (imageResponse != null) {
            Single.just(imageResponse).toObservable()
        } else {
            Observable.never<ImageUploadResponse>()
        }

        whenever(noteApi.uploadNote(any())) doReturn noteResponseObsv
        whenever(noteApi.uploadImage(any(), any())) doReturn imageResponseObsv
    }
}
