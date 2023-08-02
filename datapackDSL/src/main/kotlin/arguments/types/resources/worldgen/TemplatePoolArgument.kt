package arguments.types.resources.worldgen

import arguments.types.ResourceLocationArgument

interface TemplatePoolArgument : ResourceLocationArgument {
	companion object {
		operator fun invoke(name: String, namespace: String = "minecraft") = object : TemplatePoolArgument {
			override val name = name
			override val namespace = namespace
		}
	}
}
