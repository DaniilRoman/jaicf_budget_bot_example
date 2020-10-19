package com.justai.jaicf.template.scenario

import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.model.scenario.Scenario

object BudgetCalcScenario : Scenario() {

    const val startState = "/budget_calc"

    init {
        state(startState) {
            activators { regex("Budget calc") }

            action {
                reactions.run {
                    buttons("+", "-")
                    telegram?.say("Pick one", listOf("+", "-"))
                }
            }

            state("+", noContext = true) {
                activators { regex("\\+") }

                action {
                    val contextDelegate = ContextDelegate(context)
                    contextDelegate.sign = 1
                    reactions.say("Tape number")
                    reactions.go("number")
                }
            }

            state("-", noContext = true) {
                activators { regex("\\-") }

                action {
                    val contextDelegate = ContextDelegate(context)
                    contextDelegate.sign = -1
                    reactions.say("Tape number")
                    reactions.go("number")
                }
            }

            state("number") {
                activators { regex("\\d+") }
                action {
                    val contextDelegate = ContextDelegate(context)
                    contextDelegate.incSum(request.input.toInt())

                    reactions.run {
                        sayRandomBudget(contextDelegate)
                        telegram?.say("Pick one", listOf("What else?", "Enough"))
                        buttons("What else?", "Enough")
                    }
                }

                state("What else?", noContext = true) {
                    activators { regex("What else\\?") }
                    action {
                        val contextDelegate = ContextDelegate(context)

                        reactions.run {
                            sayRandomBudget(contextDelegate)
                            telegram?.say("Pick one", listOf("What else?", "Enough"))
                            buttons("What else?", "Enough")
                        }
                    }
                }

                state("Enough") {
                    activators { regex("Enough") }
                    action { reactions.goBack() }
                }

                fallback {
                    reactions.say(
                        "It not a number."
                    )
                }
            }
        }
    }
}