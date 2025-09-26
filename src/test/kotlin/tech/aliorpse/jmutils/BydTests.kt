package tech.aliorpse.jmutils

import kotlinx.coroutines.runBlocking
import tech.aliorpse.jmutils.core.JmUtils.download
import tech.aliorpse.jmutils.core.JmUtils.exportPdf
import tech.aliorpse.jmutils.core.JmUtils.getAlbumInfo
import kotlin.system.measureTimeMillis
import kotlin.test.Test

class BydTests {
    @Test
    fun testGetAlbum() {
        val albumId = 1216733
        runBlocking {
            val result = getAlbumInfo(albumId)
            val time = measureTimeMillis {
                result.download().exportPdf(
                    "./albums/$albumId.pdf",
                    albumId.toString()
                )
            }
            println("Finished, used ${time}ms")
        }

    }
}
