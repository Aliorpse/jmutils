package tech.aliorpse.jmutils.util

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import kotlinx.coroutines.delay
import tech.aliorpse.jmutils.util.JmUtilsHttpClientProvider.httpClient
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger

/**
 * 自定义页面下载器 [getPage]
 */
public interface JmAlbumPageProvider {
    /**
     * 当请求失败, 直接抛错
     *
     * @param albumId 车号
     * @param page 当前页数
     * @param maxRetry 最大重试次数
     * @return 图片, 格式可以是 skiko 支持的任意图片格式, 比如说 JPEG, WEBP
     */
    public suspend fun getPage(albumId: Int, page: Int, maxRetry: Byte): ByteArray
}

public object DefaultJmAlbumPageProvider : JmAlbumPageProvider {

    private val cdnList = listOf("cdn-msp", "cdn-msp2", "cdn-msp3")
    private val index = AtomicInteger(0)

    private val cdn: String
        get() {
            val i = index.getAndUpdate { (it + 1) % cdnList.size }
            return cdnList[i]
        }

    public override suspend fun getPage(
        albumId: Int,
        page: Int,
        maxRetry: Byte
    ): ByteArray {
        var attempt = 0
        while (true) {
            try {
                val bytes = httpClient.get(
                    "https://$cdn.18comic.vip/media/photos/$albumId/${String.format(Locale.ROOT, "%05d", page)}.webp"
                ).bodyAsBytes()

                return bytes

            } catch (e: Exception) {
                attempt++
                if (attempt >= maxRetry) throw e
                delay(500)
            }
        }
    }
}
