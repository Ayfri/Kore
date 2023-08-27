package arguments.types.resources

import arguments.Argument
import arguments.types.DamageTypeOrTagArgument
import arguments.types.ResourceLocationArgument
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
