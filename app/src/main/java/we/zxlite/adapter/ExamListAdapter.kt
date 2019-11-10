package we.zxlite.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_report.view.*
import we.zxlite.R
import we.zxlite.bean.ReportListBean
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale.getDefault
import kotlin.collections.ArrayList

/** 考试列表适配器
 * @param reportList 数据集合
 * @param callback 回调数据
 */
class ExamListAdapter(
    private var reportList: ArrayList<ReportListBean>,
    private val callback: (String) -> Unit
) :
    RecyclerView.Adapter<ExamListAdapter.ViewHolder>(),
    View.OnClickListener {

    companion object {
        private const val minute = 60 * 1000L//分
        private const val hour = 60 * minute//时
        private const val day = 24 * hour//日
        private const val month = 30 * day// 月
        private const val year = 12 * month//年
    }

    /** 获取Num */
    private val Int.num: String
        get() {
            return when {
                this < 10 -> "0$this"
                this in 10..99 -> this.toString()
                else -> "9+"
            }
        }

    private val Long.timeFormat: String
        get() {
            val date = Date().time - Date(this).time
            return when {
                date > year -> "${date / year} 年前"
                date > month -> "${date / month} 个月前"
                date > day -> "${date / day} 天前"
                date > hour -> "${date / hour} 小时前"
                date > minute -> "${date / minute} 分钟前"
                else -> "刚刚"
            } + "  |  " + SimpleDateFormat("yyyy-MM-dd", getDefault()).format(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reportItem = reportList[position]
        holder.itemView.setOnClickListener(this)
        holder.itemView.tag = reportItem.examId
        holder.itemView.itemNum.text = (position + 1).num
        holder.itemView.itemTitle.text = reportItem.examName
        holder.itemView.itemSubTitle.text = reportItem.examCreateTime.timeFormat
    }

    override fun getItemCount() = reportList.size

    override fun onClick(v: View) {
        callback(v.tag.toString())
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ItemDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.top = if (parent.getChildAdapterPosition(view) == 0) 16 else 8
            outRect.bottom = 8
        }
    }
}