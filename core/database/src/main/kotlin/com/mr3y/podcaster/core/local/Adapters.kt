package com.mr3y.podcaster.core.local

import app.cash.sqldelight.ColumnAdapter
import com.mr3y.podcaster.core.model.Genre

object GenresColumnAdapter : ColumnAdapter<List<Genre>, String> {

    override fun decode(databaseValue: String): List<Genre> {
        // deserializes the genres stored in the following format [(genre1Id, "label1"), (genre2Id, "label2")]
        return databaseValue
            .removePrefix("[")
            .removeSuffix("]")
            .replace("(", "")
            .replace(")", "")
            .split(", ")
            .windowed(2, step = 2)
            .map { (id, label) ->
                Genre(id.toInt(), label.replace("\"", ""))
            }
    }

    override fun encode(value: List<Genre>): String {
        // Serializes the list of genres into the following text format/pattern: [(genre1Id, "label1"), (genre2Id, "label2")]
        val encodedValue = StringBuilder()
        encodedValue.append('[')
        value.forEachIndexed { index, genre ->
            encodedValue.append('(')
            encodedValue.append(genre.id)
            encodedValue.append(", ")
            encodedValue.append("\"${genre.label}\"")
            encodedValue.append(')')
            if (index != value.lastIndex) {
                encodedValue.append(", ")
            }
        }
        encodedValue.append(']')
        return encodedValue.toString()
    }
}
