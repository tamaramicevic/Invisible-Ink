package com.invisibleink.dashboard

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.invisibleink.R
import com.invisibleink.architecture.Router
import com.invisibleink.explore.ExploreFragment
import com.invisibleink.explore.ExploreFragment.ExploreViewMode
import com.invisibleink.explore.SearchFilter
import com.invisibleink.image.ImageFragment
import com.invisibleink.note.NoteFragment
import com.invisibleink.settings.SettingsFragment
import kotlinx.android.synthetic.main.activity_dashboard.*

/**
 * The main activity which hosts a bottom navigation view and switches
 * among the content fragments upon selection.
 */
class NavigationActivity : AppCompatActivity(),
    Router<NavigationDestination> {

    interface BackPressHandler {
        fun onBackPress(): Boolean
    }

    private lateinit var bottomNavigation: BottomNavigationView
    private var currentFragment: Pair<Fragment, BackPressHandler>? = null

    sealed class NavigationContent(
        @IdRes val navItemId: Int,
        val fragmentFactory: () -> Fragment
    ) {
        object SettingsTab : NavigationContent(R.id.settingsTab, ::SettingsFragment)
        object NoteTab : NavigationContent(R.id.noteTab, ::NoteFragment)

        data class ImageTab(
            val imageUrl: String?
        ) : NavigationContent(
            R.id.exploreTab,
            {
                ImageFragment().apply { arguments = ImageFragment.constructBundle(imageUrl) }
            })

        data class ExploreTab(
            val exploreViewMode: ExploreViewMode,
            val searchFilter: SearchFilter = SearchFilter.EMPTY_FILTER
        ) : NavigationContent(
            R.id.exploreTab,
            {
                ExploreFragment().apply {
                    arguments = ExploreFragment.constructBundle(exploreViewMode, searchFilter)
                }
            })
    }

    private val navigationSelectionListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.exploreTab -> showContent(NavigationContent.ExploreTab(ExploreViewMode.DEFAULT_VIEW_MODE))
                R.id.noteTab -> showContent(NavigationContent.NoteTab)
                R.id.settingsTab -> showContent(NavigationContent.SettingsTab)
            }
            true
        }

    override fun routeTo(destination: NavigationDestination) {
        when (destination) {
            is NavigationDestination.MapExploreTab -> showContent(
                NavigationContent.ExploreTab(
                    ExploreViewMode.MAP,
                    destination.searchFilter
                )
            )
            is NavigationDestination.ArExploreTab -> showContent(
                NavigationContent.ExploreTab(
                    ExploreViewMode.AR,
                    destination.searchFilter
                )
            )
            is NavigationDestination.ImageTab -> showContent(NavigationContent.ImageTab(destination.imageUrl))
            is NavigationDestination.NoteUploadTab -> showContent(NavigationContent.NoteTab)
            is NavigationDestination.SettingsTab -> showContent(NavigationContent.SettingsTab)
        }
    }

    override fun onBackPressed() {
        if (currentFragment?.second?.onBackPress() != true) {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setSupportActionBar(findViewById(R.id.toolbar))
        bottomNavigation = findViewById<BottomNavigationView>(R.id.navigationBar).apply {
            setOnNavigationItemSelectedListener(navigationSelectionListener)
            selectedItemId = R.id.exploreTab
        }

        showContent(NavigationContent.ExploreTab(ExploreViewMode.DEFAULT_VIEW_MODE))
    }

    private fun showContent(content: NavigationContent) {
        navigationBar.setOnNavigationItemSelectedListener(null)

        val fragment = content.fragmentFactory.invoke()
        currentFragment = fragment to (fragment as BackPressHandler)

        navigationBar.selectedItemId = content.navItemId
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()

        navigationBar.setOnNavigationItemSelectedListener(navigationSelectionListener)
    }
}
