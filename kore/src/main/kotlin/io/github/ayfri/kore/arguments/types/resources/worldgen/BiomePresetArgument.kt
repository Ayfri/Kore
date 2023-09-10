package io.github.ayfri.kore.arguments.types.resources.worldgen

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface BiomePresetArgument : ResourceLocationArgument {
	companion object {
		operator fun invoke(preset: String, namespace: String = "minecraft") = object : BiomePresetArgument {
			override val name = preset
			override val namespace = namespace
		}
	}
}
