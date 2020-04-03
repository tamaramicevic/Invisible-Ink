package com.invisibleink.dashboard

import com.invisibleink.architecture.Destination

sealed class NavigationDestination : Destination {
    object MapExploreTab: NavigationDestination()
    object ArExploreTab: NavigationDestination()
    object NoteUploadTab: NavigationDestination()
    object SettingsTab: NavigationDestination()
    data class ImageTab(val imageUrl: String?): NavigationDestination()
}