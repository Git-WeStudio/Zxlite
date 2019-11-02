package we.zxlite.activity

import android.os.Bundle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.db.select
import org.jetbrains.anko.startActivity
import we.zxlite.R
import we.zxlite.utils.BaseUtils.db
import we.zxlite.utils.SqlUtils.Helper.Companion.ITEM_VALUE
import we.zxlite.utils.SqlUtils.Helper.Companion.TABLE_CFG
import we.zxlite.utils.SqlUtils.Helper.Companion.TABLE_RMB
import we.zxlite.utils.UserUtils.login
import we.zxlite.utils.BaseUtils.overridePendingTransition
import we.zxlite.utils.SqlUtils.Helper.Companion.ITEM_NAME
import we.zxlite.utils.SqlUtils.Helper.Companion.SELECT_USER

class InitActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)
    }

    override fun initView() {
        init()
    }

    /** 初始化 */
    private fun init() = launch {
        db.use {
            select(TABLE_CFG, ITEM_VALUE).whereSimple("$ITEM_NAME = '$SELECT_USER'")
                .exec { if (moveToFirst()) getString(0) else null }?.let { userName ->
                    select(TABLE_RMB, ITEM_VALUE).whereSimple("$ITEM_NAME = '$userName'")
                        .exec { if (moveToFirst()) getString(0) else null }
                        .let { userPwd -> return@use listOf(userName, userPwd) }
                }
            return@use null
        }.let {
            withContext(Main) {
                if (it != null && login(it[0], it[1]))
                    startActivity<MainActivity>() else startActivity<LoginActivity>()
                finish()
                overridePendingTransition()
            }
        }
    }
}
