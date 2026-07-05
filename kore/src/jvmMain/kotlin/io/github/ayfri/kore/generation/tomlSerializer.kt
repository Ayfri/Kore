package io.github.ayfri.kore.generation

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.TomlIndentation
import com.akuleshov7.ktoml.TomlOutputConfig

fun tomlSerializer(generator: DataPackGenerator): Toml {
	return Toml(
		outputConfig = TomlOutputConfig(
			indentation = when (generator.datapack.configuration.prettyPrintIndent) {
				"\t" -> TomlIndentation.TAB
				"    " -> TomlIndentation.FOUR_SPACES
				"  " -> TomlIndentation.TWO_SPACES
				else -> TomlIndentation.NONE
			},
			ignoreNullValues = true,
		)
	)
}
