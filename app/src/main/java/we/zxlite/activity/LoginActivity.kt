package we.zxlite.activity

import android.os.Bundle
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.db.replace
import org.jetbrains.anko.startActivity
import we.zxlite.R
import we.zxlite.utils.BaseUtils.transition
import we.zxlite.utils.BaseUtils.db
import we.zxlite.utils.BaseUtils.rc4
import we.zxlite.utils.SqlUtils.Helper.Companion.ITEM_NAME
import we.zxlite.utils.SqlUtils.Helper.Companion.ITEM_VALUE
import we.zxlite.utils.SqlUtils.Helper.Companion.SELECT_USER
import we.zxlite.utils.SqlUtils.Helper.Companion.TABLE_CFG
import we.zxlite.utils.SqlUtils.Helper.Companion.TABLE_RMB
import we.zxlite.utils.UserUtils.login

class LoginActivity : BaseActivity() {

    private val logUserName //获取编辑框账号参数
        get() = loginUsername.text.toString()

    private val logUserPwd //获取编辑框密码参数
        get() = loginPassword.text.toString().rc4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun initView() {
        loginReg.setOnClickListener { startActivity<RegisterActivity>() }
        loginBtn.setOnClickListener { onLogin() }
    }

    /**检查登录参数
     * @param loginName 用户名
     * @param loginPwd 用户密码
     */
    private fun onLogin(loginName: String = logUserName, loginPwd: String = logUserPwd) {
        loginBtn.isEnabled = false
        launch {
            if (login(loginName, loginPwd)) {
                db.use {
                    replace(TABLE_RMB, ITEM_NAME to loginName, ITEM_VALUE to loginPwd)
                    replace(TABLE_CFG, ITEM_NAME to SELECT_USER, ITEM_VALUE to loginName)
                }
                withContext(Main) {
                    startActivity<MainActivity>()
                    finish()
                    transition()
                }
            } else withContext(Main) {
                loginBtn.isEnabled = true
                Snackbar.make(loginBtn, R.string.loginFailed, LENGTH_SHORT).show()
            }
        }
    }
}