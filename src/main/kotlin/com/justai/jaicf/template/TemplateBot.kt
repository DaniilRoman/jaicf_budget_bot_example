package com.justai.jaicf.template

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.justai.jaicf.BotEngine
import com.justai.jaicf.activator.caila.CailaIntentActivator
import com.justai.jaicf.activator.caila.CailaNLUSettings
import com.justai.jaicf.activator.regex.RegexActivator
import com.justai.jaicf.channel.jaicp.logging.JaicpConversationLogger
import com.justai.jaicf.template.scenario.BudgetItemModel
import com.justai.jaicf.template.scenario.Currency
import com.justai.jaicf.template.scenario.MainScenario
import java.util.*

val budgetItems: Map<Currency, List<BudgetItemModel>> = ObjectMapper().registerKotlinModule().readValue(
    BotEngine::class.java.getResource("/items.json"),
    object : TypeReference<Map<Currency, List<BudgetItemModel>>>() {}
)

val accessToken: String = System.getenv("JAICP_API_TOKEN") ?: Properties().run {
    load(CailaNLUSettings::class.java.getResourceAsStream("/jaicp.properties"))
    getProperty("apiToken")
}

private val cailaNLUSettings = CailaNLUSettings(
    accessToken = accessToken
)

val templateBot = BotEngine(
    model = MainScenario.model,
    conversationLoggers = arrayOf(
        JaicpConversationLogger(accessToken)
    ),
    activators = arrayOf(
        CailaIntentActivator.Factory(cailaNLUSettings),
        RegexActivator
    )
)
