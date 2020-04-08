package com.invisibleink.explore.ar

import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.invisibleink.models.Note
import com.nhaarman.mockitokotlin2.mock
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ArExploreRenderTest {

    private lateinit var arFragment: ArExploreFragment

    companion object {
        private var currentLocation = LatLng(53.601606, -113.445944)
        private var defaultNotes: List<Note> = listOf(
            Note(id = "1", title = "title", body = "filtered in", location = currentLocation, score = 0),
            Note(id = "2", title = "title", body = "filtered out", location = LatLng(53.599849, -113.445976), score = -3),
            Note(id = "3", title = "title", body = "filtered in", location = LatLng(53.601606, -113.445944), score = 4),
            Note(id = "4", title = "title", body = "borderline", location = LatLng(53.601606, -113.445944), score = 2)
        )

        private var distances: Map<Note, Float> = mapOf(
            defaultNotes[0] to 0.0F,
            defaultNotes[1] to 123.45F,
            defaultNotes[2] to 76.23F,
            defaultNotes[3] to 99.9F
        )
    }

    @Before
    fun setUp() {
        arFragment = ArExploreFragment()
    }

    @Test
    fun `filtering notes based on distance from location`() {
        val filteredNotes: List<Note> = arFragment.filterNotes(distances)
        assertTrue(filteredNotes.size == 3)
    }
}