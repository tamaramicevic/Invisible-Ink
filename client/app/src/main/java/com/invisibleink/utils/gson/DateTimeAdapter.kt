package com.invisibleink.utils.gson

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.lang.IllegalArgumentException

class DateTimeAdapter : TypeAdapter<DateTime>() {

    companion object {
        private val DATE_FORMATTER = ISODateTimeFormat.dateTime().withZoneUTC()

        fun parseIsoDate(dateString: String?): DateTime =
            dateString?.let { DateTime.parse(it, DATE_FORMATTER) } ?: DateTime.now()
    }

    override fun write(output: JsonWriter?, dateTime: DateTime?) {
        if (dateTime != null) {
            output?.value(DATE_FORMATTER.print(dateTime))
        } else {
            output?.nullValue()
        }
    }

    override fun read(input: JsonReader?): DateTime = parseIsoDate(input?.nextString())
}