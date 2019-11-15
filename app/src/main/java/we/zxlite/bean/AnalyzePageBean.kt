package we.zxlite.bean

data class AnalyzePageBean(
    var disTitleNumber: String,
    var topicNumber: Int,
    var topicScoreDTOs: String,
    var topicId: String,
    var topicSetId: String,
    var analysisHtml: String,
    var contentHtml: String,
    var answerType: String,
    var answerHtml: String,
    var standardAnswer: String,
    var standardScore: Double,
    var score: Double,
    var userAnswer: String,
    var userAnswers: String,
    var imageAnswers: String,
    var relatedKnowledgeGroups: String
)