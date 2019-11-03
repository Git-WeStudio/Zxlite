package we.zxlite.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.browse
import org.jetbrains.anko.db.replace
import org.jetbrains.anko.db.select
import org.jetbrains.anko.startActivity
import we.zxlite.R
import we.zxlite.utils.BaseUtils.transition
import we.zxlite.utils.BaseUtils.db
import we.zxlite.utils.SqlUtils.Helper.Companion.ITEM_NAME
import we.zxlite.utils.SqlUtils.Helper.Companion.ITEM_VALUE
import we.zxlite.utils.SqlUtils.Helper.Companion.SELECT_USER
import we.zxlite.utils.SqlUtils.Helper.Companion.TABLE_CFG
import we.zxlite.utils.SqlUtils.Helper.Companion.TABLE_RMB
import we.zxlite.utils.UserUtils.login

class LoginActivity : BaseActivity() {

    companion object {
        //忘记密码
        private const val FORGET_URL =
            "http://pass.changyan.com/forget?customConfig=e3ZpZXdfdHlwZTogIkg1IixoaWRkZW5fbW9kdWxlOiAiZm9yZ2V0QWNjb3VudCIsbG9nbzoiemhpeHVlIix0aGVtZToiZ3JlZW4iLGNhbGxiYWNrX3R5cGU6InBvc3RNZXNzYWdlIixsb2dpbl90eXBlOiJtYW51YWwifQ=="
    }

    private val logUserName //获取编辑框账号参数
        get() = loginUsername.text.toString()

    private val logUserPwd //获取编辑框密码参数
        get() = loginPassword.text.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun initView() {
        loginUsername.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.length > 6) db.use {
                    select(TABLE_RMB, ITEM_VALUE)
                        .whereSimple("$ITEM_NAME = '${logUserName}'")
                        .exec { if (moveToFirst()) getString(0) else null }
                        ?.let { loginPassword.setText(it) }
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        loginForget.setOnClickListener { browse(FORGET_URL) }
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