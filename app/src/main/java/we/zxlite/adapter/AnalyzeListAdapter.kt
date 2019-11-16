package we.zxlite.adapter

import android.text.Html.TagHandler
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_analyze.view.*
import we.zxlite.R
import we.zxlite.bean.AnalyzeListBean
import we.zxlite.utils.GetterUtils.ImageGetter

class AnalyzeListAdapter(private val analyzeList: ArrayList<AnalyzeListBean>) :
    RecyclerView.Adapter<AnalyzeListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(from(parent.context).inflate(R.layout.item_analyze, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemTitle.text = analyzeList[position].title
        holder.itemContent.text = HtmlCompat.fromHtml(analyzeList[position].content,
            FROM_HTML_MODE_LEGACY,
            ImageGetter(holder.itemContent),
            TagHandler { opening, tag, output, _ ->
                if (!opening && tag != null && tag.contentEquals("td")) output.append('\n')
            })
    }

    override fun getItemCount() = analyzeList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemTitle: TextView = itemView.itemTitle
        val itemContent: TextView = itemView.itemContent
    }
}