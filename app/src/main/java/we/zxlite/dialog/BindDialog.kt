package we.zxlite.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_bind.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.toast
import org.json.JSONObject
import we.zxlite.R
import we.zxlite.utils.HttpUtils.Type.JsonObject
import we.zxlite.utils.HttpUtils.api
import we.zxlite.utils.HttpUtils.Error

class BindDialog : BaseSheetDialog() {

    companion object {
        //验证码
        private const val SMS_URL =
            "https://www.zhixue.com/container/app/common/user/sendSMS4Mobile"
        //绑定手机
        private const val BIND_URL = "https://www.zhixue.com/container/app/common/user/modifyMobile"
    }

    //手机号码
    private val bindMobileNumber get() = bindMobile.text.toString()
    //手机验证码
    private val bindMobileCode get() = bindCode.text.toString()
    //短信验证参数
    private val smsParams get() = "?mobile=$bindMobileNumber"
    //绑定验证参数
    private val bindParams get() = "?mobile=$bindMobileNumber&code=$bindMobileCode"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_bind, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindCodeBtn.setOnClickListener {
            if (bindMobileNumber.isNotEmpty()) launch {
                activity!!.api(SMS_URL + smsParams, null, true, JsonObject).let {
                    withContext(Main) {
                        if (it is JSONObject) {
                            context!!.toast(R.string.smsSuccess)
                        } else {
                            context!!.toast((it as Error).message)
                        }
                    }
                }
            } else {
                context!!.toast(R.string.valueIncorrect)
            }
        }
        bindBtn.setOnClickListener {
            val mobileNumber = bindMobileNumber
            val mobileCode = bindMobileCode
            if (mobileNumber.isNotEmpty() && mobileCode.isNotEmpty()) launch {
                activity!!.api(BIND_URL + bindParams, null, true, JsonObject).let {
                    withContext(Main) {
                        if (it is JSONObject) {
                            context!!.toast(R.string.bindSuccess)
                            dismiss()
                        } else {
                            context!!.toast((it as Error).message)
                        }
                    }
                }
            } else {
                context!!.toast(R.string.valueIncorrect)
            }
        }
    }

}