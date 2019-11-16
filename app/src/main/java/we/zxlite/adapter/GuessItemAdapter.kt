package we.zxlite.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_guess.view.*
import org.json.JSONObject
import we.zxlite.R
import we.zxlite.utils.BaseUtils.color

class GuessItemAdapter(
    private val myList: Map<Int, JSONObject>,
    private val guessList: Map<String, JSONObject>
) :
    RecyclerView.Adapter<GuessItemAdapter.ViewHolder>() {

    companion object {
        private const val SUBJECT_NAME = "subjectName"
        private const val SUBJECT_CODE = "subjectCode"
        private const val SCORE = "score"
        private const val DEF_CODE = "0"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_guess, parent, false))

    override fun getItemCount() = myList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val json = myList.getValue(position)
        var code = json.optString(SUBJECT_CODE)
        if (code.isEmpty()) code = DEF_CODE
        val myScore = json.optDouble(SCORE)
        val friendScore = guessList[code]?.optDouble(SCORE) ?: 0.0
        holder.itemView.itemSubjectName.text = json.optString(SUBJECT_NAME)
        holder.itemView.itemMyScore.text =
            myScore.toBigDecimal().stripTrailingZeros().toPlainString()
        holder.itemView.itemGuessScore.text =
            friendScore.toBigDecimal().stripTrailingZeros().toPlainString()
        val greyColor = holder.itemView.context.color(android.R.color.darker_gray)
        val defaultColor = holder.itemView.context.color(R.color.colorTitle)
        when {
            myScore > friendScore -> {
                holder.itemView.itemMyScore.setTextColor(defaultColor)
                holder.itemView.itemGuessScore.setTextColor(greyColor)
            }
            myScore < friendScore -> {
                holder.itemView.itemMyScore.setTextColor(greyColor)
                holder.itemView.itemGuessScore.setTextColor(defaultColor)
            }
            else -> {
                holder.itemView.itemMyScore.setTextColor(defaultColor)
                holder.itemView.itemGuessScore.setTextColor(defaultColor)
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}