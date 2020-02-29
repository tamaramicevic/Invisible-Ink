package com.invisibleink.utils.gson

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.joda.time.DateTime

/**
 * Helper to create [Gson] instances that know how to (de)serialize our
 * custom client model from network responses.
 */
fun createGson(): Gson =
    GsonBuilder()
        .registerTypeAdapter(DateTime::class.java, DateTimeAdapter())
        .serializeNulls()
        .create()