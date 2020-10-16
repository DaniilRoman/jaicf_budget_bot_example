package com.justai.jaicf.template.scenario

import com.justai.jaicf.model.scenario.Scenario

object MainScenario : Scenario() {

    init {
        state("start") {
            activators {
                regex("/start")
            }

            action {
                reactions.run {
                    buttons(
                        "+",
                        "-"
                    )
                }
            }

            state("+") {
                activators { regex("\\+") }

                action {
                    val contextDelegate = ContextDelegate(context)
                    contextDelegate.sign = 1
                    reactions.say("Tape number")
                    reactions.go("number")
                }
            }

            state("-") {
                activators { regex("\\-") }

                action {
                    val contextDelegate = ContextDelegate(context)
                    contextDelegate.sign = -1
                    reactions.say("Tape number")
                    reactions.go("number")
                }
            }
        }

        state("number") {
            activators { regex("\\d+") }
            action {
                val contextDelegate = ContextDelegate(context)
                contextDelegate.incSum(request.input.toInt())

                reactions.sayRandomBudget(contextDelegate)
                reactions.buttons("What else?", "Enough")
            }

            state("What else?", noContext = true) {
                activators { regex("What else\\?") }
                action {
                    val contextDelegate = ContextDelegate(context)

                    reactions.sayRandomBudget(contextDelegate)
                    reactions.buttons("What else?", "Enough")
                }
            }

            state("Enough") {
                activators { regex("Enough") }
                action { reactions.go("/start") }
            }

            fallback {
                reactions.say(
                    "It not a number."
                )
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