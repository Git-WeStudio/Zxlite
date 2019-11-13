package we.zxlite.bean

data class AnalyzePageBean(
    var dispTitleNumber: String, //题号
    var dispTitle: String, //题标题
    var answerType: String, //答案类型
    var answerHtml: String, //答案Html
    var topicSetId: String, //主题id
    var topicScoreDTOs: String, //主题分数
    var standardAnswer: String, //标准答案
    var userAnswer: String, //用户答案
    var userAnswers: String, //用户答案
    var imageAnswer: String, //图像答案
    var analysisHtml: String, //解析Html
    var standardScore: Double, //标准分数
    var score: Double, //分数
    var relatedKnowledgeGroups: String //知识点
)