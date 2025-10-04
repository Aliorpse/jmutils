package tech.aliorpse.jmutils

import kotlinx.coroutines.runBlocking
import tech.aliorpse.jmutils.core.JmUtils.download
import tech.aliorpse.jmutils.core.JmUtils.exportPdf
import tech.aliorpse.jmutils.core.JmUtils.getAlbum
import tech.aliorpse.jmutils.core.JmUtils.getChapter
import kotlin.test.Test

class BydTests {
    @Test
    fun testGetAlbum() {
        val albumId = 277707
        runBlocking {
            val album = getAlbum(albumId)
                ?: error("Result doesn't exist")
            val chapter = getChapter(album.chapters!!.random())
                ?: error("Result doesn't exist")

            chapter.download().exportPdf(
                "./albums/${chapter.chapterId}.pdf",
                chapter.chapterId.toString()
            )

            println(chapter.chapterId)
        }
    }
}
