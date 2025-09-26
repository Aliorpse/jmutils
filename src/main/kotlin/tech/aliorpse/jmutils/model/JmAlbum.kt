package tech.aliorpse.jmutils.model

/**
 * 代表一个 Album 的基本信息.
 *
 * @param albumId 车号
 * @param title 标题
 * @param pageCount 总页数
 * @param cover 封面图片链接
 * @param author 作者
 */
public data class JmAlbum(
    public val albumId: Int,
    public val title: String,
    public val pageCount: Int,
    public val cover: String,
    public val author: String,
)
