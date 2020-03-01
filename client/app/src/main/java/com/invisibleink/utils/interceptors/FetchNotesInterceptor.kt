package com.invisibleink.utils.interceptors

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject

class FetchNotesInterceptor : Interceptor {

    companion object {
        private const val SUCCESS_CODE = 200
        private const val CONTENT_TYPE_JSON = "application/json"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = generateNotes()
        return chain.proceed(chain.request()).newBuilder()
            .code(SUCCESS_CODE)
            .protocol(Protocol.HTTP_2).message(response)
            .body(response.toByteArray().toResponseBody(CONTENT_TYPE_JSON.toMediaTypeOrNull()))
            .addHeader("content-type", CONTENT_TYPE_JSON).build()
    }

    private fun generateNotes(count: Int = 10): String =
        (0 until count).map { id ->
            generateNote(id)
        }.toString()


    private fun generateNote(id: Int = (0..1000).random()): JSONObject =
        JSONObject().apply {
            put("title", "Title of note $id")
            put("body", "Body of note $id")
            put("expiration", "2020-01-15T22:55:41.492Z")
            put("imageUrl", JSONObject.NULL)
            put("latitude", 53.0)
            put("longitude", -113.2425784)
            put("score", (-10..10).random())
        }
}