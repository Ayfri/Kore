package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import java.util.*
import kotlinx.serialization.Serializable

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

fun Components.profile(name: String, id: UUIDArgument? = null, properties: List<ProfileProperty>? = null) = apply {
	this[ComponentTypes.PROFILE] = ProfileComponent(name, id, properties)
}

fun Components.profile(name: String, id: UUID, properties: List<ProfileProperty>? = null) = apply {
	this[ComponentTypes.PROFILE] = ProfileComponent(name, UUIDArgument(id), properties)
}

fun Components.profile(name: String, id: UUIDArgument? = null, block: ProfileComponent.() -> Unit) = apply {
	this[ComponentTypes.PROFILE] = ProfileComponent(name, id).apply(block)
}

fun Components.profile(name: String, id: UUID, block: ProfileComponent.() -> Unit) = apply {
	this[ComponentTypes.PROFILE] = ProfileComponent(name, UUIDArgument(id)).apply(block)
}

fun ProfileComponent.property(name: String, value: String, signature: String? = null) = apply {
	properties = (properties ?: mutableListOf()) + ProfileProperty(name, value, signature)
}

fun ProfileComponent.property(name: String, value: ByteArray, signature: String? = null) = apply {
	property(name, String(value), signature)
}
