package com.justai.jaicf.template.scenario

import com.justai.jaicf.context.BotContext
import com.justai.jaicf.reactions.Reactions
import com.justai.jaicf.template.budgetItems
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.double
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*


@KtorExperimentalAPI
val httpClient = HttpClient(CIO) {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
}

class ContextDelegate(context: BotContext) {

    var sign: Int? by context.client
    var sum: Int? by context.client

    fun incSum(input: Int) {
        sum = sum ?: 0
        sign = sign ?: 1
        sum = sum!! + (sign!! * input)
    }

    fun convertedSum(pickedPrice: Double): Double {
        return sum!!*pickedPrice
    }
}

data class BudgetItemModel(val name: String, val price: Double, val imgUrl: String)

enum class Currency {
    CHF,
    EUR,
}

fun <T>random(list: List<T>): T {
    return list[Random().nextInt(list.size)]
}

fun Reactions.sayRandomBudget(context: ContextDelegate) {
    val url = "https://api.ratesapi.io/api/latest?base=USD"
    val currency = runBlocking {
        httpClient.get<JsonObject>(url)
    }

    val pickedCurrency = random(Currency.values().toList())
    val pickedPrice = currency["rates"]!!.jsonObject.get(pickedCurrency.name)!!.double

    val variant = random(budgetItems[pickedCurrency]!!)
    val itemCount = BigDecimal(context.convertedSum(pickedPrice) / variant.price).setScale(2, RoundingMode.HALF_EVEN)
    image(variant.imgUrl)
    say("Your budget: $itemCount ${variant.name}'s")
}
data class CurrencyResponse(val base: String, val rates: Map<String, Double>, val date: Date)