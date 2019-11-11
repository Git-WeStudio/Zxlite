package we.zxlite.bean

data class UserConfigBean(
    var logName: String? = null, //保存的用户名
    var logPwd: String? = null, //保存的密码
    var curId: String? = null, //选中id
    var curName: String? = null, //选中用户名
    var loginName: String? = null, //登录名
    var serviceTime: Long? = null, //服务时间
    var serviceToken: String? = null //验证码
) {

    /** 清空配置 */
    fun clean() {
        logName = null
        logPwd = null
        curId = null
        curName = null
        loginName = null
        serviceTime = null
        serviceToken = null
    }
}