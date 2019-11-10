package we.zxlite.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.core.content.ContextCompat
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import we.zxlite.utils.SqlUtils.Helper
import java.lang.Integer.toHexString
import kotlin.experimental.xor

object BaseUtils {
    /** 空字符串 */
    const val EMPTY_STR = ""

    /** 获取颜色值 */
    fun Context.color(colorResId: Int) = ContextCompat.getColor(this, colorResId)

    /** 转为HttpURLConnection */
    fun String.conn() = URL(this).openConnection() as HttpURLConnection

    /** 获取数据库资源 */
    val Context.db: Helper get() = Helper.getInstance(this)
    /** 转为RC4 */
    val String.rc4: String
        get() {
            val dataBytes = toByteArray()
            val keyBytes = "iflytek_pass_edp".toByteArray()
            val bytes = ByteArray(dataBytes.size)
            val s = ByteArray(256)
            var x = 0
            var y = 0
            for (i in s.indices) {
                s[i] = i.toByte()
            }
            for (i in s.indices) {
                y = y + s[i] + keyBytes[i % keyBytes.size] and 0xFF
                s[i] = s[i] xor s[y]
                s[y] = s[y] xor s[i]
                s[i] = s[i] xor s[y]
            }
            y = 0
            for (counter in dataBytes.indices) {
                x = x + 1 and 0xFF
                y = y + s[x] and 0xFF
                s[x] = s[x] xor s[y]
                s[y] = s[y] xor s[x]
                s[x] = s[x] xor s[y]
                val k = s[s[x] + s[y] and 0xFF]
                bytes[counter] = dataBytes[counter] xor k
            }
            return bytes.hexString
        }
    /** 转为MD5 */
    val String.md5: String
        get() {
            val ins = MessageDigest.getInstance("MD5")
            ins.update(toByteArray())
            return ins.digest().hexString
        }
    /** 转为位图 */
    val String.bitmap: Bitmap
        get() {
            val decode = Base64.decode(this, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decode, 0, decode.size)
        }
    /** 转为十六进制字符串 */
    private val ByteArray.hexString: String
        get() {
            val builder = StringBuilder()
            for (b in this) {
                val i: Int = b.toInt() and 255
                builder.append("${if (i < 16) "0" else EMPTY_STR}${toHexString(i)}")
            }
            return builder.toString()
        }
}