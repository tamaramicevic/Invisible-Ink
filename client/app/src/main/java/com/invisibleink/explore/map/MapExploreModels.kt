package com.invisibleink.explore.map

import com.invisibleink.architecture.Destination
import com.invisibleink.architecture.ViewEvent
import com.invisibleink.architecture.ViewState

sealed class MapExploreViewState : ViewState

sealed class MapExploreViewEvent : ViewEvent

sealed class MapExploreDestination : Destination
