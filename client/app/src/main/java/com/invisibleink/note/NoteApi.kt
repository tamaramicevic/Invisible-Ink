package com.invisibleink.note

import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

interface NoteApi {

    @POST("upload")
    fun uploadNote(@Body noteSeed: NoteSeed): Observable<NoteUploadResponse>

    @Multipart
    @POST("photo/{noteId}")
    fun uploadImage(@Path(value = "noteId") noteId: String, @Part image: MultipartBody.Part): Observable<ImageUploadResponse>
}

data class NoteUploadResponse(val success: Boolean, val error: String?, val noteId: String?)

data class ImageUploadResponse(val success: Boolean, val error: String?)