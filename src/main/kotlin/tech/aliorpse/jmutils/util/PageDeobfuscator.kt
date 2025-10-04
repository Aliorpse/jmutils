package tech.aliorpse.jmutils.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Rect
import java.security.MessageDigest
import java.util.Locale

public object PageDeobfuscator {
    /**
     * 返回一页被分割的份数
     *
     * @param aid 当前漫画 ID
     * @param page 页码
     */
    @Suppress("MagicNumber", "ReturnCount")
    public fun getScrambleNum(aid: Int, page: Int): Int {
        val pageStr = String.format(Locale.ROOT, "%05d", page)

        if (aid < JmConstants.SCRAMBLE_220980) return 0
        if (aid < JmConstants.SCRAMBLE_268850) return 10

        val md5 = MessageDigest.getInstance("MD5")
            .digest("$aid$pageStr".toByteArray())

        val hex = md5.joinToString("") { "%02x".format(it) }
        val code = hex[31].code
        val arraySize = if (aid < JmConstants.SCRAMBLE_421926) 10 else 8
        val choices = IntArray(arraySize) { (it + 1) * 2 }
        return choices[code % arraySize]
    }

    /**
     * 还原被切割过的图像
     *
     * @param input 输入图像字节, PNG/JPEG/WebP... (Skia 支持就行)
     * @param aid 漫画 ID
     * @param page 页码
     * @param quality JPEG 压缩质量 (0-100)
     * @return JPEG 字节
     */
    public suspend fun unscrambleImage(
        input: ByteArray,
        aid: Int,
        page: Int,
        quality: Int = 100
    ): ByteArray = withContext(Dispatchers.Default) {
        val splitCount = getScrambleNum(aid, page)

        val srcImage = Image.makeFromEncoded(input)

        try {
            // 如果没有混淆, 直接返回
            if (splitCount == 0) {
                val data = srcImage.encodeToData(EncodedImageFormat.JPEG, quality)
                    ?: error("encodeToData returned null")
                // Data 提供原生字节视图, 直接返回
                return@withContext data.bytes
            }

            val width = srcImage.width
            val height = srcImage.height

            val partHeight = height / splitCount
            val remainder = height % splitCount

            // 目标位图
            val dstBitmap = Bitmap().apply {
                val ok = allocN32Pixels(width, height, /*opaque=*/ false)
                if (!ok) error("allocN32Pixels failed for destination")
            }

            val canvas = Canvas(dstBitmap)

            val maxPartHeight = partHeight + remainder
            val tempBitmap = Bitmap().apply {
                val ok = allocN32Pixels(width, maxPartHeight, /*opaque=*/ false)
                if (!ok) error("allocN32Pixels failed for temp")
            }

            val paint = Paint()

            try {
                for (index in 0 until splitCount) {
                    val partHeightAdjusted = if (index == 0) partHeight + remainder else partHeight
                    val top = height - partHeight * (index + 1) - remainder
                    val destY = if (index == 0) 0 else partHeight * index + remainder

                    val ok = srcImage.readPixels(tempBitmap, /*srcX=*/ 0, /*srcY=*/ top)
                    if (!ok) error("readPixels failed for part $index (top=$top)")

                    val partImage = Image.makeFromBitmap(tempBitmap)
                    try {
                        val srcRect = Rect(0f, 0f, width.toFloat(), partHeightAdjusted.toFloat())
                        val dstRect = Rect(0f, destY.toFloat(), width.toFloat(), (destY + partHeightAdjusted).toFloat())

                        canvas.drawImageRect(partImage, srcRect, dstRect, Paint())
                    } finally {
                        partImage.close()
                    }
                }

                val outImage = Image.makeFromBitmap(dstBitmap)
                try {
                    val outData = outImage.encodeToData(EncodedImageFormat.JPEG, quality)
                        ?: error("encodeToData returned null for output")
                    outData.bytes
                } finally {
                    outImage.close()
                }
            } finally {
                tempBitmap.close()
                paint.close()
                dstBitmap.close()
            }
        } finally {
            srcImage.close()
        }
    }
}
