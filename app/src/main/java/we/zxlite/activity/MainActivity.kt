package we.zxlite.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.internal.NavigationMenuView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header_main.view.*
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.replace
import org.jetbrains.anko.db.select
import org.jetbrains.anko.startActivity
import we.zxlite.R
import we.zxlite.dialog.BindDialog
import we.zxlite.dialog.ModifyDialog
import we.zxlite.utils.BaseUtils.EMPTY_STR
import we.zxlite.utils.BaseUtils.db
import we.zxlite.utils.SqlUtils.Helper.Companion.ITEM_NAME
import we.zxlite.utils.SqlUtils.Helper.Companion.ITEM_VALUE
import we.zxlite.utils.SqlUtils.Helper.Companion.REPORT_TYPE
import we.zxlite.utils.SqlUtils.Helper.Companion.SELECT_USER
import we.zxlite.utils.SqlUtils.Helper.Companion.TABLE_CFG
import we.zxlite.utils.UserUtils.cfg
import android.content.Intent
import android.net.Uri
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.email
import org.json.JSONObject
import we.zxlite.adapter.ExamListAdapter
import we.zxlite.adapter.ExamListAdapter.ItemDecoration
import we.zxlite.bean.ReportListBean
import we.zxlite.dialog.AboutDialog
import we.zxlite.dialog.AccountDialog
import we.zxlite.utils.HttpUtils.Error
import we.zxlite.utils.HttpUtils.Type.JsonObject
import we.zxlite.utils.HttpUtils.api

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,
    SwipeRefreshLayout.OnRefreshListener {

    companion object {
        //考试报告
        private const val EXAM_TYPE = "EXAM_TYPE"
        //作业报告
        private const val HOMEWORK_TYPE = "HOMEWORK_TYPE"
        //考试列表
        private const val EXAM_INFO_LIST = "examInfoList"
        //考试id
        private const val EXAM_ID = "examId"
        //考试名称
        private const val EXAM_NAME = "examName"
        //考试时间
        private const val CREATE_TIME = "examCreateDateTime"
        //考试
        private const val EXAM = "exam"
        //作业
        private const val HOMEWORK = "homework"
        //页面列表
        private const val LIST_URL = "https://www.zhixue.com/zhixuebao/report/getPageExamList"

        private const val QQ_GROUP =
            "mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D668tQMrX050v9ChooYRBaRA137b5YbOb"
    }

    //报告类型
    private val reportType get() = if (mainNav.checkedItem!!.itemId == R.id.menuReportExam) EXAM else HOMEWORK
    //报告列表
    private val reportList = ArrayList<ReportListBean>()
    //报告页码
    private val reportIndex get() = reportList.size / 10 + 1
    //报告参数
    private val reportParams get() = "reportType=$reportType&pageIndex=$reportIndex&pageSize=10&actualPosition=0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadList()
    }

    override fun initView() {
        setSupportActionBar(mainBar)
        val toggle =
            ActionBarDrawerToggle(this, mainDrawer, mainBar, R.string.appName, R.string.appName)
        val menuView = mainNav.getChildAt(0) as NavigationMenuView
        mainDrawer.addDrawerListener(toggle)
        toggle.syncState()
        menuView.isVerticalScrollBarEnabled = false
        mainNav.getHeaderView(0).headerTitle.text = cfg.curName
        db.use {
            select(TABLE_CFG, ITEM_VALUE)
                .whereSimple("$ITEM_NAME = '$REPORT_TYPE'")
                .exec { if (moveToFirst()) getString(0) else EXAM_TYPE }
                .let { mainNav.setCheckedItem(if (it == EXAM_TYPE) R.id.menuReportExam else R.id.menuReportHomework) }
        }
        mainNav.setNavigationItemSelectedListener(this)
        mainRefresh.setOnRefreshListener(this)
        mainFab.setOnClickListener { if (!mainRefresh.isRefreshing) loadList() }
        mainRecycler.addItemDecoration(ItemDecoration())
        mainRecycler.adapter = ExamListAdapter(reportList) {
            startActivity<ReportActivity>(EXAM_ID to it)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuReportExam -> changeReportType(EXAM_TYPE, item)
            R.id.menuReportHomework -> changeReportType(HOMEWORK_TYPE, item)
            R.id.menuUpdate -> onUpdate()
            R.id.menuLogout -> onLogout()
            R.id.menuFeedback -> onFeedback()
            R.id.menuSwitchAccount -> AccountDialog().show(supportFragmentManager, EMPTY_STR)
            R.id.menuAbout -> AboutDialog().show(supportFragmentManager, EMPTY_STR)
            R.id.menuBindMobile -> BindDialog().show(supportFragmentManager, EMPTY_STR)
            R.id.menuModifyPwd -> ModifyDialog().show(supportFragmentManager, EMPTY_STR)
            else -> Unit
        }
        return true
    }

    override fun onRefresh() {
        if (reportList.size == 0) loadList() else mainRefresh.isRefreshing = false
    }

    /** 改变报告类型
     * @param type 报告类型
     * @param item 菜单项
     */
    private fun changeReportType(type: String, item: MenuItem) {
        db.use { replace(TABLE_CFG, ITEM_NAME to REPORT_TYPE, ITEM_VALUE to type) }
        mainNav.setCheckedItem(item)
        mainDrawer.closeDrawers()
        mainRecycler.adapter!!.notifyItemRangeRemoved(0, reportList.size)
        reportList.clear()
        loadList()
    }

    /** 加载报告 */
    private fun loadList() {
        mainRefresh.isRefreshing = true
        launch {
            api(LIST_URL, reportParams, true, JsonObject).let {
                if (it is JSONObject) {
                    val listOldSize = reportList.size
                    val examInfoList = it.optJSONArray(EXAM_INFO_LIST)
                    for (i in 0 until examInfoList!!.length()) {
                        val item = examInfoList.optJSONObject(i)
                        reportList.add(
                            ReportListBean(
                                item.optString(EXAM_ID),
                                item.optString(EXAM_NAME),
                                item.optLong(CREATE_TIME)
                            )
                        )
                    }
                    withContext(Main) {
                        if (reportList.size % 10 == 0) mainFab.show() else mainFab.hide()
                        mainRefresh.isRefreshing = false
                        mainRecycler.adapter!!.notifyItemRangeInserted(
                            listOldSize,
                            reportList.size - listOldSize
                        )
                    }
                } else if (it is Error) withContext(Main) {
                    mainRefresh.isRefreshing = false
                    Snackbar.make(mainDrawer, it.message, LENGTH_SHORT).show()
                }
            }
        }
    }

    /** 意见反馈 */
    private fun onFeedback() {
        Snackbar
            .make(mainDrawer, R.string.toastFeedback, LENGTH_LONG)
            .setAction(R.string.actionConfirm) {
                if (!email("mail-westudio@gmail.com", "智学网Lite意见反馈")) {
                    Snackbar.make(mainDrawer, R.string.invokeMailFailed, LENGTH_SHORT).show()
                }
            }
            .show()
    }

    /** 检测更新 */
    private fun onUpdate() {
        Snackbar
            .make(mainDrawer, R.string.toastUpdate, LENGTH_LONG)
            .setAction(R.string.actionConfirm) {
                try {
                    startActivity(Intent().apply { data = Uri.parse(QQ_GROUP) })
                } catch (e: Exception) {
                    Snackbar.make(mainDrawer, R.string.invokeQQFailed, LENGTH_SHORT).show()
                }
            }
            .show()
    }

    /** 注销登录 */
    private fun onLogout() {
        Snackbar
            .make(mainDrawer, R.string.toastLogout, LENGTH_LONG)
            .setAction(R.string.actionConfirm) {
                cfg.clean()//清空当前信息
                db.use {
                    delete(TABLE_CFG, "$ITEM_NAME = {$SELECT_USER}", SELECT_USER to SELECT_USER)
                }
                startActivity<LoginActivity>()
                finish()
            }
            .show()
    }
}
