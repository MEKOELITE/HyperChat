package com.hyperchat.app.data.repository

import com.hyperchat.app.domain.model.EQExample
import com.hyperchat.app.domain.model.EQScenario
import com.hyperchat.app.domain.repository.EQLibraryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockEQLibraryRepository @Inject constructor() : EQLibraryRepository {

    private val examples = mapOf(
        EQScenario.DATING to listOf(
            EQExample(
                scenario = EQScenario.DATING,
                situation = "对方说 最近有点忙",
                lowEQResponse = "好吧，那你忙吧",
                highEQResponse = "最近这么忙啊，听起来挺辛苦的，等你空一点我们再约"
            ),
            EQExample(
                scenario = EQScenario.DATING,
                situation = "想约对方吃饭",
                lowEQResponse = "你有空出来吃饭吗？",
                highEQResponse = "我发现一家超棒的餐厅，特别想带你去尝尝~"
            ),
            EQExample(
                scenario = EQScenario.DATING,
                situation = "对方回复比较敷衍",
                lowEQResponse = "你怎么不理我？",
                highEQResponse = "感觉你最近可能比较累，有什么我能帮到的吗？"
            ),
            EQExample(
                scenario = EQScenario.DATING,
                situation = "想表达好感",
                lowEQResponse = "我喜欢你",
                highEQResponse = "和你聊天的时候，感觉时间过得特别快"
            )
        ),
        EQScenario.WORKPLACE to listOf(
            EQExample(
                scenario = EQScenario.WORKPLACE,
                situation = "向领导汇报坏消息",
                lowEQResponse = "领导，出问题了",
                highEQResponse = "领导，有个情况需要向您汇报，目前的进展是...我已经准备了几个应对方案"
            ),
            EQExample(
                scenario = EQScenario.WORKPLACE,
                situation = "拒绝同事的额外工作",
                lowEQResponse = "我不行，没时间",
                highEQResponse = "这个项目我也很想帮忙，但目前手头工作已经满了，我们能否一起想想其他办法？"
            ),
            EQExample(
                scenario = EQScenario.WORKPLACE,
                situation = "请求上级支持",
                lowEQResponse = "领导，我需要你帮忙",
                highEQResponse = "关于这个项目，我想请教您的看法，特别是...方面"
            ),
            EQExample(
                scenario = EQScenario.WORKPLACE,
                situation = "下属犯错",
                lowEQResponse = "你怎么又做错了！",
                highEQResponse = "这次的结果和我们预期有些差距，我们一起来看看哪里可以改进"
            )
        ),
        EQScenario.SALES to listOf(
            EQExample(
                scenario = EQScenario.SALES,
                situation = "客户说太贵了",
                lowEQResponse = "这是最低价了",
                highEQResponse = "我理解您的顾虑，让我们来看看这个方案能为您带来什么价值"
            ),
            EQExample(
                scenario = EQScenario.SALES,
                situation = "客户表示要考虑",
                lowEQResponse = "那你想好了告诉我",
                highEQResponse = "当然，了解您需要时间考虑。方便问一下您主要考虑哪些方面吗？"
            ),
            EQExample(
                scenario = EQScenario.SALES,
                situation = "被客户拒绝",
                lowEQResponse = "那好吧，再见",
                highEQResponse = "感谢您的坦诚。如果您以后有任何需求，随时欢迎联系"
            )
        ),
        EQScenario.CONFLICT to listOf(
            EQExample(
                scenario = EQScenario.CONFLICT,
                situation = "对方情绪激动",
                lowEQResponse = "你先冷静一下",
                highEQResponse = "我感受到你很在意这件事，我也希望能理解你的想法"
            ),
            EQExample(
                scenario = EQScenario.CONFLICT,
                situation = "想化解矛盾",
                lowEQResponse = "算了，我不和你计较",
                highEQResponse = "我觉得我们之间可能有些误会，我想听听你的想法"
            ),
            EQExample(
                scenario = EQScenario.CONFLICT,
                situation = "被对方误解",
                lowEQResponse = "你理解错了！",
                highEQResponse = "可能是我没说清楚，我的意思是..."
            )
        ),
        EQScenario.SOCIAL to listOf(
            EQExample(
                scenario = EQScenario.SOCIAL,
                situation = "初次见面破冰",
                lowEQResponse = "你好",
                highEQResponse = "很高兴认识你，我听说你最近在..."
            ),
            EQExample(
                scenario = EQScenario.SOCIAL,
                situation = "想加入陌生群体",
                lowEQResponse = "你们在聊什么？",
                highEQResponse = "刚好听到你们聊...这个话题真有意思"
            ),
            EQExample(
                scenario = EQScenario.SOCIAL,
                situation = "感谢帮助",
                lowEQResponse = "谢谢",
                highEQResponse = "多亏了你帮忙，真的很感谢你花时间帮我"
            )
        )
    )

    override fun getExamplesByScenario(scenario: EQScenario): List<EQExample> {
        return examples[scenario] ?: emptyList()
    }

    override fun getAllScenarios(): List<EQScenario> {
        return EQScenario.entries
    }
}
