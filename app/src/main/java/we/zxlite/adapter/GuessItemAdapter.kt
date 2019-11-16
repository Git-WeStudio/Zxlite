package we.zxlite.adapter

import android.graphics.Color
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_guess.view.*
import org.json.JSONObject
import we.zxlite.R

class GuessItemAdapter(
    private val mList: Map<Int, JSONObject>,
    private val fList: Map<String, JSONObject>
) :
    RecyclerView.Adapter<GuessItemAdapter.ViewHolder>() {

    companion object {
        private const val SUBJECT_NAME = "subjectName"
        private const val SUBJECT_CODE = "subjectCode"
        private const val SCORE = "score"
    }

    private var correctColor = Color.DKGRAY
    private var mistakeColor = Color.RED

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(from(parent.context).inflate(R.layout.item_guess, parent, false))

    override fun getItemCount() = mList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val value = mList.getValue(position)
        val code = value.optString(SUBJECT_CODE, "0")
        val mScore = value.optDouble(SCORE)
        val fScore = fList[code]?.optDouble(SCORE) ?: 0.0
        holder.itemSubject.text = value.optString(SUBJECT_NAME)
        holder.itemMyScore.text =
            mScore.toBigDecimal().stripTrailingZeros().toPlainString()
        holder.itemGuessScore.text =
            fScore.toBigDecimal().stripTrailingZeros().toPlainString()
        when {
            mScore > fScore -> {
                holder.itemMyScore.setTextColor(correctColor)
                holder.itemGuessScore.setTextColor(mistakeColor)
            }
            mScore < fScore -> {
                holder.itemMyScore.setTextColor(mistakeColor)
                holder.itemGuessScore.setTextColor(correctColor)
            }
            else -> {
                holder.itemMyScore.setTextColor(correctColor)
                holder.itemGuessScore.setTextColor(correctColor)
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemMyScore: TextView = itemView.itemMyScore
        val itemGuessScore: TextView = itemView.itemGuessScore
        val itemSubject: TextView = itemView.itemSubject
    }
}