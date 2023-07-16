package arguments.types.resources

import arguments.Argument
import arguments.types.DataArgument
import arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface StorageArgument : DataArgument, ResourceLocationArgument {
	companion object {
		operator fun invoke(name: String, namespace: String = "minecraft") = object : StorageArgument {
			override val name = name
			override val namespace = namespace
		}
	}
}

fun storage(storage: String, namespace: String = "minecraft") = StorageArgument(storage, namespace)
