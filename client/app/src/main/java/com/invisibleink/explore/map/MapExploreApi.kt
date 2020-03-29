package com.invisibleink.explore.map

import com.invisibleink.models.Note
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

data class NoteContainer(val notes: List<Note>)

interface MapExploreApi {

    @POST("retrieve-notes")
    fun fetchNotes(@Body fetchNotesRequest: FetchNotesRequest): Observable<NoteContainer>
}