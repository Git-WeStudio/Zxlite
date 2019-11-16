package we.zxlite.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_general.view.*
import we.zxlite.R

class ReportItemAdapter(private var reportList: ArrayList<String>) :
    RecyclerView.Adapter<ReportItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_general, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.itemTitle.text = reportList[position]
    }

    override fun getItemCount() = reportList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}