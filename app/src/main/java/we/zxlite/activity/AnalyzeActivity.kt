package we.zxlite.activity

import android.graphics.Color.RED
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import kotlinx.android.synthetic.main.activity_analyze.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.toast
import org.json.JSONObject
import we.zxlite.R
import we.zxlite.adapter.AnalyzePageAdapter
import we.zxlite.bean.AnalyzePageBean
import we.zxlite.utils.BaseUtils.color
import we.zxlite.utils.HttpUtils.Error
import we.zxlite.utils.HttpUtils.Type.JsonObject
import we.zxlite.utils.HttpUtils.api
import kotlin.Comparator
import kotlin.collections.ArrayList

class AnalyzeActivity : BaseActivity() {

    companion object {
        private const val ANALYZE_URL =
            "https://www.zhixue.com/zhixuebao/report/getPaperAnalysis" //解析URL
        //原卷id
        private const val PAPER_ID = "paperId"
        // 类型主题分析
        private const val TYPE_TOPIC = "typeTopicAnalysis"
        //主题分析
        private const val TYPE_DTOS = "topicAnalysisDTOs"
        //题号
        private const val DIS_TITLE_NUMBER = "disTitleNumber"
        //答案类型
        private const val ANSWER_TYPE = "answerType"
        //答案Html
        private const val ANALYSIS_HTML = "analysisHtml"
        //答案Html
        private const val ANSWER_HTML = "answerHtml"
        //题头
        private const val TOPIC_NUMBER = "topicNumber"
        //主题id
        private const val TOPIC_ID = "topicId"
        //主题id
        private const val TOPIC_SET_ID = "topicSetId"
        //主题分数
        private const val TOPIC_SCORE_DTOS = "topicScoreDTOs"
        //标准答案
        private const val STANDARD_ANSWER = "standardAnswer"
        //用户答案
        private const val USER_ANSWER = "userAnswer"
        //用户答案
        private const val USER_ANSWERS = "userAnswers"
        //图像答案
        private const val IMAGE_ANSWER = "imageAnswer"
        //标准分数
        private const val STANDARD_SCORE = "standardScore"
        //分数
        private const val SCORE = "score"
        //题目
        private const val CONTENT_HTML = "contentHtml"
        //知识点
        private const val KNOWLEDGE_GROUPS = "relatedKnowledgeGroups"
    }

    //解析页列表
    private val analyzePageList = ArrayList<AnalyzePageBean>()
    //解析参数
    private val analyzeParams get() = "?paperId=${intent.getStringExtra(PAPER_ID)}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analyze)
        loadAnalyze()
    }

    override fun initView() {
        setSupportActionBar(analyzeBar)
        analyzeBar.setNavigationOnClickListener { onBackPressed() }
        analyzePager.adapter = AnalyzePageAdapter(analyzePageList)
        analyzePager.offscreenPageLimit = 3
        TabLayoutMediator(analyzeTab, analyzePager, TabConfigurationStrategy { tab, position ->
            val tabView = View.inflate(this, R.layout.item_tab, null) as TextView
            tabView.text = analyzePageList[position].disTitleNumber
            when {
                analyzePageList[position].score == analyzePageList[position].standardScore ->
                    tab.tag = 0
                analyzePageList[position].score == 0.0 -> {
                    tab.tag = 1
                    tabView.setTextColor(RED)
                }
                else -> {
                    tab.tag = 2
                    tabView.setTextColor(color(R.color.colorDeepYellow))
                }
            }
            tab.customView = tabView
        }).attach()
    }

    private fun loadAnalyze() {
        launch {
            api(ANALYZE_URL + analyzeParams, null, true, JsonObject).let {
                if (it is JSONObject) {
                    val topicAnalysis = it.optJSONArray(TYPE_TOPIC)
                    for (i in 0 until topicAnalysis!!.length()) {
                        val topicDTOs =
                            topicAnalysis.optJSONObject(i).optJSONArray(TYPE_DTOS)
                        for (dto in 0 until topicDTOs!!.length()) {
                            val topic = topicDTOs.optJSONObject(dto)
                            analyzePageList.add(
                                AnalyzePageBean(
                                    disTitleNumber = topic.optString(DIS_TITLE_NUMBER),
                                    topicNumber = topic.optInt(TOPIC_NUMBER),
                                    topicScoreDTOs = topic.optString(TOPIC_SCORE_DTOS),
                                    topicId = topic.optString(TOPIC_ID),
                                    topicSetId = topic.optString(TOPIC_SET_ID),
                                    analysisHtml = topic.optString(ANALYSIS_HTML),
                                    contentHtml = topic.optString(CONTENT_HTML),
                                    answerType = topic.optString(ANSWER_TYPE),
                                    answerHtml = topic.optString(ANSWER_HTML),
                                    standardAnswer = topic.optString(STANDARD_ANSWER),
                                    standardScore = topic.optDouble(STANDARD_SCORE),
                                    score = topic.optDouble(SCORE),
                                    userAnswer = topic.optString(USER_ANSWER),
                                    userAnswers = topic.optString(USER_ANSWERS),
                                    imageAnswers = topic.optString(IMAGE_ANSWER),
                                    relatedKnowledgeGroups = topic.optString(KNOWLEDGE_GROUPS)
                                )
                            )
                        }
                    }
                    analyzePageList.sortWith(Comparator { o1, o2 ->
                        if (o1.topicNumber > o2.topicNumber) 1 else -1
                    })
                    withContext(Main) {
                        analyzePager.adapter!!.notifyItemRangeInserted(0, analyzePageList.size)
                    }
                } else toast((it as Error).message)
            }
        }
    }
}
