package com.mr3y.podcaster.core.local

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.mr3y.podcaster.core.model.Genre
import org.junit.Test

class TestAdapters {

    @Test
    fun `test encoding genres list`() {
        // Zero
        val encodedEmptyValue = GenresColumnAdapter.encode(emptyList())
        assertThat(encodedEmptyValue).isEqualTo("[]")

        // One
        val encodedOneValue = GenresColumnAdapter.encode(listOf(Genre(id = 42, label = "Leisure")))
        assertThat(encodedOneValue).isEqualTo("[(42, \"Leisure\")]")

        // Many
        val encodedValue = GenresColumnAdapter.encode(listOf(Genre(id = 102, label = "Technology"), Genre(id = 55, label = "News")))
        assertThat(encodedValue).isEqualTo("[(102, \"Technology\"), (55, \"News\")]")
    }

    @Test
    fun `test decoding genres list`() {
        // Zero
        val decodedEmptyValue = GenresColumnAdapter.decode("[]")
        assertThat(decodedEmptyValue).isEqualTo(emptyList())

        // One
        val decodedOneValue = GenresColumnAdapter.decode("[(42, \"Leisure\")]")
        assertThat(decodedOneValue).isEqualTo(listOf(Genre(id = 42, label = "Leisure")))

        // Many
        val decodedValue = GenresColumnAdapter.decode("[(102, \"Technology\"), (55, \"News\")]")
        assertThat(decodedValue).isEqualTo(listOf(Genre(id = 102, label = "Technology"), Genre(id = 55, label = "News")))
    }
}
