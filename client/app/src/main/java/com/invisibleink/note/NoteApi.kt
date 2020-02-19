package com.invisibleink.note

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NoteApi {

    @POST("upload")
    fun uploadNote(@Body note: Note): Observable<Response<ResponseBody>>
}