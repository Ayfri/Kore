package io.github.ayfri.kore

/**
* Global configuration options used when serializing and generating
* datapack content.
*
* Docs: https://kore.ayfri.com/docs/configuration
*
* @property prettyPrint - Whether to pretty print generated JSON
* @property prettyPrintIndent - String used for indentation when pretty printing
* @property generatedFunctionsFolder - The folder where the generated functions are stored. Defaults to "generated_scopes".
* @property generateCommentOfGeneratedFunctionCall - Whether to include a comment when an implicit generated function is called
*/
data class Configuration(
	var prettyPrint: Boolean = DEFAULT.prettyPrint,
	var prettyPrintIndent: String = DEFAULT.prettyPrintIndent,
	var generatedFunctionsFolder: String = DEFAULT.generatedFunctionsFolder,
	var generateCommentOfGeneratedFunctionCall: Boolean = DEFAULT.generateCommentOfGeneratedFunctionCall,
) {
	companion object {
		/** Default configuration instance. */
		val DEFAULT = Configuration(false, "", DataPack.DEFAULT_GENERATED_FUNCTIONS_FOLDER, true)
	}
}
