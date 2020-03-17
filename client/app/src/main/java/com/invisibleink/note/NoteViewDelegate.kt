package com.invisibleink.note

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.annotation.StringRes
import com.invisibleink.R
import com.invisibleink.architecture.BaseViewDelegate
import com.invisibleink.architecture.ViewProvider
import com.invisibleink.extensions.showSnackbar
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class NoteViewDelegate(viewProvider: ViewProvider) :
    BaseViewDelegate<NoteViewState, NoteViewEvent, NoteDestination>(viewProvider) {

    companion object {
        private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormat.forPattern("yyyy/MM/dd")
    }

    private val root: ViewGroup = viewProvider.findViewById(R.id.root)
    private val context = root.context
    private val title: EditText = viewProvider.findViewById(R.id.title)
    private val body: EditText = viewProvider.findViewById(R.id.body)
    private val addPhotoButton: ImageButton = viewProvider.findViewById(R.id.addPhoto)
    private val uploadButton: Button = viewProvider.findViewById(R.id.uploadButton)
    private val expirationButton: Button = viewProvider.findViewById(R.id.expirationButton)
    private var expirationDate: DateTime? = null

    init {
        uploadButton.setOnClickListener { pushEvent(NoteViewEvent.Upload(composeNote())) }
        expirationButton.setOnClickListener { showDatePicker() }
        addPhotoButton.setOnClickListener { showAddPhoto() }
    }

    override fun render(viewState: NoteViewState): Unit? = when (viewState) {
        is NoteViewState.Empty -> clearNoteContent()
        is NoteViewState.Draft -> showDraftContent(viewState.draft)
        is NoteViewState.ImageSelected -> showImageThumbnail(viewState.image)
        is NoteViewState.Error -> showMessage(viewState.message)
    }

    private fun clearNoteContent() {
        title.text.clear()
        body.text.clear()
        addPhotoButton.setImageResource(R.drawable.ic_add_a_photo_black_24dp)
    }

    private fun showDraftContent(noteSeed: NoteSeed) {
        title.setText(noteSeed.title)
        body.setText(noteSeed.body)

        if (noteSeed.image != null) {
            addPhotoButton.setImageBitmap(noteSeed.image)
        }

        if (noteSeed.expiration != null) {
            expirationDate = noteSeed.expiration
            showExpirationDate()
        }
    }

    private fun showImageThumbnail(image: Bitmap) = addPhotoButton.setImageBitmap(image)

    private fun showMessage(@StringRes message: Int) = title.showSnackbar(message)

    private fun showDatePicker() {
        DatePickerDialog(context).apply {
            setOnDateSetListener { _, year, month, day ->
                expirationDate = DateTime()
                    .withYear(year)
                    .withMonthOfYear(month)
                    .withDayOfMonth(day)

                showExpirationDate()
            }
            show()
        }
    }

    private fun showAddPhoto() = pushEvent(NoteViewEvent.AddImage)

    private fun composeNote(): NoteSeed = NoteSeed(
        title = title.text.toString(),
        body = body.text.toString(),
        expiration = expirationDate
    )

    private fun showExpirationDate() {
        val expirationString = expirationDate?.toString(DATE_FORMATTER)
        if (expirationString != null) {
            expirationButton.text =
                context.getString(R.string.expiration_date_format, expirationString)
        } else {
            expirationButton.text = context.getString(R.string.expiration_date_default)
        }
    }
}
