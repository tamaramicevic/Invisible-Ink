package com.invisibleink.dashboard

import android.os.Bundle
import android.view.Menu
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.invisibleink.R
import com.invisibleink.architecture.Router
import com.invisibleink.explore.SearchFilter
import com.invisibleink.explore.ar.ArExploreFragment
import com.invisibleink.explore.map.MapExploreFragment
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
        private val DEFAULT_EXPLORE_TAB = NavigationContent.MapExploreTab()
    }

    interface BackPressHandler {
        fun onBackPress(): Boolean
    }

    private val navigationRouter = NavigationRouter()
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

        data class MapExploreTab(
            val searchFilter: SearchFilter = SearchFilter.EMPTY_FILTER
        ) : NavigationContent(
            R.id.exploreTab,
            {
                MapExploreFragment().apply {
                    arguments = MapExploreFragment.constructBundle(searchFilter)
                }
            })

        data class ArExploreTab(
            val searchFilter: SearchFilter = SearchFilter.EMPTY_FILTER
        ) : NavigationContent(
            R.id.exploreTab,
            {
                ArExploreFragment().apply {
                    arguments = ArExploreFragment.constructBundle(searchFilter)
                }
            })
    }

    private val navigationSelectionListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.exploreTab -> showContent(DEFAULT_EXPLORE_TAB)
                R.id.noteTab -> showContent(NavigationContent.NoteTab)
                R.id.settingsTab -> showContent(NavigationContent.SettingsTab)
            }
            true
        }

    override fun routeTo(destination: NavigationDestination) =
        showContent(navigationRouter.getContent(destination))

    override fun onBackPressed() {
        if (currentFragment?.second?.onBackPress() != true) {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        bottomNavigation = findViewById<BottomNavigationView>(R.id.navigationBar).apply {
            setOnNavigationItemSelectedListener(navigationSelectionListener)
            selectedItemId = R.id.exploreTab
        }

        showContent(DEFAULT_EXPLORE_TAB)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        // Fragments are responsible for making their menu items visible
        menu?.findItem(R.id.refreshItem)?.isVisible = false
        menu?.findItem(R.id.mapExploreItem)?.isVisible = false
        menu?.findItem(R.id.arExploreItem)?.isVisible = false

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.explore_menu, menu)
        return true
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
