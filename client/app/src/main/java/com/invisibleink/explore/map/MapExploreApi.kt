package com.invisibleink.explore.map

import com.invisibleink.models.Note
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface MapExploreApi {

    @POST("notes")
    fun fetchNotes(@Body fetchNotesRequest: FetchNotesRequest): Observable<List<Note>>
}