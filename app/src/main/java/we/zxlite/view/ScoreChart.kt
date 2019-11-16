package we.zxlite.view

import android.content.Context
import android.graphics.Color
import android.graphics.Color.TRANSPARENT
import android.graphics.DashPathEffect
import android.graphics.Typeface.DEFAULT_BOLD
import android.util.AttributeSet
import android.view.MotionEvent
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.TOP
import com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.RIGHT
import com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL
import com.github.mikephil.charting.components.Legend.LegendForm.LINE
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineDataSet.Mode.CUBIC_BEZIER
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import we.zxlite.R
import we.zxlite.utils.BaseUtils.EMPTY_STR
import we.zxlite.utils.BaseUtils.color

class ScoreChart(ctx: Context, attr: AttributeSet) : LineChart(ctx, attr) {
    //图例条目
    var legendEntries = ArrayList<LegendEntry>()
    //数据集
    private var dataSets = ArrayList<ILineDataSet>()
    //分割线颜色
    private val diverColor get() = context!!.color(R.color.colorChartDiver)
    //线的颜色
    private val lineColor get() = context!!.color(R.color.colorAccent)

    init {
        dataSets.add(initDefaultLine())
        description.isEnabled = false
        isDoubleTapToZoomEnabled = false
        isDragEnabled = false
        legend.verticalAlignment = TOP
        legend.horizontalAlignment = RIGHT
        legend.orientation = HORIZONTAL
        data = LineData(dataSets)
        setTouchEnabled(false)
        setScaleEnabled(false)
        setAxis()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(false)
        return super.dispatchTouchEvent(ev)
    }

    fun addLine(name: String, entries: ArrayList<Entry>, length: Float) {
        val effect = DashPathEffect(floatArrayOf(length, length), 0f)
        legendEntries.add(LegendEntry(name, LINE, 10f, 2f, effect, lineColor))
        dataSets.add(LineDataSet(entries, null).apply {
            lineWidth = 2f
            circleRadius = 3f
            color = lineColor
            mode = CUBIC_BEZIER
            setDrawCircles(true)
            setDrawFilled(false)
            setDrawValues(false)
            setCircleColor(lineColor)
            enableDashedLine(length, length, 0f)
        })
    }

    private fun initDefaultLine(): LineDataSet {
        val pointValues = ArrayList<Entry>()
        for (i in 0..4) {
            pointValues.add(Entry((i * 2).toFloat(), (i * 2).toFloat()))
        }
        return LineDataSet(pointValues, null).apply {
            setDrawCircles(false)
            setDrawFilled(false)
            setDrawValues(false)
            color = TRANSPARENT
        }
    }

    private fun setAxis() {
        xAxis.isEnabled = false
        axisLeft.isEnabled = true
        axisRight.isEnabled = false
        axisLeft.axisMinimum = 0f
        axisLeft.axisMaximum = 9f
        axisLeft.xOffset = 10f
        axisLeft.yOffset = -8f
        axisLeft.typeface = DEFAULT_BOLD
        axisLeft.textColor = Color.GRAY
        axisLeft.gridColor = diverColor
        axisLeft.axisLineColor = diverColor
        axisLeft.axisLineWidth = 1.5f
        axisLeft.gridLineWidth = 1.5f
        axisLeft.setLabelCount(6, false)
        axisLeft.valueFormatter = LevelValueFormatter()
    }

    class LevelValueFormatter : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return when (value.toInt()) {
                0 -> "E"
                2 -> "D"
                4 -> "C"
                6 -> "B"
                8 -> "A"
                else -> EMPTY_STR
            }
        }
    }

}