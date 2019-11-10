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

    private var isTop = true

    init {
        val typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.ShadowView)
        isTop = typedArray.getBoolean(R.styleable.ShadowView_isTop, true)
        typedArray.recycle()
        setShadow()
    }

    /** 设置阴影 */
    private fun setShadow() {
        if (SDK_INT >= JELLY_BEAN) background = GradientDrawable().apply {
            colors = intArrayOf(
                context.color(R.color.colorShadow),
                context.color(android.R.color.transparent)
            )
            orientation = if (isTop) TOP_BOTTOM else BOTTOM_TOP
        }
    }
}