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
import com.invisibleink.note.NoteFragment
import com.invisibleink.settings.SettingsFragment
import kotlinx.android.synthetic.main.activity_dashboard.*

/**
 * The main activity which hosts a bottom navigation view and switches
 * among the content fragments upon selection.
 */
class NavigationActivity : AppCompatActivity(),
    Router<NavigationDestination> {

    private lateinit var bottomNavigation: BottomNavigationView

    private enum class NavigationContent(@IdRes val navItemId: Int, val fragmentFactory: () -> Fragment) {
        MAP_EXPLORE(R.id.exploreTab, { ExploreFragment(ExploreViewMode.MAP.CHILD_FRAGMENT_ID) }),
        AR_EXPLORE(R.id.exploreTab, { ExploreFragment(ExploreViewMode.AR.CHILD_FRAGMENT_ID) }),
        NOTE(R.id.noteTab, ::NoteFragment),
        SETTINGS(R.id.settingsTab, ::SettingsFragment)
    }

    private val navigationSelectionListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.exploreTab -> showContent(NavigationContent.MAP_EXPLORE)
                R.id.noteTab -> showContent(NavigationContent.NOTE)
                R.id.settingsTab -> showContent(NavigationContent.SETTINGS)
            }
            true
        }

    override fun routeTo(destination: NavigationDestination) {
        when (destination) {
            is NavigationDestination.MapExploreTab -> showContent(NavigationContent.MAP_EXPLORE)
            is NavigationDestination.ArExploreTab -> showContent(NavigationContent.AR_EXPLORE)
            is NavigationDestination.NoteUploadTab -> showContent(NavigationContent.NOTE)
            is NavigationDestination.SettingsTab -> showContent(NavigationContent.SETTINGS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        bottomNavigation = findViewById<BottomNavigationView>(R.id.navigationBar).apply {
            setOnNavigationItemSelectedListener(navigationSelectionListener)
            selectedItemId = R.id.exploreTab
        }

        showContent(NavigationContent.MAP_EXPLORE)
    }

    private fun showContent(content: NavigationContent) {
        navigationBar.setOnNavigationItemSelectedListener(null)
        navigationBar.selectedItemId = content.navItemId
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, content.fragmentFactory.invoke())
            .commit()
        navigationBar.setOnNavigationItemSelectedListener(navigationSelectionListener)
    }
}
