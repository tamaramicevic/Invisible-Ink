package com.invisibleink.dashboard

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.invisibleink.R
import com.invisibleink.explore.ExploreFragment
import com.invisibleink.favorites.FavoritesFragment
import com.invisibleink.note.NoteFragment
import com.invisibleink.settings.SettingsFragment

/**
 * The main activity which hosts a bottom navigation view and switches
 * among the content fragments upon selection.
 */
class NavigationActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var bottomNavigation: BottomNavigationView

    private enum class NavigationContent(val createFragment: () -> Fragment) {
        FAVORITES(::FavoritesFragment),
        EXPLORE(::ExploreFragment),
        NOTE(::NoteFragment),
        SETTINGS(::SettingsFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        bottomNavigation = findViewById<BottomNavigationView>(R.id.navigationBar).apply {
            setOnNavigationItemSelectedListener(this@NavigationActivity)
            selectedItemId = R.id.exploreTab
        }

        showContent(NavigationContent.EXPLORE)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exploreTab -> showContent(NavigationContent.EXPLORE)
            R.id.favoritesTab -> showContent(NavigationContent.FAVORITES)
            R.id.noteTab -> showContent(NavigationContent.NOTE)
            R.id.settingsTab -> showContent(NavigationContent.SETTINGS)
        }

        return true
    }

    private fun showContent(content: NavigationContent) =
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, content.createFragment.invoke())
            .commit()
}
