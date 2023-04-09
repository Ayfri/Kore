sealed class Configuration(
	val prettyPrint: Boolean,
	val prettyPrintIndent: String,
) {
	companion object Default : Configuration(false, "")
}

internal class ConfigurationImpl(
	prettyPrint: Boolean,
	prettyPrintIndent: String,
) : Configuration(prettyPrint, prettyPrintIndent)

class ConfigurationBuilder {
	var prettyPrint = false
	var prettyPrintIndent = ""

	internal fun build(): ConfigurationImpl {
		if (!prettyPrint) require(prettyPrintIndent.isEmpty()) { "Pretty print indent must be empty when pretty print is disabled" }
		require(prettyPrintIndent.all(allowedIndentCharacters::contains)) { "Only whitespace, tab, newline and carriage return are allowed as pretty print symbols. Had $prettyPrintIndent" }
		return ConfigurationImpl(prettyPrint, prettyPrintIndent)
	}

	companion object {
		val allowedIndentCharacters = setOf('\t', ' ', '\r', '\n')
	}
}

fun Configuration(init: ConfigurationBuilder.() -> Unit): Configuration = ConfigurationBuilder().apply(init).build()
