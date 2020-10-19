package com.justai.jaicf.template.scenario

import com.justai.jaicf.activator.caila.caila
import com.justai.jaicf.model.scenario.Scenario

object MenuScenario : Scenario() {

    const val startState = "/menu_start"

    init {
        state(startState) {
            activators {
                regex("Menu")
            }

            action {
                reactions.say("What do you want?")
            }

            state("buy", noContext = true) {
                activators { intent("buy") }
                action {
                    reactions.run {
                        activator.caila?.run {
                            val contextDelegate = ContextDelegate(context)

                            contextDelegate.countryCurrency = slots["country"]
                            contextDelegate.dish = slots["dish"]

                            sayPrice(contextDelegate)
                            goBack()
                        }
                    }
                }
            }


            state("dish", noContext = true) {
                activators { intent("Dish") }
                action {
                    reactions.run {
                        val contextDelegate = ContextDelegate(context)
                        activator.caila?.run {
                            contextDelegate.dish = slots["dish"] as String
                        }
                        if (contextDelegate.countryCurrency == null) {
                            say("Can you type country:")
                            go("./country")
                        } else {
                            sayPrice(contextDelegate)
                            goBack()
                        }

                    }
                }
            }


            state("country", noContext = true) {
                activators { intent("Country") }
                action {
                    reactions.run {
                        val contextDelegate = ContextDelegate(context)
                        activator.caila?.run {
                            contextDelegate.countryCurrency = slots["country"]
                        }
                        if (contextDelegate.dish == null) {
                            say("Can you type dish:")
                            go("./dish")
                        } else {
                            sayPrice(contextDelegate)
                            goBack()
                        }
                    }
                }
            }

        }
    }
}