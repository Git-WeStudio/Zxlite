package we.zxlite.activity

import android.os.Bundle
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.startActivity
import we.zxlite.R
import we.zxlite.utils.UserUtils.login
import we.zxlite.utils.BaseUtils.transition
import we.zxlite.utils.UserUtils.updateConfig

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
        updateConfig()
        withContext(Main) {
            if (login()) startActivity<MainActivity>() else startActivity<LoginActivity>()
            finish()
            transition()
        }
    }
}
