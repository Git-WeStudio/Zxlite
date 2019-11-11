package we.zxlite.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import we.zxlite.R
import we.zxlite.utils.BaseUtils.color

class ProgressBar(ctx: Context, attrs: AttributeSet) : View(ctx, attrs) {
    //进度
    var progress: Int = 0
        set(value) {
            field = value
            invalidate()
        }
    //画笔
    private val mPaint: Paint = Paint()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val progressWidth = progress * measuredWidth / 100F
        mPaint.color = context!!.color(R.color.colorLine)
        canvas.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), mPaint)
        mPaint.color = context!!.color(R.color.colorAccent)
        canvas.drawRect(0f, 0f, progressWidth, measuredHeight.toFloat(), mPaint)
    }
}