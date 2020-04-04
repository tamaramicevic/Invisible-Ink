package com.invisibleink.dashboard

import com.invisibleink.architecture.Destination
import com.invisibleink.explore.SearchFilter

sealed class NavigationDestination : Destination {
    object NoteUploadTab : NavigationDestination()
    object SettingsTab : NavigationDestination()
    data class ImageTab(val imageUrl: String?) : NavigationDestination()
    data class MapExploreTab(
        val searchFilter: SearchFilter = SearchFilter.EMPTY_FILTER
    ) : NavigationDestination()

    data class ArExploreTab(
        val searchFilter: SearchFilter = SearchFilter.EMPTY_FILTER
    ) : NavigationDestination()
}