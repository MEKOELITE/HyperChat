package com.hyperchat.app.data.repository

import android.util.Base64
import com.hyperchat.app.data.remote.*
import com.hyperchat.app.domain.model.*
import com.hyperchat.app.domain.repository.AIChatRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class MiniMaxAIChatRepository @Inject constructor(
    private val apiService: MiniMaxApiService,
    private val settingsManager: com.hyperchat.app.data.local.SettingsManager
) : AIChatRepository {

    // MiniMax vision model
    private val visionModel = "abab6.5s-chat"
    private val textModel = "abab6.5s-chat"

    override suspend fun getReplySuggestions(
        conversationInfo: ConversationInfo,
        theirMessage: String,
        chatHistory: List<String>
    ): List<ReplySuggestion> {
        return try {
            val apiKey = settingsManager.apiKey.first()
            if (apiKey.isEmpty()) {
                return getMockSuggestions(conversationInfo)
            }

            val prompt = buildReplyPrompt(conversationInfo, theirMessage, chatHistory)

            val request = ChatCompletionRequest(
                model = textModel,
                messages = listOf(
                    Message(role = "system", content = "你是一位高情商沟通专家，擅长给出三种不同风格的回复建议。"),
                    Message(role = "user", content = prompt)
                ),
                temperature = 0.8
            )

            val response = apiService.chatCompletion(
                authorization = "Bearer $apiKey",
                request = request
            )

            val content = response.choices?.firstOrNull()?.message?.content ?: ""
            parseSuggestions(content)

        } catch (e: Exception) {
            e.printStackTrace()
            getMockSuggestions(conversationInfo)
        }
    }

    override suspend fun analyzeScreenshot(imageBytes: ByteArray): ScreenshotAnalysis {
        return try {
            val apiKey = settingsManager.apiKey.first()
            if (apiKey.isEmpty()) {
                return getMockScreenshotAnalysis()
            }

            if (imageBytes.isEmpty()) {
                return ScreenshotAnalysis(
                    recognizedText = "[图片为空]",
                    analysis = AIAnalysis(
                        tone = "未知",
                        intent = "未知",
                        interestLevel = 50,
                        emotionState = "未知",
                        defenseLevel = 50,
                        relationTrend = "stable"
                    ),
                    suggestions = listOf("请选择有效的图片")
                )
            }

            // Convert image to base64
            val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
            val imageUrl = "data:image/jpeg;base64,$base64Image"

            val prompt = """
请分析这张聊天截图。
请从以下角度分析：
1. 对方的态度和兴趣程度
2. 对方的情绪状态
3. 对方是否有防备心理
4. 关系发展趋势
5. 给出的建议

请用JSON格式返回，包含以下字段：
- interestLevel: 0-100的兴趣程度
- tone: 语气描述
- emotionState: 情绪状态
- defenseLevel: 0-100的防备程度
- relationTrend: rising/fallingText: 你识/stable
- recognized别的文字内容
- suggestions: 建议数组
            """.trimIndent()

            val request = ChatCompletionRequest(
                model = visionModel,
                messages = listOf(
                    Message(role = "system", content = "你是一个专业的聊天分析助手，可以分析聊天截图。"),
                    Message(
                        role = "user",
                        content = """[
                            {"type": "text", "text": "$prompt"},
                            {"type": "image_url", "image_url": {"url": "$imageUrl"}}
                        ]"""
                    )
                ),
                temperature = 0.7
            )

            val response = apiService.chatCompletion(
                authorization = "Bearer $apiKey",
                request = request
            )

            val content = response.choices?.firstOrNull()?.message?.content ?: ""
            parseScreenshotAnalysis(content)

        } catch (e: Exception) {
            e.printStackTrace()
            getMockScreenshotAnalysis()
        }
    }

    override suspend fun getConversationStrategy(
        conversationInfo: ConversationInfo,
        currentPhase: Int
    ): ConversationStrategy {
        return try {
            val apiKey = settingsManager.apiKey.first()
            if (apiKey.isEmpty()) {
                return getMockStrategy(conversationInfo, currentPhase)
            }

            val prompt = """
聊天目标：${conversationInfo.chatGoal.name}
对方身份：${conversationInfo.contactRole.name}
当前阶段：$currentPhase

请给出当前阶段的策略名称、描述和推荐话术。
请用JSON格式返回，包含：
- phaseName: 阶段名称
- phaseDescription: 阶段描述
- recommendedReply: 推荐话术
- totalPhases: 总阶段数
            """.trimIndent()

            val request = ChatCompletionRequest(
                model = textModel,
                messages = listOf(
                    Message(role = "system", content = "你是一个对话策略专家。"),
                    Message(role = "user", content = prompt)
                ),
                temperature = 0.7
            )

            val response = apiService.chatCompletion(
                authorization = "Bearer $apiKey",
                request = request
            )

            val content = response.choices?.firstOrNull()?.message?.content ?: ""
            parseStrategy(content, currentPhase)

        } catch (e: Exception) {
            e.printStackTrace()
            getMockStrategy(conversationInfo, currentPhase)
        }
    }

    override suspend fun getChatReviewReport(
        conversationInfo: ConversationInfo,
        messages: List<String>
    ): ReviewReport {
        return try {
            val apiKey = settingsManager.apiKey.first()
            if (apiKey.isEmpty()) {
                return getMockReviewReport()
            }

            val chatText = messages.joinToString("\n")

            val prompt = """
请分析以下聊天记录：
$chatText

请给出复盘报告，包含：
- emotionCurve: 情绪曲线数组（10个0-10的数字）
- investmentLevel: 对方投入度0-100
- keyTurningPoints: 关键转折点数组
- expressionIssues: 表达问题数组
- improvementSuggestions: 改进建议数组

请用JSON格式返回。
            """.trimIndent()

            val request = ChatCompletionRequest(
                model = textModel,
                messages = listOf(
                    Message(role = "system", content = "你是一个聊天复盘专家。"),
                    Message(role = "user", content = prompt)
                ),
                temperature = 0.7
            )

            val response = apiService.chatCompletion(
                authorization = "Bearer $apiKey",
                request = request
            )

            val content = response.choices?.firstOrNull()?.message?.content ?: ""
            parseReviewReport(content)

        } catch (e: Exception) {
            e.printStackTrace()
            getMockReviewReport()
        }
    }

    private fun buildReplyPrompt(
        conversationInfo: ConversationInfo,
        theirMessage: String,
        chatHistory: List<String>
    ): String {
        val goalName = when (conversationInfo.chatGoal) {
            ChatGoal.CLOSER_RELATION -> "拉近关系"
            ChatGoal.INVITE_DINNER -> "邀请吃饭"
            ChatGoal.REQUEST_HELP -> "请求帮助"
            ChatGoal.PROMOTE_PRODUCT -> "推销产品"
            ChatGoal.RECONCILE -> "挽回关系"
            ChatGoal.REFUSE -> "拒绝请求"
        }

        val roleName = when (conversationInfo.contactRole) {
            ContactRole.COLLEAGUE -> "同事"
            ContactRole.SUPERIOR -> "上级"
            ContactRole.SUBORDINATE -> "下属"
            ContactRole.CUSTOMER -> "客户"
            ContactRole.FRIEND -> "朋友"
            ContactRole.ROMANTIC -> "暧昧对象"
            ContactRole.LOVER -> "恋人"
            ContactRole.FAMILY -> "家人"
            ContactRole.STRANGER -> "陌生人"
        }

        val styleName = when (conversationInfo.chatStyle) {
            ChatStyle.SINCERE -> "真诚型"
            ChatStyle.HUMOROUS -> "幽默型"
            ChatStyle.MATURE -> "成熟稳重"
            ChatStyle.CASUAL -> "轻松自然"
            ChatStyle.PROFESSIONAL -> "商务专业"
        }

        return """
对方角色：$roleName
聊天目标：$goalName
聊天风格：$styleName
熟悉程度：${conversationInfo.familiarity}/10

对方刚才说：$theirMessage

请给出3种不同风格的回复建议，格式：
1. [理解型] 回复内容 - 理由
2. [轻松型] 回复内容 - 理由
3. [目标推进型] 回复内容 - 理由
        """.trimIndent()
    }

    // Parse JSON from response
    private fun parseSuggestions(content: String): List<ReplySuggestion> {
        val suggestions = mutableListOf<ReplySuggestion>()
        val lines = content.split("\n").filter { it.isNotBlank() }

        var type = "建议"
        lines.forEach { line ->
            if (line.contains("1") || line.contains("2") || line.contains("3")) {
                suggestions.add(
                    ReplySuggestion(
                        type = type,
                        content = line.replace(Regex("^[1-3][.、:]?\\s*"), "").take(100),
                        reason = "AI生成建议"
                    )
                )
            }
            if (line.contains("理解")) type = "理解型"
            else if (line.contains("轻松") || line.contains("幽默")) type = "轻松型"
            else if (line.contains("推进") || line.contains("目标")) type = "目标推进型"
        }

        if (suggestions.isEmpty()) {
            suggestions.add(ReplySuggestion(type = "理解型", content = content.take(80), reason = "AI生成"))
            suggestions.add(ReplySuggestion(type = "轻松型", content = content.drop(40).take(80), reason = "AI生成"))
            suggestions.add(ReplySuggestion(type = "目标推进型", content = content.drop(80).take(80), reason = "AI生成"))
        }

        return suggestions.take(3)
    }

    private fun parseScreenshotAnalysis(content: String): ScreenshotAnalysis {
        val interestLevel = extractNumber(content, "interestLevel") ?: 65
        val defenseLevel = extractNumber(content, "defenseLevel") ?: 35

        return ScreenshotAnalysis(
            recognizedText = "[已通过多模态AI分析]",
            analysis = AIAnalysis(
                tone = extractString(content, "tone") ?: "积极友好",
                intent = "聊天中",
                interestLevel = interestLevel,
                emotionState = extractString(content, "emotionState") ?: "轻松",
                defenseLevel = defenseLevel,
                relationTrend = extractString(content, "relationTrend") ?: "stable"
            ),
            suggestions = listOf(
                "对方态度${if (interestLevel > 70) "积极" else "一般"}，可以${if (interestLevel > 70) "适当推进话题" else "保持当前节奏"}",
                "根据聊天氛围调整表达方式",
                "注意把握聊天节奏"
            )
        )
    }

    private fun parseStrategy(content: String, currentPhase: Int): ConversationStrategy {
        return ConversationStrategy(
            currentPhase = currentPhase,
            totalPhases = extractNumber(content, "totalPhases") ?: 4,
            phaseName = extractString(content, "phaseName") ?: "阶段$currentPhase",
            phaseDescription = extractString(content, "phaseDescription") ?: "继续推进对话",
            recommendedReply = extractString(content, "recommendedReply") ?: "根据AI建议回复"
        )
    }

    private fun parseReviewReport(content: String): ReviewReport {
        return ReviewReport(
            emotionCurve = extractIntList(content, "emotionCurve") ?: listOf(5, 6, 7, 6, 8, 7, 9, 8, 7, 6),
            investmentLevel = extractNumber(content, "investmentLevel") ?: 65,
            keyTurningPoints = extractStringList(content, "keyTurningPoints") ?: listOf("对方开始主动分享", "提到共同兴趣"),
            expressionIssues = extractStringList(content, "expressionIssues") ?: listOf("部分表达可以改进"),
            improvementSuggestions = extractStringList(content, "improvementSuggestions") ?: listOf("多使用开放式问题")
        )
    }

    // Helper functions
    private fun extractNumber(text: String, key: String): Int? {
        val regex = Regex("$key[:\\s]*(\\d+)")
        return regex.find(text)?.groupValues?.getOrNull(1)?.toIntOrNull()
    }

    private fun extractString(text: String, key: String): String? {
        val regex = Regex("$key[:\\s]*[\"']?([^\"',}]+)")
        return regex.find(text)?.groupValues?.getOrNull(1)?.trim()
    }

    private fun extractIntList(text: String, key: String): List<Int>? {
        val regex = Regex("$key[:\\s]*\\[([^\\]]+)\\]")
        val match = regex.find(text) ?: return null
        return match.groupValues[1].split(",").mapNotNull { it.trim().toIntOrNull() }
    }

    private fun extractStringList(text: String, key: String): List<String>? {
        val regex = Regex("$key[:\\s]*\\[([^\\]]+)\\]")
        val match = regex.find(text) ?: return null
        return match.groupValues[1].split(",").map { it.replace(Regex("[\"']"), "").trim() }
    }

    // Mock methods - inline implementation
    private fun getMockSuggestions(info: ConversationInfo): List<ReplySuggestion> {
        val style = info.chatStyle
        return when (style) {
            ChatStyle.SINCERE -> listOf(
                ReplySuggestion(type = "理解型", content = "我理解你的想法，谢谢你告诉我这些。", reason = "真诚地表达理解"),
                ReplySuggestion(type = "真诚型", content = "我很想了解你的真实想法，可以多说说吗？", reason = "表达真诚的关心"),
                ReplySuggestion(type = "共情型", content = "听起来这件事让你有些困扰，我在这里支持你。", reason = "共情对方的情绪")
            )
            ChatStyle.HUMOROUS -> listOf(
                ReplySuggestion(type = "轻松型", content = "哈哈，别想太多啦！来聊聊开心的事~", reason = "用幽默缓解紧张气氛"),
                ReplySuggestion(type = "调侃型", content = "你这句话让我不知道该怎么接了，要不你再提示提示？", reason = "轻松调侃"),
                ReplySuggestion(type = "幽默型", content = "哈哈，我刚才可能脑子进水了，我们重新来过？", reason = "自嘲式幽默")
            )
            ChatStyle.MATURE -> listOf(
                ReplySuggestion(type = "稳重型", content = "我理解你的立场，我们会找到合适的解决方案的。", reason = "稳重回应"),
                ReplySuggestion(type = "理性型", content = "让我们冷静下来分析一下这个情况。", reason = "理性分析"),
                ReplySuggestion(type = "周到型", content = "考虑到各方面因素，我建议我们可以这样做...", reason = "全面考虑")
            )
            ChatStyle.CASUAL -> listOf(
                ReplySuggestion(type = "轻松型", content = "哈哈，是吗？那后来呢？", reason = "自然追问"),
                ReplySuggestion(type = "友好型", content = "哇，听起来挺有意思的！", reason = "友好回应"),
                ReplySuggestion(type = "随意型", content = "好吧，那你觉得怎么办？", reason = "轻松随意")
            )
            ChatStyle.PROFESSIONAL -> listOf(
                ReplySuggestion(type = "专业型", content = "收到，我会跟进这个事项，稍后给您反馈。", reason = "专业回应"),
                ReplySuggestion(type = "商务型", content = "感谢您的反馈，我会整理后提交给团队讨论。", reason = "商务礼仪"),
                ReplySuggestion(type = "高效型", content = "明白，我来处理这个问题。", reason = "高效直接")
            )
        }
    }

    private fun getMockScreenshotAnalysis(): ScreenshotAnalysis {
        val interestLevel = Random.nextInt(40, 90)
        val defenseLevel = Random.nextInt(10, 60)

        return ScreenshotAnalysis(
            recognizedText = "[模拟分析 - 请配置API Key获取真实分析]",
            analysis = AIAnalysis(
                tone = if (interestLevel > 70) "积极友好" else "中性平淡",
                intent = "正在沟通中",
                interestLevel = interestLevel,
                emotionState = if (defenseLevel < 30) "轻松" else "正常",
                defenseLevel = defenseLevel,
                relationTrend = when {
                    interestLevel > 70 -> "上升"
                    interestLevel < 40 -> "下降"
                    else -> "稳定"
                }
            ),
            suggestions = listOf(
                if (interestLevel > 70) "对方态度积极，可以适当推进话题" else "对方兴趣不高，建议换个话题",
                if (defenseLevel > 50) "对方有些防备，注意措辞" else "对方态度开放",
                "可以尝试增加轻松内容活跃气氛"
            )
        )
    }

    private fun getMockStrategy(info: ConversationInfo, currentPhase: Int): ConversationStrategy {
        val goal = info.chatGoal
        val phases = when (goal) {
            ChatGoal.CLOSER_RELATION -> listOf("破冰阶段" to "轻松开场，建立初步连接", "建立连接" to "寻找共同话题", "情感交流" to "分享个人经历", "关系确认" to "明确表达友好")
            ChatGoal.INVITE_DINNER -> listOf("暖场阶段" to "轻松聊天", "兴趣探测" to "了解对方喜好", "时机把握" to "提出邀请", "应对回应" to "处理回应")
            ChatGoal.REQUEST_HELP -> listOf("说明情况" to "清晰说明原因", "价值展示" to "让对方感受意义", "降低负担" to "表示不会添太多麻烦", "感谢表达" to "表示感谢")
            ChatGoal.PROMOTE_PRODUCT -> listOf("引起兴趣" to "引起注意", "需求挖掘" to "了解需求", "价值传递" to "说明价值", "促成行动" to "引导下一步")
            ChatGoal.RECONCILE -> listOf("表达诚意" to "真诚道歉", "理解对方" to "承认立场", "修复建议" to "提出改善方案", "给空间" to "给对方时间")
            ChatGoal.REFUSE -> listOf("理解请求" to "表示理解", "清晰表达" to "明确说明原因", "提供替代" to "提供其他方案", "保持友好" to "表达善意")
        }

        val (phaseName, phaseDesc) = phases.getOrElse(currentPhase - 1) { phases.last() }

        return ConversationStrategy(
            currentPhase = currentPhase,
            totalPhases = phases.size,
            phaseName = phaseName,
            phaseDescription = phaseDesc,
            recommendedReply = "[$phaseName] 根据当前阶段给出合适的回复"
        )
    }

    private fun getMockReviewReport(): ReviewReport {
        return ReviewReport(
            emotionCurve = listOf(5, 6, 7, 6, 8, 7, 9, 8, 7, 6),
            investmentLevel = Random.nextInt(40, 80),
            keyTurningPoints = listOf(
                "第3轮对话：对方开始主动分享个人经历",
                "第6轮对话：提到共同兴趣后气氛明显活跃",
                "第8轮对话：对方使用了积极的表情符号"
            ),
            expressionIssues = listOf(
                "部分表达过于直接，可能让对方感到压力",
                "缺少情感共鸣式的回应"
            ),
            improvementSuggestions = listOf(
                "多使用开放式问题，引导对方分享更多",
                "在对方分享后加入简短的情感反馈"
            )
        )
    }
}
