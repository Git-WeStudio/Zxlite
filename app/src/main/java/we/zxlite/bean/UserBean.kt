package we.zxlite.bean

data class UserBean(
    var logName: String? = null, //保存的用户名
    var logPwd: String? = null, //保存的密码
    var userId: String? = null, //用户id
    var userName: String? = null, //用户名
    var serviceTime: Long? = null, //服务时间
    var token: String? = null //验证码
)