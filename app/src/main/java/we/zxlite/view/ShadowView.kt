package we.zxlite.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM
import android.graphics.drawable.GradientDrawable.Orientation.BOTTOM_TOP
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.JELLY_BEAN
import android.util.AttributeSet
import android.view.View
import we.zxlite.R
import we.zxlite.utils.BaseUtils.color

/** 阴影控件 */
class ShadowView(ctx: Context, attrs: AttributeSet) : View(ctx, attrs) {
    init {
        if (SDK_INT >= JELLY_BEAN) {
            val typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.ShadowView)
            val isTop = typedArray.getBoolean(R.styleable.ShadowView_isTop, true)
            this.background = GradientDrawable().apply {
                colors = intArrayOf(
                    ctx.color(R.color.colorShadow),
                    ctx.color(android.R.color.transparent)
                )
                orientation = if (isTop) TOP_BOTTOM else BOTTOM_TOP
            }
            typedArray.recycle()
        }
    }
}