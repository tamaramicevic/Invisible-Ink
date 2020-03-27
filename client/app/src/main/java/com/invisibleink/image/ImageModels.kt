package com.invisibleink.image

import androidx.annotation.StringRes
import com.invisibleink.architecture.Destination
import com.invisibleink.architecture.ViewEvent
import com.invisibleink.architecture.ViewState

sealed class ImageViewState : ViewState {
    object ShowImage : ImageViewState()
}

sealed class ImageViewEvent : ViewEvent

sealed class ImageDestination : Destination
