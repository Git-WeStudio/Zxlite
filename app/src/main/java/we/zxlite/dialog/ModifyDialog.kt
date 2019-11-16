package we.zxlite.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_modify.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.db.replace
import org.jetbrains.anko.toast
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
        private const val MODIFY_URL =
            "https://www.zhixue.com/container/app/modifyOriginPWD" //修改密码URL
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dialog_modify, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        modifyBtn.setOnClickListener {
            val newPwd = modifyNew.text.toString()
            val originPwd = modifyOrigin.text.toString()
            if (newPwd.isNotEmpty() && originPwd.isNotEmpty()) launch {
                val modifyParams =
                    "loginName=${cfg.loginName?.rc4}&newPWD=${newPwd.rc4}&originPWD=${originPwd.rc4}&description=encrypt"
                activity!!.api(MODIFY_URL, modifyParams, true, JsonObject).let {
                    withContext(Main) {
                        if (it is JSONObject) {
                            context!!.toast(R.string.modifySuccess)
                            cfg.logPwd = newPwd
                            context!!.db.use {
                                replace(TABLE_RMB, ITEM_NAME to cfg.logName, ITEM_VALUE to newPwd)
                            }
                            dismiss()
                        } else context!!.toast((it as Error).message)
                    }
                }
            } else context!!.toast(R.string.valueIncorrect)
        }
    }
}