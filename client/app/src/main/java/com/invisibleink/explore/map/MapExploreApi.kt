package com.invisibleink.explore.map

import com.invisibleink.models.Note
import io.reactivex.Observable
import retrofit2.http.GET

interface MapExploreApi {

    @GET("notes")
    fun fetchNotes(): Observable<List<Note>>
}