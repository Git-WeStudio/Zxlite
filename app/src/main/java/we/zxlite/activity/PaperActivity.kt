package we.zxlite.activity

import android.annotation.SuppressLint
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_paper.*
import we.zxlite.R
import we.zxlite.utils.UserUtils.cfg

class PaperActivity : BaseActivity() {

    companion object {
        //原卷
        private const val PAPER_URL = "https://www.zhixue.com/studentanswer/index.html"
        //原卷id
        private const val PAPER_ID = "paperId"
    }

    //原卷URL
    private val paperUrl get() = "$PAPER_URL?token=${cfg.serviceToken}&paperId=$paperId"
    //原卷id
    private val paperId get() = intent.getStringExtra(PAPER_ID)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paper)
        paperWeb.loadUrl(paperUrl)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        setSupportActionBar(paperBar)
        paperBar.setNavigationOnClickListener { onBackPressed() }
        paperWeb.settings.apply {
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            loadWithOverviewMode = true
            javaScriptEnabled = true
            useWideViewPort = true
        }
    }
}
