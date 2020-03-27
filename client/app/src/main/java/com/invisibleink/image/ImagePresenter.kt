package com.invisibleink.image

import com.invisibleink.architecture.BasePresenter
import retrofit2.Retrofit
import javax.inject.Inject

class ImagePresenter @Inject constructor(
    retrofit: Retrofit
) :
    BasePresenter<ImageViewState, ImageViewEvent, ImageDestination>() {

    override fun onEvent(viewEvent: ImageViewEvent) {}

    override fun onAttach() {
        super.onAttach()
        pushState(ImageViewState.ShowImage)
    }
}