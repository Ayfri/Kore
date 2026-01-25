package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.arguments.types.resources.ModelArgument
import io.github.ayfri.kore.arguments.types.resources.model
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.helpers.mannequins.MannequinModel
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ProfileProperty(
	var name: String,
	var value: String,
	var signature: String? = null,
)

@Serializable(with = ProfileComponent.Companion.ProfileComponentSerializer::class)
sealed class ProfileComponent : Component() {
	companion object {
		data object ProfileComponentSerializer : NamespacedPolymorphicSerializer<ProfileComponent>(
			ProfileComponent::class,
			skipOutputName = true,
		)
	}
}

@Serializable
@SerialName("player")
data class PlayerProfile(
	var name: String? = null,
	var id: UUIDArgument? = null,
	var properties: List<ProfileProperty>? = null,
) : ProfileComponent()

@Serializable
@SerialName("texture")
data class TextureProfile(
	var cape: ModelArgument? = null,
	var elytra: ModelArgument? = null,
	var model: MannequinModel? = null,
	var texture: ModelArgument? = null,
) : ProfileComponent()

fun ComponentsScope.playerProfile(
	name: String? = null,
	id: UUIDArgument? = null,
	properties: List<ProfileProperty>? = null,
) = apply {
	this[ItemComponentTypes.PROFILE] = PlayerProfile(name, id, properties)
}

fun ComponentsScope.playerProfile(
	name: String? = null,
	id: UUID,
	properties: List<ProfileProperty>? = null,
) = playerProfile(name, UUIDArgument(id), properties)

fun ComponentsScope.playerProfile(name: String? = null, id: UUIDArgument? = null, block: PlayerProfile.() -> Unit) = apply {
	this[ItemComponentTypes.PROFILE] = PlayerProfile(name, id).apply(block)
}

fun ComponentsScope.playerProfile(name: String? = null, id: UUID, block: PlayerProfile.() -> Unit) =
	playerProfile(name, UUIDArgument(id), block)

fun PlayerProfile.property(name: String, value: String, signature: String? = null) = apply {
	properties = (properties ?: mutableListOf()) + ProfileProperty(name, value, signature)
}

fun PlayerProfile.property(name: String, value: ByteArray, signature: String? = null) = apply {
	property(name, String(value), signature)
}

fun ComponentsScope.textureProfile(
	cape: ModelArgument? = null,
	elytra: ModelArgument? = null,
	model: MannequinModel? = null,
	texture: ModelArgument? = null,
) = apply {
	this[ItemComponentTypes.PROFILE] = TextureProfile(cape, elytra, model, texture)
}

fun ComponentsScope.textureProfile(
	cape: String? = null,
	elytra: String? = null,
	model: MannequinModel? = null,
	texture: String,
) = textureProfile(
	cape = cape?.let { model(it) },
	elytra = elytra?.let { model(it) },
	model = model,
	texture = model(texture),
)

fun ComponentsScope.textureProfile(block: TextureProfile.() -> Unit) = apply {
	this[ItemComponentTypes.PROFILE] = TextureProfile().apply(block)
}
