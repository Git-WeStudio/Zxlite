package we.zxlite.utils

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.json.JSONObject
import we.zxlite.bean.UserBean
import we.zxlite.utils.BaseUtils.EMPTY_STR
import we.zxlite.utils.HttpUtils.connApi
import we.zxlite.utils.HttpUtils.Type.JsonObject

object UserUtils {

    var cfg = UserBean() //用户配置

    private const val LOG_URL = "https://www.zhixue.com/container/app/login" //登录账号
    private const val INFO_URL = "https://www.zhixue.com/zhixuebao/base/common/getUserInfo" //用户信息

    private const val USER_INFO = "userInfo"
    private const val SERVER_INFO = "serverInfo"
    private const val CUR_SERVER_TIME = "curServerTime"
    private const val CUR_CHILD_ID = "curChildId"
    private const val TOKEN = "token"
    private const val NAME = "name"

    private val logParams get() = "loginName=${cfg.logName}&password=${cfg.logPwd}&description={'encrypt':['password']}" //登录参数

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

    /** 清除配置信息 */
    fun clean() {
        cfg = UserBean()
    }
}