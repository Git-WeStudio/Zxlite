package we.zxlite.activity

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.os.Build.VERSION_CODES.M
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import we.zxlite.R
import we.zxlite.utils.BaseUtils.color

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.initStatusBar()
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        initView()
    }

    /** 初始化状态栏
     * Android 6.0 白底黑字状态栏
     * Android 5.0 暗色状态栏
     */
    private fun Window.initStatusBar() {
        if (SDK_INT >= M) {
            decorView.systemUiVisibility = SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else if (SDK_INT >= LOLLIPOP) {
            statusBarColor = color(R.color.colorGreyBar)
        }
    }

    /** 初始化界面 */
    abstract fun initView()
}