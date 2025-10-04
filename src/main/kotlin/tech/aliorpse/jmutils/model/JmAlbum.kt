package tech.aliorpse.jmutils.model

/**
 * 代表一个 Album 的基本信息
 *
 * @param albumId 车号
 * @param title 标题
 * @param pageCount 总页数
 * @param cover 封面图片链接
 * @param author 作者
 * @param chapters 章节, 如果不是多章节, 则为 null
 */
public data class JmAlbum(
    public val albumId: Int,
    public val title: String,
    public val pageCount: Int,
    public val cover: String,
    public val author: String,
    public val chapters: List<Int>?,
)

/**
 * 代表一个 Chapter 的基本信息
 *
 * @param chapterId 车号
 * @param pageCount 页数
 * @param pageArr 涵盖的页面, 由于有些页面不是连续数字, 所以这是必须的
 */
public data class JmChapter(
    public val chapterId: Int,
    public val pageCount: Int,
    public val pageArr: List<String>
)
