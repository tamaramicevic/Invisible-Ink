package com.invisibleink.note

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface NoteApi {

    @POST("upload")
    fun uploadNote(@Body noteSeed: NoteSeed): Observable<NoteUploadResponse>

    @Multipart
    @POST("upload")
    fun uploadNoteWithImage(@Part image: MultipartBody.Part, @Part("note") note: RequestBody): Observable<NoteUploadResponse>
}

data class NoteUploadResponse(val message: String)