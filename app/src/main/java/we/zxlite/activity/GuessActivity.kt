package we.zxlite.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_guess.*
import we.zxlite.R

class GuessActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guess)
    }

    override fun initView() {
        setSupportActionBar(guessBar)
        guessBar.setNavigationOnClickListener { onBackPressed() }
    }
}
