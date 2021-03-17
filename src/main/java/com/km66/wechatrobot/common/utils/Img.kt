package com.km66.wechatrobot.common.utils

import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.*
import java.util.*
import javax.imageio.ImageIO
import javax.imageio.stream.FileImageOutputStream

/**
 * @author Sir丶雨轩
 * @date 2020/7/22
 */
object Img {
    @Throws(IOException::class)
    fun getByteByPic(imageUrl: String): ByteArray {
        val imageFile = File(imageUrl)
        val inStream: InputStream = FileInputStream(imageFile)
        val bis = BufferedInputStream(inStream)
        val bm = ImageIO.read(bis)
        val bos = ByteArrayOutputStream()
        val type = imageUrl.substring(imageUrl.length - 3)
        ImageIO.write(bm, type, bos)
        bos.flush()
        return bos.toByteArray()
    }

    /**
     * 将图片压缩到指定大小以内
     *
     * @param srcImgData 源图片数据
     * @param maxSize 目的图片大小
     * @return 压缩后的图片数据
     */
    @JvmStatic
    fun compressUnderSize(srcImgData: ByteArray, maxSize: Long): ByteArray {
        val scale = 0.9
        var imgData = Arrays.copyOf(srcImgData, srcImgData.size)
        if (imgData.size > maxSize) {
            do {
                imgData = try {
                    compress(imgData, scale)
                } catch (e: IOException) {
                    throw IllegalStateException("压缩图片过程中出错，请及时联系管理员！", e)
                }
            } while (imgData.size > maxSize)
        }
        return imgData
    }

    /**
     * 按照 宽高 比例压缩
     *
     * @param imgIs 待压缩图片输入流
     * @param scale 压缩刻度
     * @param out 输出
     * @return 压缩后图片数据
     * @throws IOException 压缩图片过程中出错
     */
    @Throws(IOException::class)
    fun compress(srcImgData: ByteArray?, scale: Double): ByteArray {
        val bi = ImageIO.read(ByteArrayInputStream(srcImgData))
        val width = (bi.width * scale).toInt() // 源图宽度
        val height = (bi.height * scale).toInt() // 源图高度
        val image = bi.getScaledInstance(width, height, Image.SCALE_SMOOTH)
        val tag = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val g = tag.graphics
        g.color = Color.RED
        g.drawImage(image, 0, 0, null) // 绘制处理后的图
        g.dispose()
        val bOut = ByteArrayOutputStream()
        ImageIO.write(tag, "JPEG", bOut)
        return bOut.toByteArray()
    }

    //byte数组到图片
    fun byte2image(data: ByteArray, path: String) {
        if (data.size < 3 || path == "") return
        try {
            val imageOutput = FileImageOutputStream(File(path))
            imageOutput.write(data, 0, data.size)
            imageOutput.close()
            println("Make Picture success,Please find image in $path")
        } catch (ex: Exception) {
            println("Exception: $ex")
            ex.printStackTrace()
        }
    }
}