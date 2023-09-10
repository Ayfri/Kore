package io.github.ayfri.kore

data class Configuration(
	var prettyPrint: Boolean = DEFAULT.prettyPrint,
	var prettyPrintIndent: String = DEFAULT.prettyPrintIndent,
	var generatedFunctionsFolder: String = DEFAULT.generatedFunctionsFolder,
) {
	companion object {
		val DEFAULT = Configuration(false, "", DataPack.DEFAULT_GENERATED_FUNCTIONS_FOLDER)
	}
}
