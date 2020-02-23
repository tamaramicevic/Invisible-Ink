package com.invisibleink.explore

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.invisibleink.R
import com.invisibleink.explore.ar.ArExploreFragment
import com.invisibleink.explore.map.MapExploreFragment

class ExploreFragment : Fragment() {

    enum class ExploreViewMode { MAP, AR }

    private var exploreViewMode = ExploreViewMode.MAP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onStart() {
        super.onStart()
        showExploreViewMode(exploreViewMode)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.explore_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val mapExploreItem = menu.findItem(R.id.mapExploreItem)
        val arExploreItem = menu.findItem(R.id.arExploreItem)

        if (exploreViewMode == ExploreViewMode.MAP) {
            mapExploreItem.isVisible = false
            arExploreItem.isVisible = true
        } else {
            mapExploreItem.isVisible = true
            arExploreItem.isVisible = false
        }

        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.mapExploreItem -> {
            showExploreViewMode(ExploreViewMode.MAP)
            true
        }
        R.id.arExploreItem -> {
            showExploreViewMode(ExploreViewMode.AR)
            true
        }
        else ->
            super.onOptionsItemSelected(item)
    }

    private fun showExploreViewMode(exploreViewMode: ExploreViewMode) {
        requireActivity().invalidateOptionsMenu()
        this.exploreViewMode = exploreViewMode
        val exploreFragment: Fragment = if (exploreViewMode == ExploreViewMode.MAP) {
            MapExploreFragment()
        } else {
            ArExploreFragment()
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, exploreFragment)
            .commit()
    }
}