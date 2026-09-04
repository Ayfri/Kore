package io.github.ayfri.kore

import io.github.ayfri.kore.optimization.Optimization

/**
* Global configuration options used when serializing and generating
* datapack content.
*
 * Docs: https://kore.ayfri.com/docs/guides/configuration
*
* @property generateCommentOfGeneratedFunctionCall - Whether to include a comment when an implicit generated function is called
* @property generatedFunctionsFolder - The folder where the generated functions are stored. Defaults to "generated_scopes".
* @property optimization - The whole-pack optimization passes run before writing, disabled by default
* @property prettyPrint - Whether to pretty print generated JSON
* @property prettyPrintIndent - String used for indentation when pretty printing
*/
data class Configuration(
	var generateCommentOfGeneratedFunctionCall: Boolean = DEFAULT.generateCommentOfGeneratedFunctionCall,
	var generatedFunctionsFolder: String = DEFAULT.generatedFunctionsFolder,
	var optimization: Optimization = Optimization(),
	var prettyPrint: Boolean = DEFAULT.prettyPrint,
	var prettyPrintIndent: String = DEFAULT.prettyPrintIndent,
) {
	companion object {
		/** Default configuration instance. */
		val DEFAULT = Configuration(
			generateCommentOfGeneratedFunctionCall = false,
			generatedFunctionsFolder = DataPack.DEFAULT_GENERATED_FUNCTIONS_FOLDER,
			optimization = Optimization(),
			prettyPrint = false,
			prettyPrintIndent = "\t",
		)
	}
}
