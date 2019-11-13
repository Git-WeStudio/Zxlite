package we.zxlite.activity

import android.os.Bundle
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
import we.zxlite.utils.HttpUtils.Error
import we.zxlite.utils.HttpUtils.Type.JsonObject
import we.zxlite.utils.HttpUtils.api

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
        //题标题
        private const val DIS_TITLE = "disTitle"
        //答案类型
        private const val ANSWER_TYPE = "answerType"
        //答案Html
        private const val ANSWER_HTML = "answerHtml"
        //主题id
        private const val TOPIC_SET_ID = " topicSetId"
        //主题分数
        private const val TOPIC_SCORE_DTOS = "topicScoreDTOs"
        //标准答案
        private const val STANDARD_ANSWER = " standardAnswer"
        //用户答案
        private const val USER_ANSWER = " userAnswer"
        //用户答案
        private const val USER_ANSWERS = " userAnswers"
        //图像答案
        private const val IMAGE_ANSWER = "imageAnswer"
        //html解析
        private const val ANALYSIS_HTML = " analysisHtml"
        //标准分数
        private const val STANDARD_SCORE = "standardScore"
        //分数
        private const val SCORE = "score"
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
        TabLayoutMediator(analyzeTab, analyzePager, TabConfigurationStrategy { tab, position ->
            tab.text = analyzePageList[position].dispTitle
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
                                    topic.optString(DIS_TITLE_NUMBER),
                                    topic.optString(DIS_TITLE),
                                    topic.optString(ANSWER_TYPE),
                                    topic.optString(ANSWER_HTML),
                                    topic.optString(TOPIC_SET_ID),
                                    topic.optString(TOPIC_SCORE_DTOS),
                                    topic.optString(STANDARD_ANSWER),
                                    topic.optString(USER_ANSWER),
                                    topic.optString(USER_ANSWERS),
                                    topic.optString(IMAGE_ANSWER),
                                    topic.optString(ANALYSIS_HTML),
                                    topic.optDouble(STANDARD_SCORE),
                                    topic.optDouble(SCORE),
                                    topic.optString(KNOWLEDGE_GROUPS)
                                )
                            )
                        }
                    }
                    withContext(Main) {
                        analyzePager.adapter!!.notifyItemRangeInserted(0, analyzePageList.size)
                    }
                } else toast((it as Error).message)
            }
        }
    }
}
