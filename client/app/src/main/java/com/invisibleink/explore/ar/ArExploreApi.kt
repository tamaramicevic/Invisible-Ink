package com.invisibleink.explore.ar

import com.invisibleink.models.Note
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface ArExploreApi {

    @POST("notes")
    fun fetchNotes(@Body fetchNotesRequest: FetchNotesRequest): Observable<List<Note>>
}