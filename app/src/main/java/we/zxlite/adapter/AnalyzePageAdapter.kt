package we.zxlite.adapter

import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater.from
import androidx.recyclerview.widget.RecyclerView
import we.zxlite.R
import we.zxlite.bean.AnalyzeListBean
import we.zxlite.bean.AnalyzePageBean

class AnalyzePageAdapter(private val analyzeList: ArrayList<AnalyzePageBean>) :
    RecyclerView.Adapter<AnalyzePageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(from(parent.context).inflate(R.layout.fragment_analyze, parent, false))

    override fun getItemCount() = analyzeList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder.itemView as RecyclerView).run {
            val list = ArrayList<AnalyzeListBean>()
            list.add(AnalyzeListBean("题目解析", analyzeList[position].contentHtml))
            adapter = AnalyzeListAdapter(list)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}