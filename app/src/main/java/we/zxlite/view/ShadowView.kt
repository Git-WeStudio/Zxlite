package we.zxlite.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import we.zxlite.R

/** 阴影控件 */
class ShadowView(ctx: Context, attrs: AttributeSet) : View(ctx, attrs) {

    init {
        val typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.ShadowView)
        setShadowView(typedArray.getBoolean(R.styleable.ShadowView_isTop, true))
        typedArray.recycle()
    }

    /** 设置阴影 */
    private fun setShadowView(isTop: Boolean) {
        if (isTop)
            setBackgroundResource(R.drawable.shadow_top)
        else
            setBackgroundResource(R.drawable.shadow_top)
    }
}