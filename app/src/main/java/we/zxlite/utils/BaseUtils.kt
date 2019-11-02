package we.zxlite.utils

import android.content.Context
import androidx.core.content.ContextCompat

object BaseUtils {
    /** 获取颜色值
     * @param colorResId 颜色资源标识
     * @return 颜色值
     */
    fun Context.color(colorResId: Int) = ContextCompat.getColor(this, colorResId)
}