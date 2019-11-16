package we.zxlite.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import we.zxlite.R

/** 阴影控件
 * isTop 是否是顶部阴影
 */
class ShadowView(ctx: Context, attrs: AttributeSet) : View(ctx, attrs) {
    init {
        val styleAttrs = ctx.obtainStyledAttributes(attrs, R.styleable.ShadowView)

        if (styleAttrs.getBoolean(R.styleable.ShadowView_isTop, true)) {
            setBackgroundResource(R.drawable.shadow_top) //设置顶部阴影
        } else {
            setBackgroundResource(R.drawable.shadow_btm) //设置底部阴影
        }

        styleAttrs.recycle()
    }
}