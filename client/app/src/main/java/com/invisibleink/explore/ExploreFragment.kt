package com.invisibleink.explore

import android.os.Bundle
import android.view.*
import androidx.annotation.IntRange
import androidx.fragment.app.Fragment
import com.invisibleink.R
import com.invisibleink.dashboard.NavigationActivity
import com.invisibleink.explore.ar.ArExploreFragment
import com.invisibleink.explore.map.MapExploreFragment
import com.invisibleink.extensions.doNothingOnBackPress

class ExploreFragment : Fragment(), NavigationActivity.BackPressHandler {

    companion object {
        private const val EXTRA_EXPLORE_MODE = "com.invisibleink.explore.extra_explore_mode"

        fun constructBundle(exploreViewMode: ExploreViewMode) = Bundle().apply {
            putInt(EXTRA_EXPLORE_MODE, exploreViewMode.EXPLORE_MODE_ID)
        }
    }

    enum class ExploreViewMode(val fragmentFactory: () -> Fragment, val EXPLORE_MODE_ID: Int) {
        MAP(::MapExploreFragment, 0),
        AR(::ArExploreFragment, 1);

        companion object {
            fun fromModeId(@IntRange(from = 0, to = 1) exploreModeId: Int): ExploreViewMode {
                return if (exploreModeId == MAP.EXPLORE_MODE_ID) MAP else AR
            }
        }
    }

    private lateinit var exploreViewMode: ExploreViewMode

    override fun onBackPress() = doNothingOnBackPress()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)

        val exploreModeId = arguments?.getInt(EXTRA_EXPLORE_MODE)
        exploreViewMode = if (exploreModeId != null) {
            ExploreViewMode.fromModeId(exploreModeId)
        } else {
            ExploreViewMode.MAP
        }

        return view
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
        this.exploreViewMode = exploreViewMode
        requireActivity().invalidateOptionsMenu()

        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, exploreViewMode.fragmentFactory.invoke())
            .commit()
    }
}