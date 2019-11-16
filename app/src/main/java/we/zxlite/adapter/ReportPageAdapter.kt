package we.zxlite.adapter

import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_report.view.*
import kotlinx.android.synthetic.main.fragment_report.view.reportAdvice
import kotlinx.android.synthetic.main.fragment_report.view.reportDetail
import kotlinx.android.synthetic.main.fragment_report.view.reportProgress
import kotlinx.android.synthetic.main.fragment_report.view.reportTitle
import kotlinx.android.synthetic.main.fragment_report_general.view.*
import we.zxlite.R
import we.zxlite.bean.ReportPageBean
import we.zxlite.view.ProgressBar
import we.zxlite.view.ScoreChart
import java.math.BigDecimal.ROUND_DOWN

class ReportPageAdapter(
    private val pageList: ArrayList<ReportPageBean>,
    private val callback: (ScoreChart, String) -> Unit
) : RecyclerView.Adapter<ReportPageAdapter.ViewHolder>() {

    companion object {
        private const val SUBJECT = 0 //学科
        private const val GENERAL = 1 //全科
    }

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
        if (viewType == SUBJECT)
            ViewHolder(
                from(parent.context).inflate(R.layout.fragment_report, parent, false)
            )
        else
            ViewHolder(
                from(parent.context).inflate(R.layout.fragment_report_general, parent, false)
            )

    override fun getItemCount() = pageList.size

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val standardScore = //标准得分
            pageList[i].standardScore.toBigDecimal()
        val userScore = //得分
            pageList[i].userScore.toBigDecimal()
        val examScale = //占比
            userScore.multiply(100.toBigDecimal()).divide(standardScore, 0, ROUND_DOWN).toInt()
        val examDeduct = //扣分
            standardScore.subtract(userScore).stripTrailingZeros().toPlainString()

        val showScore =
            "${userScore.stripTrailingZeros().toPlainString()} / ${standardScore.stripTrailingZeros().toPlainString()}"
        val showDetail =
            "•   我的分数： $showScore\n\n•   等级评估： ${examScale.examLevel}\n\n•   分数总扣： $examDeduct 分\n\n•   分数占比： $examScale %"
        val showAdvice =
            "•   学科诊断： ${examScale.examDiagnosis}\n\n•   学科建议： ${examScale.examAdvice}"
        val showChartTitle =
            "•   成绩变化曲线： "

        if (getItemViewType(i) == SUBJECT) {
            holder.itemView.reportChartTitle.text = showChartTitle
            holder.reportTitle.text = pageList[i].paperName
            holder.reportDetail.text = showDetail
            holder.reportAdvice.text = showAdvice
            holder.reportProgress.value = examScale

            if (holder.itemView.reportChart.tag != true) {
                callback(holder.itemView.reportChart, pageList[i].paperId)
            }
        } else {
            holder.reportTitle.text = pageList[i].paperName
            holder.reportDetail.text = showDetail
            holder.reportAdvice.text = showAdvice
            holder.reportProgress.value = examScale
            val subjectList = ArrayList<String>()
            for (item in pageList) {
                subjectList.add("•   ${item.title} ： ${item.userScore.toBigDecimal().stripTrailingZeros().toPlainString()} / ${item.standardScore.toBigDecimal().stripTrailingZeros().toPlainString()}")
            }

            holder.itemView.reportGeneral.layoutManager =
                GridLayoutManager(holder.itemView.context, 2)
            holder.itemView.reportGeneral.adapter =
                ReportItemAdapter(subjectList)
        }
    }

    override fun getItemViewType(i: Int) = if (pageList.size > 1 && i == 0) GENERAL else SUBJECT

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reportTitle: TextView = itemView.reportTitle
        val reportDetail: TextView = itemView.reportDetail
        val reportAdvice: TextView = itemView.reportAdvice
        val reportProgress: ProgressBar = itemView.reportProgress
    }
}