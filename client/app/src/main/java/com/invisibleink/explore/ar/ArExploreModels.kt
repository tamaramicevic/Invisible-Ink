package com.invisibleink.explore.ar

import com.invisibleink.architecture.Destination
import com.invisibleink.architecture.ViewEvent
import com.invisibleink.architecture.ViewState

sealed class ArExploreViewState : ViewState

sealed class ArExploreViewEvent : ViewEvent

sealed class ArExploreDestination : Destination
