package io.github.ayfri.kore.bindings

import io.github.ayfri.kore.bindings.api.importDatapacks

const val vanillaDatapackName = "vanilla-data-1.21.8.zip"

fun importingVanilla() = newTest("vanilla") {
    importDatapacks {
        configuration {
            debug = true
            outputPath(srcDir())
        }

        val resourceUrl = Thread.currentThread().contextClassLoader
            .getResource(vanillaDatapackName) ?: error("Vanilla datapack resource not found")

        url(resourceUrl)
    }
}
