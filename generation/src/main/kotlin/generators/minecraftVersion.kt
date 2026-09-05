package generators

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import generateFile
import logGenerated
import minecraftVersion

fun writeMinecraftVersion() {
	val file = generateFile("minecraftVersion") {
		addProperty(
			PropertySpec
				.builder("MINECRAFT_VERSION", String::class)
				.initializer("\"$minecraftVersion\"")
				.addModifiers(KModifier.CONST)
				.build()
		)
	}
	logGenerated("file", "minecraftVersion", file = file)
}
