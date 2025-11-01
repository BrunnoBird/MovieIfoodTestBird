package com.example.movieifoodtest.data.network.tmdb

import com.example.movieifoodtest.BuildConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ImageUrlBuilderTest {

    @Test
    fun `returns full w500 url when path is present`() {
        val url = ImageUrlBuilder.buildPosterUrl("/abc.jpg", "w500")
        assertEquals(BuildConfig.TMDB_IMG_BASE + "w500/abc.jpg", url)
    }

    @Test
    fun `returns null when path is null or blank`() {
        assertNull(ImageUrlBuilder.buildPosterUrl(null))
        assertNull(ImageUrlBuilder.buildPosterUrl(""))
    }
}
