package we.zxlite.activity

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import kotlinx.android.synthetic.main.activity_main.*
import we.zxlite.R

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun initView() {
        setSupportActionBar(mainBar)
        val toggle =
            ActionBarDrawerToggle(this, mainDrawer, mainBar, R.string.appName, R.string.appName)
        mainDrawer.addDrawerListener(toggle)
        toggle.syncState()
    }
}
