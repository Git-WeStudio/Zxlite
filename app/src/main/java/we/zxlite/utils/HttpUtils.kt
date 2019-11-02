package we.zxlite.utils

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import we.zxlite.utils.BaseUtils.conn
import we.zxlite.utils.BaseUtils.md5
import we.zxlite.utils.UserUtils.config
import we.zxlite.utils.HttpUtils.Type.JsonObject
import we.zxlite.utils.HttpUtils.Type.JsonArray
import java.io.DataOutputStream
import java.lang.System.currentTimeMillis
import java.net.UnknownHostException
import java.util.UUID.randomUUID

object HttpUtils {
    private const val AUTH_KEY = "iflytek!@#123student"
    private const val AUTH_CODE = "authbizcode"
    private const val AUTH_GUID = "authguid"
    private const val AUTH_TIME = "authtimestamp"
    private const val AUTH_TOKEN = "authtoken"
    private const val ERROR_CODE = "errorCode"
    private const val ERROR_INFO = "errorInfo"
    private const val ERROR_RESULT = "result"

    /** 回调数据类型 */
    enum class Type {
        JsonObject, JsonArray
    }

    /** 接口访问
     * @param url 接口地址
     * @param params 提交数据
     * @param add 增加验证
     * @param type 返回数据类型
     * @param callback 回调
     */
    suspend fun connApi(
        url: String,
        params: String,
        add: Boolean,
        type: Type?,
        callback: (Any) -> Unit
    ) {
        GlobalScope.launch {
            val urlConn =
                (if (add) "$url&token=${config.token}&childrenId=${config.userId}" else url).conn()
            try {
                val authCode = "0001"
                val authGuid = randomUUID().toString()
                val authTime = currentTimeMillis().toString()
                val authToken = (authGuid + authTime + AUTH_KEY).md5()
                urlConn.apply {
                    requestMethod = "POST"
                    doOutput = true
                    readTimeout = 20 * 1000
                    connectTimeout = 20 * 1000
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
                    callback(
                        when (type) {
                            JsonObject -> resultData.objectOf()
                            JsonArray -> resultData.arrayOf()
                            else -> resultData
                        }
                    )
                }
            } catch (e: Exception) {
                callback(Error(e))
            } finally {
                urlConn.disconnect()
            }
        }.join()
    }

    /** 转为JSON */
    private fun String.json(): String = JSONObject(this).run {
        if (optInt(ERROR_CODE) == 0) optString(ERROR_RESULT)
        else throw Exception(optString(ERROR_INFO))
    }

    /** 转为JSONObject */
    private fun String.objectOf(): Any = json().run {
        if (isNotEmpty()) JSONObject(this) else JSONObject()
    }

    /** 转为JSONArray */
    private fun String.arrayOf(): Any = json().run {
        if (isNotEmpty()) JSONArray(this) else JSONArray()
    }

    /** 返回错误信息 */
    class Error(private val error: Exception) {
        fun get() = when (error) {
            is UnknownHostException -> "网络连接错误"
            else -> error.message!!.toString()
        }
    }
}