package we.zxlite.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_analyze.*
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.json.JSONObject
import we.zxlite.R
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
    }

    private val analyzeParams get() = "?paperId=${intent.getStringExtra(PAPER_ID)}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analyze)
        loadAnalyze()
    }

    override fun initView() {
        setSupportActionBar(analyzeBar)
        analyzeBar.setNavigationOnClickListener { onBackPressed() }

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

                        }
                    }
                } else toast((it as Error).message)
            }
        }
    }
}
