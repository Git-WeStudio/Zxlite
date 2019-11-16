package we.zxlite.bean

data class AnalyzePageBean(
    var disTitleNumber: String, //标题编号
    var topicNumber: Int, //主题编号
    var topicScoreDTOs: String, //主题分数集合
    var topicId: String, //主题id
    var topicSetId: String, //主题集id
    var analysisHtml: String, //解析Html
    var contentHtml: String, //题目Html
    var answerType: String, //答案类型
    var answerHtml: String, //答案Html
    var standardAnswer: String, //标准答案
    var standardScore: Double, //标准分数
    var score: Double, //得分
    var userAnswer: String, //用户答案
    var userAnswers: String, //用户答案集
    var imageAnswers: String, //图像答案
    var relatedKnowledgeGroups: String //知识点
)