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
    //值
    private val mValue: Float get()= measuredWidth / 100F

    init {
        val typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.ProgressBar)
        progress = typedArray.getInt(R.styleable.ProgressBar_progress, 0)
        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint.color = context!!.color(R.color.colorLine)
        canvas.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), mPaint)
        mPaint.color = context!!.color(R.color.colorAccent)
        canvas.drawRect(0f, 0f, ((progress * mValue)), measuredHeight.toFloat(), mPaint)
    }
}