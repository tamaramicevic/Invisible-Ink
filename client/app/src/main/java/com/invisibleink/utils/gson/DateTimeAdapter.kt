package com.invisibleink.utils.gson

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

class DateTimeAdapter : TypeAdapter<DateTime>() {

    companion object {
        private val DATE_FORMATTER = ISODateTimeFormat.dateTime().withZoneUTC()

        fun parseIsoDate(dateString: String?): DateTime =
            dateString?.let { DateTime.parse(it, DATE_FORMATTER) } ?: DateTime.now()
    }

    override fun write(out: JsonWriter?, dateTime: DateTime?) {
        if (dateTime != null) {
            out?.value(DATE_FORMATTER.print(dateTime))
        } else {
            out?.nullValue()
        }
    }

    override fun read(`in`: JsonReader?): DateTime = parseIsoDate(`in`?.nextString())
}