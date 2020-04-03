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
        private const val EXTRA_EXPLORE_SEARCH_FILTER =
            "com.invisibleink.explore.extra_search_filter"

        fun constructBundle(
            exploreViewMode: ExploreViewMode,
            searchFilter: SearchFilter = SearchFilter.EMPTY_FILTER
        ) =
            Bundle().apply {
                putInt(EXTRA_EXPLORE_MODE, exploreViewMode.EXPLORE_MODE_ID)
                putSerializable(EXTRA_EXPLORE_SEARCH_FILTER, searchFilter)
            }
    }

    enum class ExploreViewMode(
        val fragmentFactory: (SearchFilter) -> Fragment,
        val EXPLORE_MODE_ID: Int
    ) {
        MAP({ filter ->
            MapExploreFragment().apply { arguments = MapExploreFragment.constructBundle(filter) }
        }, 0),
        AR({ filter ->
            ArExploreFragment().apply { arguments = ArExploreFragment.constructBundle(filter) }
        }, 1);

        companion object {
            val DEFAULT_VIEW_MODE = MAP

            fun fromModeId(@IntRange(from = 0, to = 1) exploreModeId: Int): ExploreViewMode {
                return if (exploreModeId == MAP.EXPLORE_MODE_ID) MAP else AR
            }
        }
    }

    private lateinit var exploreViewMode: Pair<ExploreViewMode, SearchFilter>

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
        val exploreFilter =
            (arguments?.getSerializable(EXTRA_EXPLORE_SEARCH_FILTER) as? SearchFilter)
                ?: SearchFilter.EMPTY_FILTER

        exploreViewMode = if (exploreModeId != null) {
            ExploreViewMode.fromModeId(exploreModeId) to exploreFilter
        } else {
            ExploreViewMode.MAP to exploreFilter
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

        if (exploreViewMode.first == ExploreViewMode.MAP) {
            mapExploreItem.isVisible = false
            arExploreItem.isVisible = true
        } else {
            mapExploreItem.isVisible = true
            arExploreItem.isVisible = false
        }

        super.onPrepareOptionsMenu(menu)
    }

    private fun showExploreViewMode(exploreViewMode: Pair<ExploreViewMode, SearchFilter>) {
        this.exploreViewMode = exploreViewMode
        requireActivity().invalidateOptionsMenu()

        val (exploreMode, exploreFilter) = exploreViewMode

        val exploreFragment = exploreMode.fragmentFactory.invoke(exploreFilter)
        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, exploreFragment)
            .commit()
    }
}