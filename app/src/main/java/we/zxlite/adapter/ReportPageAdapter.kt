package we.zxlite.adapter

import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_report.view.*
import we.zxlite.R
import we.zxlite.bean.ReportPageBean
import we.zxlite.utils.BaseUtils.EMPTY_STR
import we.zxlite.view.ScoreChart
import java.math.BigDecimal.ROUND_DOWN
import java.text.DecimalFormat

class ReportPageAdapter(
    private val pageList: ArrayList<ReportPageBean>,
    private val callback: (ScoreChart, String) -> Unit
) : RecyclerView.Adapter<ReportPageAdapter.ViewHolder>() {

    //等级评估
    private val Int.examLevel
        get() = when {
            this < 60 -> "待及格"
            this < 70 -> "及格"
            this < 85 -> "良好"
            this != 100 -> "优秀"
            else -> "满分！继续努力"
        }
    //学科建议
    private val Int.examAdvice
        get() = when {
            this < 60 -> "务实基础，再接再厉"
            this < 70 -> "保持心态，查漏补缺"
            this < 85 -> "进行针对性巩固练习"
            else -> "按时复习，稳住优势"
        }
    //学科诊断
    private val Int.examDiagnosis
        get() = when {
            this < 60 -> "该科成绩较差"
            this < 70 -> "该科成绩一般"
            this < 85 -> "该科成绩良好"
            this != 100 -> "该科成绩优秀"
            else -> "该科成绩优异"
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(from(parent.context).inflate(R.layout.fragment_report, parent, false))

    override fun getItemCount() = pageList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val standardScore = pageList[position].standardScore.toBigDecimal()
        val userScore = pageList[position].userScore.toBigDecimal()
        val examScale =
            userScore.multiply(100.toBigDecimal()).divide(standardScore, 0, ROUND_DOWN).toInt() //占比
        val examDeduct = standardScore.subtract(userScore).stripTrailingZeros().toPlainString() //扣分
        val showScore =
            "${userScore.stripTrailingZeros().toPlainString()} / ${standardScore.stripTrailingZeros().toPlainString()}"
        val showDetail =
            "•   我的分数： $showScore\n\n•   等级评估： ${examScale.examLevel}\n\n•   分数总扣： $examDeduct 分\n\n•   分数占比： $examScale %"
        val showAdvice =
            "•   学科诊断： ${examScale.examDiagnosis}\n\n•   学科建议： ${examScale.examAdvice}"
        holder.itemView.run {
            reportTitle.text = pageList[position].paperName
            reportChartTitle.text = "•   成绩变化曲线： "
            reportDetail.text = showDetail
            reportAdvice.text = showAdvice
            reportProgress.progress = examScale
            if (reportChart.tag != true) callback(reportChart, pageList[position].paperId)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}