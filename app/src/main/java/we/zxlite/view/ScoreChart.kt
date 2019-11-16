package we.zxlite.view

import android.content.Context
import android.graphics.Color
import android.graphics.Color.TRANSPARENT
import android.graphics.DashPathEffect
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import we.zxlite.R
import we.zxlite.utils.BaseUtils.EMPTY_STR
import we.zxlite.utils.BaseUtils.color

class ScoreChart(ctx: Context, attr: AttributeSet) : LineChart(ctx, attr) {

    var legendEntries = ArrayList<LegendEntry>()

    private var dataSets = ArrayList<ILineDataSet>()

    private val diverColor get() = context!!.color(R.color.colorChartDiver)

    private val lineColor: Int get() = context!!.color(R.color.colorAccent)

    init {
        dataSets.add(initDefaultLine())
        description.isEnabled = false
        isDoubleTapToZoomEnabled = false
        isDragEnabled = false
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
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
        legendEntries.add(
            LegendEntry(
                name,
                Legend.LegendForm.LINE,
                10f,
                2f,
                DashPathEffect(floatArrayOf(length, length), 0f),
                lineColor
            )
        )
        dataSets.add(LineDataSet(entries, null).apply {
            lineWidth = 2f
            circleRadius = 3f
            color = lineColor
            mode = LineDataSet.Mode.CUBIC_BEZIER
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
        axisLeft.typeface = Typeface.DEFAULT_BOLD
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