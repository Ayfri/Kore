package io.github.ayfri.kore.bindings

import io.github.ayfri.kore.bindings.api.importDatapacks

fun dungeonsAndTavernsTests() = newTest("dungeonsAndTaverns") {
    importDatapacks {
        configuration {
			outputPath(srcDir())
		}

        url("https://cdn.modrinth.com/data/tpehi7ww/versions/xjVWiPK3/Dungeons%20and%20Taverns%20v4.7.3.zip")
    }
}
