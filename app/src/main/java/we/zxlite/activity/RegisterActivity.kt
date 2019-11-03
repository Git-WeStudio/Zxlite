package we.zxlite.activity

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import we.zxlite.GlideApp
import we.zxlite.R
import we.zxlite.utils.BaseUtils.EMPTY_STR
import we.zxlite.utils.BaseUtils.color
import we.zxlite.utils.BaseUtils.bitmap
import we.zxlite.utils.BaseUtils.rc4
import we.zxlite.utils.HttpUtils.Error
import we.zxlite.utils.HttpUtils.Type.JsonObject
import we.zxlite.utils.HttpUtils.connApi

class RegisterActivity : BaseActivity() {

    //获取图像标识
    private var sid = EMPTY_STR
    //获取图像
    private var img = EMPTY_STR
    //获取编辑框手机号码
    private val regPhone get() = regPhoneNumber.text.toString()
    //获取编辑框密码
    private val regPwd get() = regPassword.text.toString().rc4
    //获取编辑框图形验证码
    private val regImage get() = regImgCode.text.toString()
    //获取编辑框手机验证码
    private val regSms get() = regSmsCode.text.toString()
    //短信参数
    private val smsParams get() = "checkCode=$regImage&mobile=$regPhone&passwd=$regPwd&sid=$sid"
    //注册参数
    private val regParams get() = "passwd=$regPwd&role=parent&smsCode=$regSms&sid=$sid"

    companion object {
        private const val REG_URL = "https://www.zhixue.com/container/app/reg/register?"//注册账号
        private const val IMG_URL = "https://www.zhixue.com/container/app/reg/getImageCode"//获取图像验证码
        private const val SMS_URL = "https://www.zhixue.com/container/app/reg/getSmsCode"//获取手机验证码

        private const val IMG = "image"
        private const val SID = "sid"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        loadCode()
    }

    override fun initView() {
        regImgBtn.setOnClickListener { loadCode() }
        regSmsBtn.setOnClickListener { getSms() }
        regBtn.setOnClickListener { onReg() }
    }

    /**获取图形验证码*/
    private fun loadCode() = launch {
        connApi(IMG_URL, EMPTY_STR, false, JsonObject).run {
            if (this is JSONObject) {
                sid = optString(SID)
                img = optString(IMG)
                GlideApp.with(this@RegisterActivity).load(img.bitmap)
                    .transform(CenterCrop(), RoundedCorners(8)).into(regImg)
            } else {
                GlideApp.with(this@RegisterActivity)
                    .load(ColorDrawable(color(R.color.colorSubTitle)))
                    .transform(CenterCrop(), RoundedCorners(8))
                    .into(regImg)
            }
        }
    }

    /** 获取短信验证码 */
    private fun getSms() {
        if (regPhone.isNotEmpty() && regPwd.isNotEmpty() && regImage.isNotEmpty()) launch {
            connApi(SMS_URL, smsParams, false, JsonObject).let {
                withContext(Main) {
                    if (it is JSONObject)
                        Snackbar.make(regSmsBtn, R.string.smsSuccess, LENGTH_SHORT).show()
                    else if (it is Error) {
                        loadCode()
                        Snackbar.make(regSmsBtn, it.message, LENGTH_SHORT).show()
                    }
                }
            }
        } else Snackbar.make(regSmsBtn, R.string.valueIncorrect, LENGTH_SHORT).show()
    }

    /** 注册账号 */
    private fun onReg() {
        regBtn.isEnabled = false
        launch {
            connApi(REG_URL, regParams, false, JsonObject).let {
                withContext(Main) {
                    if (it is JSONObject)
                        Snackbar.make(regSmsBtn, R.string.regSuccess, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.invokeAction) { finish() }
                            .show()
                    else if (it is Error)
                        Snackbar.make(regBtn, it.message, LENGTH_SHORT).show()
                    regBtn.isEnabled = true
                }
            }
        }
    }
}
