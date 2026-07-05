package io.github.ayfri.kore

import io.github.ayfri.kore.generation.DataPackGenerationOptions
import io.github.ayfri.kore.generation.DataPackGenerator
import io.github.ayfri.kore.generation.DataPackJarGenerationOptions
import io.github.ayfri.kore.generation.DatapackGenerationMode

/** Generates the datapack as raw files. */
fun DataPack.generate(init: DataPackGenerationOptions.() -> Unit = {}) {
	val options = DataPackGenerationOptions().apply(init)
	val datapackGenerator = DataPackGenerator(this, options)
	datapackGenerator.generate()
}

/** Generates the datapack as a jar file, must be used as a mod. */
fun DataPack.generateJar(init: DataPackJarGenerationOptions.() -> Unit = {}) {
	val options = DataPackJarGenerationOptions(this).apply(init)
	val datapackGenerator = DataPackGenerator(this, options, DatapackGenerationMode.JAR)
	datapackGenerator.generate()
}

/** Generates the datapack as a zip file for easy distribution and faster loading. */
fun DataPack.generateZip(init: DataPackGenerationOptions.() -> Unit = {}) {
	val options = DataPackGenerationOptions().apply(init)
	val datapackGenerator = DataPackGenerator(this, options, DatapackGenerationMode.ZIP)
	datapackGenerator.generate()
}
