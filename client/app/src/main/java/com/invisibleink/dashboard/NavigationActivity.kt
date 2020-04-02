package com.invisibleink.dashboard


import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.invisibleink.R
import com.invisibleink.explore.ExploreFragment
import com.invisibleink.note.NoteFragment
import com.invisibleink.settings.SettingsFragment


/**
 * The main activity which hosts a bottom navigation view and switches
 * among the content fragments upon selection.
 */
class NavigationActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var bottomNavigation: BottomNavigationView

    private enum class NavigationContent(val fragmentFactory: () -> Fragment) {
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // This check verifies that the option selected is not from the top bar with an associated id
        if (item == null) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exploreTab -> showContent(NavigationContent.EXPLORE)
            R.id.noteTab -> showContent(NavigationContent.NOTE)
            R.id.settingsTab -> showContent(NavigationContent.SETTINGS)
        }

        return true
    }

    private fun showContent(content: NavigationContent) =
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, content.fragmentFactory.invoke())
            .commit()


    override fun onBackPressed() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ExploreFragment())
            .commit()
    }
}
