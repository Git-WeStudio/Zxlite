package we.zxlite.activity

import android.content.Intent
import android.os.Bundle
import we.zxlite.R

class InitActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)
    }

    override fun initView() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}
