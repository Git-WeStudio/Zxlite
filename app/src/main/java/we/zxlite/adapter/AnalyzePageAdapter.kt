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

    companion object;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(from(parent.context).inflate(R.layout.fragment_analyze, parent, false))

    override fun getItemCount() = analyzeList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = ArrayList<AnalyzeListBean>()
        (holder.itemView as RecyclerView).adapter = AnalyzeListAdapter(list)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}