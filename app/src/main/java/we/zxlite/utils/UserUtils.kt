package we.zxlite.utils

import android.content.Context
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.jetbrains.anko.db.select
import org.json.JSONObject
import we.zxlite.bean.UserBean
import we.zxlite.utils.BaseUtils.EMPTY_STR
import we.zxlite.utils.BaseUtils.db
import we.zxlite.utils.HttpUtils.connApi
import we.zxlite.utils.HttpUtils.Type.JsonObject
import java.lang.System.currentTimeMillis

object UserUtils {

    var cfg = UserBean() //用户配置

    //检测过期
    val isExpired get() = (cfg.serviceTime ?: 0) - 3600000L > currentTimeMillis()

    private const val LOG_URL = "https://www.zhixue.com/container/app/login" //登录账号
    private const val INFO_URL = "https://www.zhixue.com/zhixuebao/base/common/getUserInfo" //用户信息

    private const val USER_INFO = "userInfo" //用户信息
    private const val SERVER_INFO = "serverInfo" //服务信息
    private const val CUR_SERVER_TIME = "curServerTime" //服务时间
    private const val CUR_CHILD_ID = "curChildId" //选定Id
    private const val TOKEN = "token" //验证码
    private const val NAME = "name" //名字
    //登录参数
    private val logParams get() = "loginName=${cfg.logName}&password=${cfg.logPwd}&description={'encrypt':['password']}"

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
                cfg.userName = optJSONObject(USER_INFO)!!.optString(NAME)
                cfg.token = optString(TOKEN)
                cfg.serviceTime = optJSONObject(SERVER_INFO)!!.optLong(CUR_SERVER_TIME)
                connApi(INFO_URL, EMPTY_STR, true, JsonObject).run {
                    if (this is JSONObject) cfg.userId = optString(CUR_CHILD_ID)
                }
            }
        }
        return@async cfg.userId != null
    }.await()

    /** 更新信息 */
    fun Context.updateConfig() {
        db.use {
            select(SqlUtils.Helper.TABLE_CFG, SqlUtils.Helper.ITEM_VALUE)
                .whereSimple("${SqlUtils.Helper.ITEM_NAME} = '${SqlUtils.Helper.SELECT_USER}'")
                .exec { if (moveToFirst()) getString(0) else null }
                ?.let { userName ->
                    select(SqlUtils.Helper.TABLE_RMB, SqlUtils.Helper.ITEM_VALUE)
                        .whereSimple("${SqlUtils.Helper.ITEM_NAME} = '$userName'")
                        .exec { if (moveToFirst()) getString(0) else null }
                        ?.let { userPwd ->
                            cfg.logName = userName
                            cfg.logPwd = userPwd
                        }
                }
        }
    }

    /** 清除配置信息 */
    fun cleanConfig() {
        cfg = UserBean()
    }
}