package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.arguments.types.resources.ModelArgument
import io.github.ayfri.kore.arguments.types.resources.model
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

/** A skin texture property (name, base64-encoded value, and optional signature). */
@Serializable
data class ProfileProperty(
	var name: String,
	var value: String,
	var signature: String? = null,
)

/**
 * Represents the `minecraft:profile` item component, which sets the player skin displayed on a player head item.
 *
 * Can be a [PlayerProfile] (name/UUID lookup) or a [TextureProfile] (direct texture model).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#profile
 */
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

/** Sets the player skin displayed on a player head item using a player name or UUID lookup. */
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

/** Sets the player skin displayed on a player head item using direct texture/model references. */
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
