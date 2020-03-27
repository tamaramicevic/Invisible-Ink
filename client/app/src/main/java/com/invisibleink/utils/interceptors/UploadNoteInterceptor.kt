package com.invisibleink.utils.interceptors

import com.invisibleink.utils.gson.createGson
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject

class UploadNoteInterceptor : Interceptor {

    companion object {
        private const val SUCCESS_CODE = 200
        private const val CONTENT_TYPE_JSON = "application/json"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = generateResponse().toString()
        return chain.proceed(chain.request())
            .newBuilder()
            .code(SUCCESS_CODE)
            .protocol(Protocol.HTTP_2).message(response)
            .body(response.toByteArray().toResponseBody(CONTENT_TYPE_JSON.toMediaTypeOrNull()))
            .addHeader("content-type", CONTENT_TYPE_JSON).build()
    }

    private fun generateResponse(
        success: Boolean = true,
        error: String = "Error uploading note!",
        noteId: Int = (0..1000).random()
    ): JSONObject =
        JSONObject().apply {
            put("success", success)
            put("error", error)
            put("noteId", noteId)
        }
}
