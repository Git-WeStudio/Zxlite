package we.zxlite.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import we.zxlite.R
import we.zxlite.utils.BaseUtils.color

class ProgressBar(ctx: Context, attrs: AttributeSet) : View(ctx, attrs) {

    /* 进度值 */
    var value: Int = 0
        set(value) {
            field = value
            invalidate() //刷新
        }

    //画笔
    private val mPaint: Paint = Paint()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val progressWidth = value * measuredWidth / 100F //计算进度宽度

        mPaint.color = context!!.color(R.color.colorLine)
        canvas.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), mPaint) //画底层

        mPaint.color = context!!.color(R.color.colorAccent)
        canvas.drawRect(0f, 0f, progressWidth, measuredHeight.toFloat(), mPaint) //画进度
    }
}