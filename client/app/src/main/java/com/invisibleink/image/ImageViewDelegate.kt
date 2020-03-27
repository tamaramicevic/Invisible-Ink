package com.invisibleink.image

import android.content.Context
import android.util.Log
import android.widget.ImageView
import androidx.annotation.StringRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.invisibleink.R
import com.invisibleink.architecture.BaseViewDelegate
import com.invisibleink.architecture.ViewProvider

class ImageViewDelegate(viewProvider: ViewProvider) :
    BaseViewDelegate<ImageViewState, ImageViewEvent, ImageDestination>(viewProvider) {

    lateinit var imageURL: String
    var context: Context? = null

    override fun render(viewState: ImageViewState): Unit? = when (viewState) {
        is ImageViewState.ShowImage -> showImage()
    }

    private fun showImage() {
        var image: ImageView = viewProvider.findViewById(R.id.note_image)

       context?.let {
            Glide.with(it) //1
                .load(imageURL)
                .placeholder(R.drawable.ic_photo_camera_24dp)
                .error(R.drawable.ic_report_foreground)
                .skipMemoryCache(true) //2
                .diskCacheStrategy(DiskCacheStrategy.NONE) //3
                .transform(FitCenter()) //4
                .into(image)
        }

    }
}