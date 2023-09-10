package io.github.ayfri.kore.arguments.types.resources

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.DamageTypeOrTagArgument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface DamageTypeArgument : ResourceLocationArgument, DamageTypeOrTagArgument {
	companion object {
		operator fun invoke(damageType: String, namespace: String = "minecraft") = object : DamageTypeArgument {
			override val name = damageType
			override val namespace = namespace
		}
	}
}
