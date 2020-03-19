package com.invisibleink.explore.ar

import com.invisibleink.models.Note
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ArExploreApi {

    @GET("notes")
    fun fetchNotes(
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double
    ): Observable<List<Note>>
}