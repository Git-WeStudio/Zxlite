package we.zxlite.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_report.*
import we.zxlite.R

class ReportActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
    }

    override fun initView() {
        setSupportActionBar(reportBar)
        reportBar.setNavigationOnClickListener { onBackPressed() }
    }
}
