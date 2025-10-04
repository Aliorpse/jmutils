package tech.aliorpse.jmutils.core

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.util.appendAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.openpdf.text.Document
import org.openpdf.text.Image
import org.openpdf.text.pdf.PdfWriter
import tech.aliorpse.jmutils.model.JmAlbum
import tech.aliorpse.jmutils.model.JmChapter
import tech.aliorpse.jmutils.util.DefaultJmPageProvider
import tech.aliorpse.jmutils.util.JmPageProvider
import tech.aliorpse.jmutils.util.JmConstants
import tech.aliorpse.jmutils.util.JmUtilsHttpClientProvider
import tech.aliorpse.jmutils.util.PageDeobfuscator
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ConcurrentHashMap

public object JmUtils {
    /**
     * 全局统一请求最大并发数, 虽然没有强制性限制, 但请不要调太高
     *
     * 默认 [JmPageProvider] 实现会把请求平均分配给三个 cdn 节点来缓解节点压力
     */
    @Volatile public var semaphore: Semaphore = Semaphore(30)

    /**
     * 获取一个 Album 的基本信息
     *
     * 返回 null 表示不存在
     *
     * @param albumId 车号
     */
    public suspend fun getAlbum(albumId: Int): JmAlbum? {
        val response = semaphore.withPermit {
            JmUtilsHttpClientProvider.httpClient.get("https://18comic.vip/album/$albumId") {
                headers.appendAll(JmConstants.htmlHeaders())
            }.bodyAsText()
        }

        val doc = Ksoup.parse(response)

        val trainDiv = doc.selectFirst("div.absolute.train-number")
            ?: return null

        val chapters: List<Int>? = if ("章節列表" !in response) {
            null
        } else {
            doc.select("a[href][data-album][data-index]")
                .mapNotNull { el -> el.attr("data-album").toIntOrNull() }
                .toCollection(LinkedHashSet())
                .toList()
        }

        return JmAlbum(
            albumId = trainDiv.selectFirst("span.number")?.text()!!.removePrefix("禁漫车：JM").toInt(),
            pageCount = trainDiv.selectFirst("span.pagecount")?.text()!!.removePrefix("页数:").toInt(),
            title = doc.selectFirst("h1#book-name")!!.text(),
            cover = "https://cdn-msp.18comic.vip/media/albums/$albumId.jpg",
            author = doc.selectFirst("div.p-t-5.p-b-5:has(h2.h2_info)")!!.ownText(),
            chapters = chapters
        )
    }

    private val arrRegex = Regex("""var\s+page_arr\s*=\s*\[(.*?)];""", RegexOption.DOT_MATCHES_ALL)

    /**
     * 获取一个 Chapter 的基本信息
     *
     * @param chapterId 车号
     */
    public suspend fun getChapter(chapterId: Int): JmChapter? {
        val response = semaphore.withPermit {
            JmUtilsHttpClientProvider.httpClient.get("https://18comic.vip/photo/$chapterId") {
                headers.appendAll(JmConstants.htmlHeaders())
            }.bodyAsText()
        }

        val pageArr = arrRegex.find(response)?.groupValues?.get(1)
            ?.split(",")
            ?.map { it.trim().trim('"') }
            ?: return null
        
        return JmChapter(
            chapterId = chapterId,
            pageCount = pageArr.size,
            pageArr = pageArr,
        )
    }

    /**
     * 下载 Album 并反混淆, 支持并发调用
     *
     * @param pageProvider 自定义页面下载器
     * @return JPEG 格式图片数组
     */
    public suspend fun JmChapter.download(
        maxRetry: Byte = 3,
        pageProvider: JmPageProvider = DefaultJmPageProvider
    ): List<ByteArray> {
        val pageResults = ConcurrentHashMap<Int, ByteArray>()
        val deobfChannel = Channel<Pair<ByteArray, Int>>(capacity = Channel.UNLIMITED)

        coroutineScope {
            val consumers = List(Runtime.getRuntime().availableProcessors()) {
                launch(Dispatchers.Default) {
                    deobfChannel.consumeEach { (data, page) ->
                        val decoded = PageDeobfuscator.unscrambleImage(data, chapterId, page)
                        pageResults[page] = decoded
                    }
                }
            }

            val jobs = pageArr.map { page ->
                launch {
                    val data = semaphore.withPermit {
                        pageProvider.getPage(chapterId, page, maxRetry)
                    }

                    deobfChannel.send(Pair(
                        data,
                        page.removeSuffix(".webp").toInt())
                    )
                }
            }

            jobs.joinAll()
            deobfChannel.close()
            consumers.joinAll()
        }

        return pageResults.toSortedMap().values.toList()
    }

    /**
     * 导出 Album 为 PDF
     *
     * @param outputPath 导出路径
     * @param password 可选加密
     */
    public fun List<ByteArray>.exportPdf(
        outputPath: String,
        password: String? = null
    ) {
        val file = File(outputPath)
        file.parentFile?.mkdirs()

        val document = Document()
        val fos = FileOutputStream(file)

        val writer = PdfWriter.getInstance(document, fos).apply {
            if (!password.isNullOrEmpty()) {
                setEncryption(
                    password.toByteArray(),
                    password.toByteArray(),
                    PdfWriter.ALLOW_PRINTING,
                    PdfWriter.ENCRYPTION_AES_128
                )
            }
        }

        document.open()

        for (bytes in this) {
            val img = Image.getInstance(bytes)
            document.setPageSize(img)
            document.newPage()
            img.setAbsolutePosition(0f, 0f)
            document.add(img)
        }

        document.close()
        writer.close()
        fos.close()
    }
}
