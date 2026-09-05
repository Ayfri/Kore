package io.github.ayfri.kore.utils

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.configuration

fun DataPack.pretty() = configuration {
	prettyPrint = true
	prettyPrintIndent = "	"
}
