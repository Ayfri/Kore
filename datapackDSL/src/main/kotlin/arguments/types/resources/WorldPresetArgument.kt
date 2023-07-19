package arguments.types.resources

import arguments.types.ResourceLocationArgument

interface WorldPresetArgument : ResourceLocationArgument {
	companion object {
		operator fun invoke(name: String, namespace: String = "minecraft") = object : WorldPresetArgument {
			override val name = name
			override val namespace = namespace
		}
	}
}
