package generators

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import generateFile
import minecraftVersion

fun writeMinecraftVersion() {
	generateFile("minecraftVersion") {
		addProperty(
			PropertySpec
				.builder("MINECRAFT_VERSION", String::class)
				.initializer("\"$minecraftVersion\"")
				.addModifiers(KModifier.CONST)
				.build()
		)
	}
}
