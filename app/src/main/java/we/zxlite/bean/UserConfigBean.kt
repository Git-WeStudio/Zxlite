package we.zxlite.bean

data class UserConfigBean(
    var logName: String? = null, //保存的用户名
    var logPwd: String? = null, //保存的密码
    var curId: String? = null, //选中id
    var curName: String? = null, //选中用户名
    var loginName: String? = null, //登录名
    var serviceTime: Long? = null, //服务时间
    var token: String? = null //验证码
)