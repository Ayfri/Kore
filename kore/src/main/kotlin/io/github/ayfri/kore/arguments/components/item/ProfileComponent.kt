package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ProfileProperty(
	var name: String,
	var value: String,
	var signature: String? = null,
)

@Serializable(with = ProfileComponent.Companion.ProfileComponentSerializer::class)
data class ProfileComponent(
	var name: String,
	var id: UUIDArgument? = null,
	var properties: List<ProfileProperty>? = null,
) : Component() {
	companion object {
		data object ProfileComponentSerializer : SinglePropertySimplifierSerializer<ProfileComponent, String>(
			ProfileComponent::class,
			ProfileComponent::name
		)
	}
}

fun ComponentsScope.profile(name: String, id: UUIDArgument? = null, properties: List<ProfileProperty>? = null) = apply {
	this[ItemComponentTypes.PROFILE] = ProfileComponent(name, id, properties)
}

fun ComponentsScope.profile(name: String, id: UUID, properties: List<ProfileProperty>? = null) = apply {
	this[ItemComponentTypes.PROFILE] = ProfileComponent(name, UUIDArgument(id), properties)
}

fun ComponentsScope.profile(name: String, id: UUIDArgument? = null, block: ProfileComponent.() -> Unit) = apply {
	this[ItemComponentTypes.PROFILE] = ProfileComponent(name, id).apply(block)
}

fun ComponentsScope.profile(name: String, id: UUID, block: ProfileComponent.() -> Unit) = apply {
	this[ItemComponentTypes.PROFILE] = ProfileComponent(name, UUIDArgument(id)).apply(block)
}

fun ProfileComponent.property(name: String, value: String, signature: String? = null) = apply {
	properties = (properties ?: mutableListOf()) + ProfileProperty(name, value, signature)
}

fun ProfileComponent.property(name: String, value: ByteArray, signature: String? = null) = apply {
	property(name, String(value), signature)
}
