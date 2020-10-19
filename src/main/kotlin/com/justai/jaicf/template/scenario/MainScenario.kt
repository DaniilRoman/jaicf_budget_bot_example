package com.justai.jaicf.template.scenario

import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.model.scenario.Scenario

object MainScenario : Scenario(
    dependencies = listOf(BudgetCalcScenario, MenuScenario)
) {

    const val startState = "/main"

    init {
        state(startState) {
            activators {
                regex("/start")
            }

            action {
                reactions.run {
                    buttons("Budget calc", "Menu")
                    telegram?.say("Pick one", listOf("Budget calc", "Menu"))
                }
            }

            state("Budget calc") {
                activators { regex("Budget calc") }
                action { reactions.go(BudgetCalcScenario.startState, startState) }
            }

            state("Menu") {
                activators { regex("Menu") }
                action { reactions.go(MenuScenario.startState, startState) }
            }
        }


        fallback {
            reactions.sayRandom(
                "Sorry, I didn't get that...",
                "Sorry, could you repeat please?"
            )
        }
    }
}