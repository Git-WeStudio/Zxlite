package we.zxlite.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.github.mikephil.charting.data.Entry
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONObject
import we.zxlite.R
import we.zxlite.adapter.ReportPageAdapter
import we.zxlite.bean.ReportPageBean
import we.zxlite.utils.HttpUtils.Error
import we.zxlite.utils.HttpUtils.api
import we.zxlite.utils.HttpUtils.Type.JsonObject

class ReportActivity : BaseActivity() {

    companion object {
        //报告
        private const val REPORT_URL = "https://www.zhixue.com/zhixuebao/report/exam/getReportMain"
        //表
        private const val CHART_URL = "https://www.zhixue.com/zhixuebao/report/paper/getLevelTrend"
        //报告列表
        private const val PAPER_LIST = "paperList"
        //列表
        private const val LIST = "list"
        //报告id
        private const val EXAM_ID = "examId"
        //标题
        private const val TITLE = "title"
        //考卷id
        private const val PAPER_ID = "paperId"
        //考卷名
        private const val PAPER_NAME = "paperName"
        //总分
        private const val STANDARD_SCORE = "standardScore"
        //用户得分
        private const val USER_SCORE = "userScore"
        //学科码
        private const val SUBJECT_CODE = "subjectCode"
        // 学科名
        private const val SUBJECT_NAME = "subjectName"
        //数据列表
        private const val DATA_LIST = "dataList"
        //等级
        private const val LEVEL = "level"
        //标识
        private const val TAG = "tag"
        //名称
        private const val NAME = "name"
    }

    //报告页列表
    private val reportPageList = ArrayList<ReportPageBean>()
    //报告id
    private val examId get() = intent.getStringExtra(EXAM_ID)
    //报告参数
    private val reportParams get() = "examId=$examId"
    //水平趋势
    private val String.levelTrend
        get() = when (this) {
            "A等" -> 8F
            "A1" -> 8F
            "A2" -> 7.6F
            "A3" -> 7.2F
            "A4" -> 6.8F
            "A5" -> 6.4F
            "B等" -> 6F
            "B1" -> 6F
            "B2" -> 5.6F
            "B3" -> 5.2F
            "B4" -> 4.8F
            "B5" -> 4.4F
            "C等" -> 4F
            "C1" -> 4F
            "C2" -> 3.6F
            "C3" -> 3.2F
            "C4" -> 2.8F
            "C5" -> 2.4F
            "D等" -> 2F
            "D1" -> 2F
            "D2" -> 1.6F
            "D3" -> 1.2F
            "D4" -> 0.8F
            "D5" -> 0.4F
            else -> 0F
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        loadReport()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_report, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val selected = reportPageList[reportPager.currentItem].paperId
        when (item.itemId) {
            R.id.menuAnalyze -> startActivity<AnalyzeActivity>(PAPER_ID to selected)
            R.id.menuPaper -> startActivity<PaperActivity>(PAPER_ID to selected)
            R.id.menuGuess -> startActivity<GuessActivity>(PAPER_ID to selected)
            else -> Unit
        }
        return true
    }

    override fun initView() {
        setSupportActionBar(reportBar)
        reportBar.setNavigationOnClickListener { onBackPressed() }
        reportPager.adapter = ReportPageAdapter(reportPageList) { chart, id ->
            launch {
                val chartParams = "paperId=$id&pageIndex=1&pageSize=5&examId=$examId"
                api(CHART_URL, chartParams, true, JsonObject).let {
                    if (it is JSONObject) {
                        val chartList = it.optJSONArray(LIST)
                        for (i in 0 until chartList!!.length()) {
                            val entries = ArrayList<Entry>()
                            val item = chartList.optJSONObject(i)
                            val itemName = item.optJSONObject(TAG)!!.optString(NAME)
                            val dataList = item.optJSONArray(DATA_LIST)
                            for (data in 0 until dataList!!.length()) {
                                val levelTrend =
                                    dataList.optJSONObject(data).optString(LEVEL).levelTrend
                                entries.add(Entry(data * 2f, levelTrend))
                            }
                            chart.addLine(itemName, entries, i * 5f)
                        }
                        withContext(Main) {
                            chart.tag = true
                            chart.legend.setCustom(chart.legendEntries)
                            chart.notifyDataSetChanged()
                            chart.invalidate()
                        }
                    }
                }
            }
        }
        TabLayoutMediator(reportTab, reportPager, TabConfigurationStrategy { tab, position ->
            tab.text = reportPageList[position].title
        }).attach()
    }

    /** 加载报告 */
    private fun loadReport() {
        launch {
            api(REPORT_URL, reportParams, true, JsonObject).let {
                if (it is JSONObject) {
                    val reportList = it.optJSONArray(PAPER_LIST)
                    for (i in 0 until reportList!!.length()) {
                        val item = reportList.optJSONObject(i)
                        reportPageList.add(
                            ReportPageBean(
                                title = item.optString(TITLE),
                                paperId = item.optString(PAPER_ID),
                                paperName = item.optString(PAPER_NAME),
                                standardScore = item.optDouble(STANDARD_SCORE),
                                userScore = item.optDouble(USER_SCORE),
                                subjectCode = item.optInt(SUBJECT_CODE),
                                subjectName = item.optString(SUBJECT_NAME)
                            )
                        )
                    }
                    withContext(Main) {
                        reportPager.adapter!!.notifyItemRangeInserted(0, reportPageList.size)
                    }
                } else withContext(Main) {
                    toast((it as Error).message)
                }
            }
        }
    }
}
