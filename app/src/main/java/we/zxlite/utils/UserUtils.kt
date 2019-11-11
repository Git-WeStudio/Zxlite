package we.zxlite.utils

import android.content.Context
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.jetbrains.anko.db.select
import org.json.JSONObject
import we.zxlite.bean.UserConfigBean
import we.zxlite.utils.BaseUtils.EMPTY_STR
import we.zxlite.utils.BaseUtils.db
import we.zxlite.utils.BaseUtils.rc4
import we.zxlite.utils.HttpUtils.connApi
import we.zxlite.utils.HttpUtils.Type.JsonObject
import we.zxlite.utils.SqlUtils.Helper.Companion.ITEM_NAME
import we.zxlite.utils.SqlUtils.Helper.Companion.ITEM_VALUE
import we.zxlite.utils.SqlUtils.Helper.Companion.SELECT_USER
import we.zxlite.utils.SqlUtils.Helper.Companion.TABLE_CFG
import we.zxlite.utils.SqlUtils.Helper.Companion.TABLE_RMB
import java.lang.System.currentTimeMillis

object UserUtils {

    //用户配置
    var cfg = UserConfigBean()
    //检测登录是否过期
    val isExpired get() = (cfg.serviceTime ?: 0) + 3600000L < currentTimeMillis()

    private const val LOG_URL = "https://www.zhixue.com/container/app/login" //登录账号URL
    private const val INFO_URL =
        "https://www.zhixue.com/zhixuebao/base/common/getUserInfo" //用户信息URL

    private const val USER_INFO = "userInfo" //用户信息
    private const val SERVER_INFO = "serverInfo" //服务信息
    private const val CUR_SERVER_TIME = "curServerTime" //服务时间
    private const val CUR_CHILD_ID = "curChildId" //选定Id
    private const val LOGIN_NAME = "loginName" //登录名
    private const val TOKEN = "token" //验证码
    private const val NAME = "name" //名字
    //登录参数
    private val logParams get() = "loginName=${cfg.logName}&password=${cfg.logPwd?.rc4}&description={'encrypt':['password']}"

    /** 登录
     * @param userName 用户名
     * @param userPwd 用户密码
     */
    suspend fun login(
        userName: String? = cfg.logName,
        userPwd: String? = cfg.logPwd
    ) = GlobalScope.async {
        cfg.logName = userName
        cfg.logPwd = userPwd
        connApi(LOG_URL, logParams, false, JsonObject).run {
            if (this is JSONObject) {
                cfg.loginName = optJSONObject(USER_INFO)!!.optString(LOGIN_NAME)
                cfg.curName = optJSONObject(USER_INFO)!!.optString(NAME)
                cfg.serviceTime = optJSONObject(SERVER_INFO)!!.optLong(CUR_SERVER_TIME)
                cfg.serviceToken = optString(TOKEN)
                connApi(INFO_URL, EMPTY_STR, true, JsonObject).run {
                    if (this is JSONObject) cfg.curId = optString(CUR_CHILD_ID)
                }
            }
        }
        return@async cfg.curId != null
    }.await()

    /** 更新信息 */
    fun Context.updateConfig() = db.use {
        select(TABLE_CFG, ITEM_VALUE).whereSimple("$ITEM_NAME = '$SELECT_USER'")
            .exec { if (moveToFirst()) getString(0) else null }
            ?.let { userName ->
                select(TABLE_RMB, ITEM_VALUE).whereSimple("$ITEM_NAME = '$userName'")
                    .exec { if (moveToFirst()) getString(0) else null }
                    ?.let { userPwd ->
                        cfg.logName = userName
                        cfg.logPwd = userPwd
                    }
            }
    }
}