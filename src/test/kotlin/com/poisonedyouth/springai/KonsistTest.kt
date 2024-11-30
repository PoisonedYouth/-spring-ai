package com.poisonedyouth.springai

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import org.junit.jupiter.api.Test

class KonsistTest {
    @Test
    fun `verify architecture`()  {
        Konsist
            .scopeFromProduction()
            .assertArchitecture {
                val domain = Layer("domain", "com.poisonedyouth.springai.chat.domain..")
                val application = Layer("application", "com.poisonedyouth.springai.chat.application..")
                val infrastructure = Layer("infrastructure", "com.poisonedyouth.springai.chat.infrastructure..")

                domain.dependsOnNothing()
                application.dependsOn(domain)
                infrastructure.dependsOn(domain, application)
            }
    }
}
