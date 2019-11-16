package we.zxlite.adapter

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater.from
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import we.zxlite.R
import we.zxlite.bean.AnalyzeListBean
import we.zxlite.bean.AnalyzePageBean

class AnalyzePageAdapter(private val analyzeList: ArrayList<AnalyzePageBean>) :
    RecyclerView.Adapter<AnalyzePageAdapter.ViewHolder>() {

    companion object {
        private const val TITLE_TOPIC = "题目"
        private const val TITLE_MY_SCORE = "我的得分"
        private const val TITLE_MY_ANSWER = "我的答案"
        private const val TITLE_ANALYSIS = "题目解析"
        private const val TITLE_CORRECT = "正确答案"
        private const val TITLE_KNOW_LEDGES = "知识点"

        private const val SCORE = "score"
        private const val NAME = "name"
        private const val TOPIC_NUM = "topicNum"
        private const val USER_ANSWER = "userAnswer"
        private const val STAND_SCORE = "standScore"
        private const val DISP_TITLE = "dispTitle"
        private const val KNOW_LEDGES = "relatedKnowledges"
    }

    private val Int.topic get() = AnalyzeListBean(TITLE_TOPIC, analyzeList[this].contentHtml)

    private val String.analysis get() = AnalyzeListBean(TITLE_ANALYSIS, this)

    private val String.imgAnswer: AnalyzeListBean
        get() {
            val array = JSONArray(this)
            val answers = StringBuilder()
            for (i in 0 until array.length()) {
                answers.append("<img src=\"${array[i]}\"/>")
            }
            return AnalyzeListBean(TITLE_MY_ANSWER, answers.toString())
        }

    private val String.answer: AnalyzeListBean?
        get() {
            val mAnswers = StringBuilder()
            val answers = JSONArray(this)
            for (i in 0 until answers.length()) {
                val item = answers.getJSONObject(i)
                mAnswers.append("${item.optInt(TOPIC_NUM)}.${item.optString(USER_ANSWER)}；")
            }
            return if (mAnswers.isNotEmpty())
                AnalyzeListBean(TITLE_MY_ANSWER, mAnswers.toString())
            else null
        }

    private val String.standard: AnalyzeListBean
        get() {
            var answer = this
            if (this.startsWith("https://")) answer = "<img src=\"$answer\"/>"
            return AnalyzeListBean(TITLE_CORRECT, answer)
        }

    private val String.knowledge: AnalyzeListBean
        get() {
            val buffer = StringBuilder()
            val groups = JSONArray(this)
            for (i in 0 until groups.length()) {
                val knowledge = groups.optJSONObject(i)
                val dispTitle = knowledge.optString(DISP_TITLE)
                val knowledges = knowledge.optJSONArray(KNOW_LEDGES)
                if (dispTitle.isNotEmpty() && groups.length() != 1)
                    buffer.append("第&nbsp;$dispTitle&nbsp;题：&nbsp;")
                for (it in 0 until knowledges!!.length()) {
                    buffer.append(knowledges.optJSONObject(it).optString(NAME))
                    if (it != knowledges.length() - 1) buffer.append("；")
                }
                buffer.append("<br/>")
            }
            return AnalyzeListBean(TITLE_KNOW_LEDGES, buffer.toString())
        }

    private val String.score: AnalyzeListBean?
        get() {
            return if (this.isNotEmpty()) {
                val array = JSONArray(this)
                val scores = StringBuilder()
                for (i in 0 until array.length()) {
                    val item = array.optJSONObject(i)
                    scores.append(
                        "第&nbsp;${item.optString(DISP_TITLE)}&nbsp;题：&nbsp;&nbsp;" +
                                "${item.optString(SCORE)}&nbsp;分&nbsp;/&nbsp;" +
                                "${item.optString(STAND_SCORE)}&nbsp;分"
                    )
                    if (array.length() != i + 1) scores.append("<br/>")
                }
                AnalyzeListBean(TITLE_MY_SCORE, scores.toString())
            } else null
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder((from(parent.context)
            .inflate(R.layout.fragment_analyze, parent, false) as RecyclerView).apply {
            addItemDecoration(ItemDecoration())
            setHasFixedSize(true)
        })

    override fun getItemCount() = analyzeList.size

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        GlobalScope.launch {
            val list = ArrayList<AnalyzeListBean>()
            analyzeList[i].run {
                if (contentHtml.isNotEmpty()) list.add(i.topic)
                if (topicScoreDTOs.score == null) {
                    list.add(
                        AnalyzeListBean(
                            TITLE_MY_SCORE,
                            "第&nbsp;$disTitleNumber&nbsp;题：&nbsp;&nbsp;$score&nbsp;分&nbsp;/&nbsp;$standardScore&nbsp;分"
                        )
                    )
                } else list.add(topicScoreDTOs.score!!)
                if (answerType == "s01Text")
                    list.add(AnalyzeListBean(TITLE_MY_ANSWER, userAnswer))
                else if (userAnswers.isNotEmpty() && userAnswers.answer != null)
                    list.add(userAnswers.answer!!)
                if (imageAnswers.isNotEmpty()) list.add(imageAnswers.imgAnswer)
                if (standardAnswer.isNotEmpty() && answerHtml == "略")
                    list.add(standardAnswer.standard)
                else
                    list.add(AnalyzeListBean(TITLE_CORRECT, answerHtml))
                if (analysisHtml.isNotEmpty()) list.add(analysisHtml.analysis)
                if (relatedKnowledgeGroups.isNotEmpty()) list.add(relatedKnowledgeGroups.knowledge)
            }
            withContext(Main) {
                (holder.itemView as RecyclerView).adapter = AnalyzeListAdapter(list)
            }
        }
    }

    class ViewHolder(itemView: RecyclerView) : RecyclerView.ViewHolder(itemView)

    class ItemDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.top =
                if (parent.getChildAdapterPosition(view) == 0) 56 else 24
            outRect.bottom = 24
            outRect.left = 48
            outRect.right = 48
        }
    }
}