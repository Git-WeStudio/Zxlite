package we.zxlite.utils

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.json.JSONArray
import org.json.JSONObject
import we.zxlite.utils.BaseUtils.EMPTY_STR
import we.zxlite.utils.BaseUtils.conn
import we.zxlite.utils.BaseUtils.md5
import we.zxlite.utils.UserUtils.cfg
import we.zxlite.utils.HttpUtils.Type.JsonObject
import we.zxlite.utils.HttpUtils.Type.JsonArray
import java.io.DataOutputStream
import java.lang.System.currentTimeMillis
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.UUID.randomUUID
import java.util.concurrent.TimeoutException

object HttpUtils {
    private const val AUTH_KEY = "iflytek!@#123student"
    private const val AUTH_CODE = "authbizcode"
    private const val AUTH_GUID = "authguid"
    private const val AUTH_TIME = "authtimestamp"
    private const val AUTH_TOKEN = "authtoken"
    private const val ERROR_CODE = "errorCode"
    private const val ERROR_INFO = "errorInfo"
    private const val ERROR_RESULT = "result"

    private val tokenParams get() = "?token=${cfg.token}&childrenId=${cfg.userId}"

    /** 回调数据类型 */
    enum class Type {
        JsonObject, JsonArray
    }

    /** 接口访问
     * @param url 接口地址
     * @param params 提交数据
     * @param add 增加验证
     * @param type 返回数据类型
     */
    suspend fun connApi(url: String, params: String, add: Boolean, type: Type?) =
        GlobalScope.async {
            val urlConn = ("$url${if (add) tokenParams else EMPTY_STR}").conn()
            try {
                val authCode = "0001"
                val authGuid = randomUUID().toString()
                val authTime = currentTimeMillis().toString()
                val authToken = (authGuid + authTime + AUTH_KEY).md5
                urlConn.apply {
                    requestMethod = "POST"
                    doOutput = true
                    readTimeout = 10 * 1000
                    connectTimeout = 10 * 1000
                    setRequestProperty(AUTH_CODE, authCode)
                    setRequestProperty(AUTH_GUID, authGuid)
                    setRequestProperty(AUTH_TIME, authTime)
                    setRequestProperty(AUTH_TOKEN, authToken)
                }.run {
                    val stream = DataOutputStream(outputStream)
                    stream.writeBytes(params)
                    stream.flush()
                    stream.close()
                    val resultData = inputStream.reader().readText()
                    return@async when (type) {
                        JsonObject -> resultData.jsonObject
                        JsonArray -> resultData.jsonArray
                        else -> resultData
                    }
                }
            } catch (e: Exception) {
                return@async Error(e)
            } finally {
                urlConn.disconnect()
            }
        }.await()

    /** 转为JSON */
    private fun String.json(): String = JSONObject(this).run {
        if (optInt(ERROR_CODE) == 0) optString(ERROR_RESULT)
        else throw Exception(optString(ERROR_INFO))
    }

    /** 转为JSONObject */
    private val String.jsonObject: Any
        get() {
            val json = json()
            return if (json.isNotEmpty()) JSONObject(json) else JSONObject()
        }

    /** 转为JSONArray */
    private val String.jsonArray: Any
        get() {
            val json = json()
            return if (json.isNotEmpty()) JSONArray(json) else JSONArray()
        }

    /** 返回错误信息 */
    class Error(private val error: Exception) {
        val message: String
            get() = when (error) {
                is UnknownHostException -> "网络连接错误"
                is SocketTimeoutException -> "网络连接超时"
                else -> error.message.toString()
            }
    }
}