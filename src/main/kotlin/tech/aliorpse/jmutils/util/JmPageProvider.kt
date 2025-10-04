package tech.aliorpse.jmutils.util

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import kotlinx.coroutines.delay
import tech.aliorpse.jmutils.util.JmUtilsHttpClientProvider.httpClient
import java.util.concurrent.atomic.AtomicInteger

/**
 * 自定义页面下载器 [getPage]
 */
public interface JmPageProvider {
    /**
     * 当请求失败, 直接抛错
     *
     * @param chapterId 车号
     * @param page 当前页面
     * @param maxRetry 最大重试次数
     * @return 图片, 格式可以是 skiko 支持的任意图片格式, 比如说 JPEG, WEBP
     */
    public suspend fun getPage(chapterId: Int, page: String, maxRetry: Byte): ByteArray
}

public object DefaultJmPageProvider : JmPageProvider {

    private val cdnList = listOf("cdn-msp", "cdn-msp2", "cdn-msp3")
    private val index = AtomicInteger(0)

    private val cdn: String
        get() {
            val i = index.getAndUpdate { (it + 1) % cdnList.size }
            return cdnList[i]
        }

    public override suspend fun getPage(
        chapterId: Int,
        page: String,
        maxRetry: Byte
    ): ByteArray {
        var attempt = 0
        while (true) {
            try {
                val response = httpClient.get(
                    "https://$cdn.18comic.vip/media/photos/$chapterId/$page"
                )

                if (!response.headers["Content-Type"].orEmpty().startsWith("image/")) {
                    error("Server returned not a picture")
                }

                return response.bodyAsBytes()
            } catch (e: Exception) {
                attempt++
                if (attempt >= maxRetry) throw e
                delay(500)
            }
        }
    }
}
