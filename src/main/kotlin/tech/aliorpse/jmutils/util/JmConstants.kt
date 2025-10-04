package tech.aliorpse.jmutils.util

import io.ktor.http.HeadersBuilder

internal object JmConstants {
    // 图片分割参数
    const val SCRAMBLE_220980 = 220980
    const val SCRAMBLE_268850 = 268850
    const val SCRAMBLE_421926 = 421926

    @Suppress("MaxLineLength")
    fun htmlHeaders(): HeadersBuilder = HeadersBuilder().apply {
        append("accept", "text/html,application/xhtml+xml,...")
        append("accept-language", "zh-CN,zh;q=0.9")
        append("cache-control", "no-cache")
        append("dnt", "1")
        append("pragma", "no-cache")
        append("priority", "u=0, i")
        append("referer", "https://18comic.vip/")
        append("sec-ch-ua", "\"Chromium\";v=\"124\", \"Google Chrome\";v=\"124\", \"Not-A.Brand\";v=\"99\"")
        append("sec-ch-ua-mobile", "?0")
        append("sec-ch-ua-platform", "\"Windows\"")
        append("sec-fetch-dest", "document")
        append("sec-fetch-mode", "navigate")
        append("sec-fetch-site", "none")
        append("sec-fetch-user", "?1")
        append("upgrade-insecure-requests", "1")
        append("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 ... Chrome/124 Safari/537.36")
    }
}
