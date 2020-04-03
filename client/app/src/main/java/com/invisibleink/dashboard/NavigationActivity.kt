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

    companion object {
        private const val FRAGMENT_TAG = "com.invisibleink.dashboard.fragment_tag"
    }

    interface BackPressHandler {
        fun onBackPress(): Boolean
    }

    private lateinit var bottomNavigation: BottomNavigationView
    private var currentFragment: Pair<Fragment, BackPressHandler>? = null

    private enum class NavigationContent(
        @IdRes val navItemId: Int, val fragmentFactory: () -> Fragment,
        var arguments: Bundle
    ) {
        MAP_EXPLORE(
            R.id.exploreTab,
            ::ExploreFragment,
            ExploreFragment.constructBundle(ExploreViewMode.MAP)
        ),
        AR_EXPLORE(
            R.id.exploreTab,
            ::ExploreFragment,
            ExploreFragment.constructBundle(ExploreViewMode.AR)
        ),
        IMAGE_VIEW(
            R.id.exploreTab,
            ::ImageFragment,
            Bundle()
        ),
        NOTE(
            R.id.noteTab,
            ::NoteFragment,
            Bundle()
        ),
        SETTINGS(
            R.id.settingsTab,
            ::SettingsFragment,
            Bundle()
        ),
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
            is NavigationDestination.ImageTab -> {
                showContent(NavigationContent.IMAGE_VIEW.apply {
                    arguments = ImageFragment.constructBundle(destination.imageUrl)
                })
            }
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

        showContent(NavigationContent.MAP_EXPLORE)
    }

    private fun showContent(content: NavigationContent) {
        navigationBar.setOnNavigationItemSelectedListener(null)

        val fragment = content.fragmentFactory.invoke().apply { arguments = content.arguments }
        // Every fragment is required to be able to handle back presses
        currentFragment = fragment to (fragment as BackPressHandler)

        navigationBar.selectedItemId = content.navItemId
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment, FRAGMENT_TAG)
            .commit()

        navigationBar.setOnNavigationItemSelectedListener(navigationSelectionListener)
    }
}
