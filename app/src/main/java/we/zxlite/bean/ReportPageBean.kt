package we.zxlite.bean

data class ReportPageBean(
    var title: String, //标题
    var paperId: String, //考卷id
    var paperName: String, //考卷名
    var standardScore: Double, //总分
    var userScore: Double, //用户得分
    var subjectCode: Int, //学科码
    var subjectName: String //学科名
)