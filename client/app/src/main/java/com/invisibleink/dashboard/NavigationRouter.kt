package com.invisibleink.dashboard

import com.invisibleink.dashboard.NavigationActivity.NavigationContent

class NavigationRouter {

    fun getContent(destination: NavigationDestination): NavigationContent {
        return when (destination) {
            is NavigationDestination.MapExploreTab ->
                NavigationContent.MapExploreTab(destination.searchFilter)
            is NavigationDestination.ArExploreTab ->
                NavigationContent.ArExploreTab(destination.searchFilter)
            is NavigationDestination.ImageTab -> NavigationContent.ImageTab(destination.imageUrl)
            is NavigationDestination.NoteUploadTab -> NavigationContent.NoteTab
            is NavigationDestination.SettingsTab -> NavigationContent.SettingsTab
        }
    }
}