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
import we.zxlite.utils.BaseUtils.transition
import we.zxlite.utils.SqlUtils.Helper.Companion.ITEM_NAME
import we.zxlite.utils.SqlUtils.Helper.Companion.ITEM_VALUE
import we.zxlite.utils.SqlUtils.Helper.Companion.REPORT_TYPE
import we.zxlite.utils.SqlUtils.Helper.Companion.SELECT_USER
import we.zxlite.utils.SqlUtils.Helper.Companion.TABLE_CFG
import we.zxlite.utils.UserUtils.cfg
import we.zxlite.utils.UserUtils.cleanConfig
import android.content.Intent
import android.net.Uri
import org.jetbrains.anko.email
import we.zxlite.dialog.AboutDialog
import we.zxlite.dialog.AccountDialog

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val EXAM_TYPE = "EXAM_TYPE"
        private const val HOMEWORK_TYPE = "HOMEWORK_TYPE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

    /** 改变报告类型
     * @param type 报告类型
     * @param item 菜单项
     */
    private fun changeReportType(type: String, item: MenuItem) {
        db.use { replace(TABLE_CFG, ITEM_NAME to REPORT_TYPE, ITEM_VALUE to type) }
        mainNav.setCheckedItem(item)
        mainDrawer.closeDrawers()
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
                    startActivity(
                        Intent().apply {
                            data =
                                Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http://qm.qq.com/cgi-bin/qm/qr?from=app&p=android&k=V8t37i1Yeow7zm-RPuBenpn2TGeTynCS")
                        })
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
                cleanConfig() //清空当前信息
                db.use {
                    delete(TABLE_CFG, "$ITEM_NAME = {$SELECT_USER}", SELECT_USER to SELECT_USER)
                }
                startActivity<LoginActivity>()
                finish()
                transition()
            }
            .show()
    }
}
