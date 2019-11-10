package we.zxlite.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_report.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import we.zxlite.R
import we.zxlite.bean.ReportPageBean
import we.zxlite.view.ScoreChart
import java.text.DecimalFormat

class ReportPageAdapter(
    private val pageList: ArrayList<ReportPageBean>,
    private val callback: (ScoreChart, String) -> Unit
) :
    RecyclerView.Adapter<ReportPageAdapter.ViewHolder>() {

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
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.fragment_report,
                parent,
                false
            )
        )

    override fun getItemCount() = pageList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val standardScore = pageList[position].standardScore
        val userScore = pageList[position].userScore
        val examDeduct = ((standardScore - userScore).toString() + " 分").replace(".0", "")
        val examScore = "$userScore / $standardScore".replace(".0", "")
        val examScale = DecimalFormat("0").format(userScore * 100 / standardScore)
        val examDetail =
            "•   我的分数： $examScore\n\n•   等级评估： ${examScale.toInt().examLevel}\n\n•   分数总扣： $examDeduct\n\n•   分数占比： $examScale %"
        val examAdvice =
            "•   学科诊断： ${examScale.toInt().examDiagnosis}\n\n•   学科建议： ${examScale.toInt().examAdvice}"
        holder.itemView.run {
            reportTitle.text = pageList[position].paperName
            reportChartTitle.text = "•   成绩变化曲线： "
            reportDetail.text = examDetail
            reportAdvice.text = examAdvice
            reportProgress.progress = (userScore / standardScore * 100).toInt()
            if (reportChart.tag != true) callback(reportChart, pageList[position].paperId)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}