package com.invisibleink.utils.interceptors

import com.invisibleink.extensions.randomDouble
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
        private const val LAT_LNG_DEVIATION = 0.0005
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val lat = chain.request().url.queryParameter("latitude")?.toDouble() ?: 53.523
        val lng = chain.request().url.queryParameter("longitude")?.toDouble() ?: -113.520

        val response = generateNotes(lat, lng)
        return chain.proceed(chain.request())
            .newBuilder()
            .code(SUCCESS_CODE)
            .protocol(Protocol.HTTP_2).message(response)
            .body(response.toByteArray().toResponseBody(CONTENT_TYPE_JSON.toMediaTypeOrNull()))
            .addHeader("content-type", CONTENT_TYPE_JSON).build()

    }

    private fun generateNotes(lat: Double, lng: Double, count: Int = 10): String =
        (0 until count).map { id ->
            generateNote(lat, lng, id)
        }.toString()


    private fun generateNote(lat: Double, lng: Double, id: Int = (0..1000).random()): JSONObject =
        JSONObject().apply {
            put("title", "Title of note $id")
            put("body", "Body of note $id")
            put("expiration", "2020-01-15T22:55:41.492Z")
            put("imageUrl", JSONObject.NULL)
            put("location", JSONObject().apply {
                put("latitude", randomDouble(lat.minus(LAT_LNG_DEVIATION), lat.plus(LAT_LNG_DEVIATION)))
                put("longitude", randomDouble(lng.minus(LAT_LNG_DEVIATION), lng.plus(LAT_LNG_DEVIATION)))
            })
            put("score", (-10..10).random())
        }
}