package tech.aliorpse.jmutils.util

import io.ktor.http.HeadersBuilder

internal object JmConstants {
    // 搜索参数-排序
    const val ORDER_BY_LATEST = "mr"
    const val ORDER_BY_VIEW = "mv"
    const val ORDER_BY_PICTURE = "mp"
    const val ORDER_BY_LIKE = "tf"

    const val ORDER_MONTH_RANKING = "mv_m"
    const val ORDER_WEEK_RANKING = "mv_w"
    const val ORDER_DAY_RANKING = "mv_t"

    // 搜索参数-时间段
    const val TIME_TODAY = "t"
    const val TIME_WEEK = "w"
    const val TIME_MONTH = "m"
    const val TIME_ALL = "a"

    // 分类参数API接口的category
    const val CATEGORY_ALL = "0"  // 全部
    const val CATEGORY_DOUJIN = "doujin"  // 同人
    const val CATEGORY_SINGLE = "single"  // 单本
    const val CATEGORY_SHORT = "short"  // 短篇
    const val CATEGORY_ANOTHER = "another"  // 其他
    const val CATEGORY_HANMAN = "hanman"  // 韩漫
    const val CATEGORY_MEIMAN = "meiman"  // 美漫
    const val CATEGORY_DOUJIN_COSPLAY = "doujin_cosplay"  // cosplay
    const val CATEGORY_3D = "3D"  // 3D
    const val CATEGORY_ENGLISH_SITE = "english_site"  // 英文站

    // 副分类
    const val SUB_CHINESE = "chinese"  // 汉化，通用副分类
    const val SUB_JAPANESE = "japanese"  // 日语，通用副分类

    // 其他类（CATEGORY_ANOTHER）的副分类
    const val SUB_ANOTHER_OTHER = "other"  // 其他漫画
    const val SUB_ANOTHER_3D = "3d"  // 3D
    const val SUB_ANOTHER_COSPLAY = "cosplay"  // cosplay

    // 同人（SUB_CHINESE）的副分类
    const val SUB_DOUJIN_CG = "CG"  // CG
    const val SUB_DOUJIN_CHINESE = SUB_CHINESE
    const val SUB_DOUJIN_JAPANESE = SUB_JAPANESE

    // 短篇（CATEGORY_SHORT）的副分类
    const val SUB_SHORT_CHINESE = SUB_CHINESE
    const val SUB_SHORT_JAPANESE = SUB_JAPANESE

    // 单本（CATEGORY_SINGLE）的副分类
    const val SUB_SINGLE_CHINESE = SUB_CHINESE
    const val SUB_SINGLE_JAPANESE = SUB_JAPANESE
    const val SUB_SINGLE_YOUTH = "youth"

    // 图片分割参数
    const val SCRAMBLE_220980 = 220980
    const val SCRAMBLE_268850 = 268850
    const val SCRAMBLE_421926 = 421926

    // 移动端API密钥
    const val APP_TOKEN_SECRET = "18comicAPP"
    const val APP_TOKEN_SECRET_2 = "18comicAPPContent"
    const val APP_DATA_SECRET = "185Hcomic3PAPP7R"
    const val API_DOMAIN_SERVER_SECRET = "diosfjckwpqpdfjkvnqQjsik"
    const val APP_VERSION = "2.0.6"

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
