package we.zxlite.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import kotlinx.android.synthetic.main.dialog_modify.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.db.replace
import org.json.JSONObject
import we.zxlite.R
import we.zxlite.utils.HttpUtils.Type.JsonObject
import we.zxlite.utils.HttpUtils.api
import we.zxlite.utils.HttpUtils.Error
import we.zxlite.utils.UserUtils.cfg
import we.zxlite.utils.BaseUtils.db
import we.zxlite.utils.BaseUtils.rc4
import we.zxlite.utils.SqlUtils.Helper.Companion.ITEM_NAME
import we.zxlite.utils.SqlUtils.Helper.Companion.ITEM_VALUE
import we.zxlite.utils.SqlUtils.Helper.Companion.TABLE_RMB

class ModifyDialog : BaseSheetDialog() {

    companion object {
        //修改密码
        private const val MODIFY_URL = "https://www.zhixue.com/container/app/modifyOriginPWD"
    }

    //新密码
    private val modifyNewPwd get() = modifyNew.text.toString()
    //原密码
    private val modifyOriginPwd get() = modifyOrigin.text.toString()
    //修改参数
    private val modifyParams get() = "loginName=${cfg.loginName?.rc4}&newPWD=${modifyNewPwd.rc4}&originPWD=${modifyOriginPwd.rc4}&description=encrypt"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_modify, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        modifyBtn.setOnClickListener {
            val newPwd = modifyNewPwd
            val originPwd = modifyOriginPwd
            if (newPwd.isNotEmpty() && originPwd.isNotEmpty()) launch {
                activity!!.api(MODIFY_URL, modifyParams, true, JsonObject).let {
                    withContext(Main) {
                        if (it is JSONObject) {
                            Toast.makeText(context!!, R.string.modifySuccess, LENGTH_SHORT).show()
                            cfg.logPwd = newPwd
                            context!!.db.use {
                                replace(TABLE_RMB, ITEM_NAME to cfg.logName, ITEM_VALUE to newPwd)
                            }
                            dismiss()
                        } else if (it is Error) {
                            Toast.makeText(context!!, it.message, LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(context!!, R.string.valueIncorrect, LENGTH_SHORT).show()
            }
        }
    }
}