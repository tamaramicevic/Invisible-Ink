package com.invisibleink.note

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.invisibleink.R
import com.invisibleink.architecture.ViewProvider
import com.invisibleink.extensions.findViewOrThrow
import com.invisibleink.extensions.hasCameraPermission
import com.invisibleink.extensions.hasExternalWritePermission
import com.invisibleink.injection.InvisibleInkApplication
import pl.aprilapps.easyphotopicker.ChooserType
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.EasyImage.Callbacks
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import javax.inject.Inject

class NoteFragment : Fragment(), ViewProvider, NotePresenter.ImageSelector {

    @Inject
    lateinit var presenter: NotePresenter
    private lateinit var viewDelegate: NoteViewDelegate
    private var imagePicker: EasyImage? = null

    override fun <T : View> findViewById(id: Int): T = findViewOrThrow(id)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_note, container, false)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as InvisibleInkApplication).appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDelegate = NoteViewDelegate(this)
        presenter.imageSelector = this
        presenter.attach(viewDelegate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detach()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        imagePicker?.handleActivityResult(
            requestCode,
            resultCode,
            data,
            requireActivity(),
            object : Callbacks {
                override fun onCanceled(source: MediaSource) {
                    viewDelegate.render(NoteViewState.Error(R.string.error_adding_image))
                }

                override fun onImagePickerError(error: Throwable, source: MediaSource) {
                    viewDelegate.render(NoteViewState.Error(R.string.error_adding_image))
                }

                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    if (imageFiles.isNotEmpty()) {
                        val image = BitmapFactory.decodeFile(imageFiles.first().file.path)
                        viewDelegate.render(NoteViewState.ImageSelected(image))
                    }
                }
            })
    }

    override fun onAddImageSelected() {
        val validContext = requireContext()
        if (!validContext.hasExternalWritePermission()) {
            viewDelegate.render(NoteViewState.Error(R.string.missing_permission_storage))
            requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        } else if (!validContext.hasCameraPermission()) {
            viewDelegate.render(NoteViewState.Error(R.string.missing_permission_camera))
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 0)
        } else {
            imagePicker = EasyImage.Builder(validContext)
                .setChooserType(ChooserType.CAMERA_AND_GALLERY)
                .setChooserTitle(validContext.getString(R.string.image_picker_title))
                .build().apply { openChooser(this@NoteFragment) }
        }
    }
}
