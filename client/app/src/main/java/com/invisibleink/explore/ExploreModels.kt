package com.invisibleink.explore

import com.invisibleink.architecture.Destination
import com.invisibleink.architecture.ViewEvent
import com.invisibleink.architecture.ViewState

sealed class ExploreViewState : ViewState

sealed class ExploreViewEvent : ViewEvent

sealed class ExploreDestination : Destination
