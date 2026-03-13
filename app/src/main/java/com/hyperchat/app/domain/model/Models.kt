package com.hyperchat.app.domain.model

// 对方身份
enum class ContactRole {
    COLLEAGUE,      // 同事
    SUPERIOR,       // 上级
    SUBORDINATE,    // 下属
    CUSTOMER,       // 客户
    FRIEND,         // 朋友
    ROMANTIC,       // 暧昧对象
    LOVER,          // 恋人
    FAMILY,         // 家人
    STRANGER        // 陌生人
}

// 聊天目标
enum class ChatGoal {
    CLOSER_RELATION,       // 拉近关系
    INVITE_DINNER,         // 邀请吃饭
    REQUEST_HELP,          // 请求帮助
    PROMOTE_PRODUCT,       // 推销产品
    RECONCILE,             // 挽回关系
    REFUSE                 // 拒绝请求
}

// 聊天风格
enum class ChatStyle {
    SINCERE,      // 真诚型
    HUMOROUS,     // 幽默型
    MATURE,       // 成熟稳重
    CASUAL,       // 轻松自然
    PROFESSIONAL  // 商务专业
}

// 会话信息
data class ConversationInfo(
    val id: Long = 0,
    val contactRole: ContactRole = ContactRole.FRIEND,
    val familiarity: Int = 5,        // 1-10 熟悉程度
    val relationTemperature: Int = 5, // 1-10 关系温度
    val hasHistoryConflict: Boolean = false,
    val chatGoal: ChatGoal = ChatGoal.CLOSER_RELATION,
    val chatStyle: ChatStyle = ChatStyle.CASUAL,
    val createdAt: Long = System.currentTimeMillis()
)

// 对方消息
data class TheirMessage(
    val id: Long = 0,
    val conversationId: Long = 0,
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

// AI分析结果
data class AIAnalysis(
    val tone: String = "",              // 语气
    val intent: String = "",            // 意图
    val interestLevel: Int = 50,         // 兴趣程度 0-100
    val emotionState: String = "",      // 情绪状态
    val defenseLevel: Int = 50,         // 防御程度 0-100
    val relationTrend: String = "stable" // 关系趋势: rising/falling/stable
)

// 回复建议
data class ReplySuggestion(
    val id: Long = 0,
    val conversationId: Long = 0,
    val type: String = "",              // 方案类型: 理解型/轻松型/目标推进型
    val content: String = "",           // 回复内容
    val reason: String = ""             // 建议理由
)

// 对话策略
data class ConversationStrategy(
    val id: Long = 0,
    val conversationId: Long = 0,
    val currentPhase: Int = 1,          // 当前阶段
    val totalPhases: Int = 4,            // 总阶段数
    val phaseName: String = "",          // 阶段名称
    val phaseDescription: String = "",  // 阶段描述
    val recommendedReply: String = ""    // 推荐话术
)

// 高情商库场景
enum class EQScenario {
    DATING,          // 恋爱聊天
    WORKPLACE,       // 职场沟通
    SALES,           // 客户销售
    CONFLICT,        // 冲突化解
    SOCIAL           // 社交破冰
}

// 高情商库示例
data class EQExample(
    val id: Long = 0,
    val scenario: EQScenario = EQScenario.SOCIAL,
    val situation: String = "",         // 场景描述
    val lowEQResponse: String = "",    // 低情商表达
    val highEQResponse: String = ""     // 高情商表达
)

// 聊天复盘报告
data class ReviewReport(
    val id: Long = 0,
    val conversationId: Long = 0,
    val emotionCurve: List<Int> = emptyList(),  // 情绪曲线
    val investmentLevel: Int = 50,              // 对方投入度
    val keyTurningPoints: List<String> = emptyList(),  // 关键转折点
    val expressionIssues: List<String> = emptyList(),   // 表达问题
    val improvementSuggestions: List<String> = emptyList()  // 改进建议
)

// 截图分析结果
data class ScreenshotAnalysis(
    val recognizedText: String = "",
    val analysis: AIAnalysis = AIAnalysis(),
    val suggestions: List<String> = emptyList()
)
