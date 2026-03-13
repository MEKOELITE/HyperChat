package com.hyperchat.app.data.repository

import com.hyperchat.app.domain.model.*
import com.hyperchat.app.domain.repository.AIChatRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class MockAIChatRepository @Inject constructor() : AIChatRepository {

    // 模拟AI生成回复建议
    override suspend fun getReplySuggestions(
        conversationInfo: ConversationInfo,
        theirMessage: String,
        chatHistory: List<String>
    ): List<ReplySuggestion> {
        // 模拟网络延迟
        kotlinx.coroutines.delay(500)

        val style = conversationInfo.chatStyle
        val goal = conversationInfo.chatGoal
        val role = conversationInfo.contactRole

        return when (style) {
            ChatStyle.SINCERE -> listOf(
                ReplySuggestion(
                    type = "理解型",
                    content = "我理解你的想法，谢谢你告诉我这些。",
                    reason = "真诚地表达理解，让对方感受到被倾听"
                ),
                ReplySuggestion(
                    type = "真诚型",
                    content = "我很想了解你的真实想法，可以多说说吗？",
                    reason = "表达真诚的关心，建立深度交流"
                ),
                ReplySuggestion(
                    type = "共情型",
                    content = "听起来这件事让你有些困扰，我在这里支持你。",
                    reason = "共情对方的情绪，提供情感支持"
                )
            )
            ChatStyle.HUMOROUS -> listOf(
                ReplySuggestion(
                    type = "轻松型",
                    content = "哈哈，别想太多啦！来聊聊开心的事~",
                    reason = "用幽默缓解紧张气氛"
                ),
                ReplySuggestion(
                    type = "调侃型",
                    content = "你这句话让我不知道该怎么接了，要不你再提示提示？",
                    reason = "轻松调侃，保持互动乐趣"
                ),
                ReplySuggestion(
                    type = "幽默型",
                    content = "哈哈，我刚才可能脑子进水了，我们重新来过？",
                    reason = "自嘲式幽默，缓解尴尬"
                )
            )
            ChatStyle.MATURE -> listOf(
                ReplySuggestion(
                    type = "稳重型",
                    content = "我理解你的立场，我们会找到合适的解决方案的。",
                    reason = "稳重回应，展现成熟态度"
                ),
                ReplySuggestion(
                    type = "理性型",
                    content = "让我们冷静下来分析一下这个情况。",
                    reason = "理性分析，帮助理清思路"
                ),
                ReplySuggestion(
                    type = "周到型",
                    content = "考虑到各方面因素，我建议我们可以这样做...",
                    reason = "全面考虑，展现周到思维"
                )
            )
            ChatStyle.CASUAL -> listOf(
                ReplySuggestion(
                    type = "轻松型",
                    content = "哈哈，是吗？那后来呢？",
                    reason = "自然追问，保持聊天"
                ),
                ReplySuggestion(
                    type = "友好型",
                    content = "哇，听起来挺有意思的！",
                    reason = "友好回应，让对方有继续聊下去的兴趣"
                ),
                ReplySuggestion(
                    type = "随意型",
                    content = "好吧，那你觉得怎么办？",
                    reason = "轻松随意，不给对方压力"
                )
            )
            ChatStyle.PROFESSIONAL -> listOf(
                ReplySuggestion(
                    type = "专业型",
                    content = "收到，我会跟进这个事项，稍后给您反馈。",
                    reason = "专业回应，明确行动方向"
                ),
                ReplySuggestion(
                    type = "商务型",
                    content = "感谢您的反馈，我会整理后提交给团队讨论。",
                    reason = "商务礼仪，保持专业形象"
                ),
                ReplySuggestion(
                    type = "高效型",
                    content = "明白，我来处理这个问题。",
                    reason = "高效直接，明确表示会行动"
                )
            )
        }
    }

    // 模拟截图分析
    override suspend fun analyzeScreenshot(imageBytes: ByteArray): ScreenshotAnalysis {
        kotlinx.coroutines.delay(1000)

        // 随机生成分析结果
        val interestLevel = Random.nextInt(40, 90)
        val defenseLevel = Random.nextInt(10, 60)

        return ScreenshotAnalysis(
            recognizedText = "[聊天记录文字识别中...]",
            analysis = AIAnalysis(
                tone = if (interestLevel > 70) "积极友好" else if (interestLevel > 50) "中性平淡" else "敷衍冷淡",
                intent = "正在沟通中",
                interestLevel = interestLevel,
                emotionState = if (defenseLevel < 30) "轻松" else if (defenseLevel < 60) "正常" else "警惕",
                defenseLevel = defenseLevel,
                relationTrend = when {
                    interestLevel > 70 -> "上升"
                    interestLevel < 40 -> "下降"
                    else -> "稳定"
                }
            ),
            suggestions = listOf(
                if (interestLevel > 70) "对方态度积极，可以适当推进话题" else "对方兴趣一般，保持当前节奏",
                if (defenseLevel > 50) "对方有些防备，注意措辞不要过于直接" else "对方态度开放，可以继续深入交流",
                "可以尝试增加一些轻松的内容活跃气氛"
            )
        )
    }

    // 模拟获取对话策略
    override suspend fun getConversationStrategy(
        conversationInfo: ConversationInfo,
        currentPhase: Int
    ): ConversationStrategy {
        kotlinx.coroutines.delay(300)

        val goal = conversationInfo.chatGoal
        val phases = when (goal) {
            ChatGoal.CLOSER_RELATION -> listOf(
                "破冰阶段" to "轻松开场，建立初步连接",
                "建立连接" to "寻找共同话题，加深了解",
                "情感交流" to "分享个人经历，建立情感共鸣",
                "关系确认" to "明确表达友好意愿"
            )
            ChatGoal.INVITE_DINNER -> listOf(
                "暖场阶段" to "轻松聊天，营造愉快氛围",
                "兴趣探测" to "了解对方喜好和近期安排",
                "时机把握" to "在气氛好时提出邀请",
                "应对回应" to "处理接受或拒绝的情况"
            )
            ChatGoal.REQUEST_HELP -> listOf(
                "说明情况" to "清晰说明需要帮助的原因",
                "价值展示" to "让对方感受到帮助的意义",
                "降低负担" to "表示不会给对方添太多麻烦",
                "感谢表达" to "无论结果如何都表示感谢"
            )
            ChatGoal.PROMOTE_PRODUCT -> listOf(
                "引起兴趣" to "用有趣的方式引起注意",
                "需求挖掘" to "了解对方潜在需求",
                "价值传递" to "说明产品能带来的价值",
                "促成行动" to "引导下一步行动"
            )
            ChatGoal.RECONCILE -> listOf(
                "表达诚意" to "真诚地表达歉意",
                "理解对方" to "承认对方的感受和立场",
                "修复建议" to "提出改善关系的具体想法",
                "给空间" to "给对方时间消化，不要急于求成"
            )
            ChatGoal.REFUSE -> listOf(
                "理解请求" to "先表示理解对方的立场",
                "清晰表达" to "明确说明无法帮助的原因",
                "提供替代" to "如果可能，提供其他解决方案",
                "保持友好" to "结尾表达善意，维护关系"
            )
        }

        val (phaseName, phaseDesc) = phases.getOrElse(currentPhase - 1) { phases.last() }

        return ConversationStrategy(
            currentPhase = currentPhase,
            totalPhases = phases.size,
            phaseName = phaseName,
            phaseDescription = phaseDesc,
            recommendedReply = getRecommendedReply(conversationInfo, currentPhase)
        )
    }

    private fun getRecommendedReply(info: ConversationInfo, phase: Int): String {
        val style = when (info.chatStyle) {
            ChatStyle.SINCERE -> "真诚"
            ChatStyle.HUMOROUS -> "幽默"
            ChatStyle.MATURE -> "成熟"
            ChatStyle.CASUAL -> "自然"
            ChatStyle.PROFESSIONAL -> "专业"
        }

        return when (phase) {
            1 -> "[根据当前阶段，AI会自动生成合适的开场话术] ${style}型表达"
            2 -> "[根据对方反应，动态调整对话方向] ${style}型表达"
            3 -> "[把握时机，适时推进目标] ${style}型表达"
            else -> "[根据最终回应，选择最佳结尾方式] ${style}型表达"
        }
    }

    // 模拟聊天复盘
    override suspend fun getChatReviewReport(
        conversationInfo: ConversationInfo,
        messages: List<String>
    ): ReviewReport {
        kotlinx.coroutines.delay(800)

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
                "缺少情感共鸣式的回应",
                "有时没有及时回应对方的分享"
            ),
            improvementSuggestions = listOf(
                "多使用开放式问题，引导对方分享更多",
                "在对方分享后加入简短的情感反馈",
                "注意聊天气氛，适时调整话题"
            )
        )
    }
}
