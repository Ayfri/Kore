package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.CapeTextureArgument
import io.github.ayfri.kore.generated.arguments.types.ElytraTextureArgument
import io.github.ayfri.kore.generated.arguments.types.PlayerSkinTextureArgument
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import io.github.ayfri.kore.serializers.UUIDAsIntArraySerializer
import io.github.ayfri.kore.serializers.splitNamespacedId
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

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
 * Can be a [PlayerProfile] (name/UUID lookup, with optional texture overrides) or a [TextureProfile] (direct textures).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#profile
 */
@GeneratedSealedSerializer
@Serializable(with = ProfileComponent.Companion.ProfileComponentSerializer::class)
sealed class ProfileComponent : Component() {
	companion object {
		data object ProfileComponentSerializer : NamespacedPolymorphicSerializer<ProfileComponent>(
			profileComponentSealedSerializer(),
			skipOutputName = true,
			suffix = "Profile",
		)
	}
}

/**
 * A profile looked up by [name] or [id], or built from [properties], that can also override the looked-up skin with
 * [texture], [cape], [elytra] and [model].
 *
 * ```
 * playerProfile("Ayfri") { model = MannequinModel.SLIM } // profile={model:"slim",name:"Ayfri"}
 * ```
 */
@Serializable
data class PlayerProfile(
	var cape: CapeTextureArgument? = null,
	var elytra: ElytraTextureArgument? = null,
	@Serializable(with = UUIDAsIntArraySerializer::class) var id: UUIDArgument? = null,
	var model: MannequinModel? = null,
	var name: String? = null,
	var properties: List<ProfileProperty>? = null,
	var texture: PlayerSkinTextureArgument? = null,
) : ProfileComponent()

@Serializable
data class TextureProfile(
	var cape: CapeTextureArgument? = null,
	var elytra: ElytraTextureArgument? = null,
	var model: MannequinModel? = null,
	var texture: PlayerSkinTextureArgument? = null,
) : ProfileComponent()

/** Sets the player skin displayed on a player head item using a player name or UUID lookup. */
fun ComponentsScope.playerProfile(
	name: String? = null,
	id: UUIDArgument? = null,
	properties: List<ProfileProperty>? = null,
) = apply {
	this[ItemComponentTypes.PROFILE] = PlayerProfile(id = id, name = name, properties = properties)
}

fun ComponentsScope.playerProfile(
	name: String? = null,
	id: Uuid,
	properties: List<ProfileProperty>? = null,
) = playerProfile(name, UUIDArgument(id), properties)

fun ComponentsScope.playerProfile(name: String? = null, id: UUIDArgument? = null, block: PlayerProfile.() -> Unit) = apply {
	this[ItemComponentTypes.PROFILE] = PlayerProfile(id = id, name = name).apply(block)
}

fun ComponentsScope.playerProfile(name: String? = null, id: Uuid, block: PlayerProfile.() -> Unit) =
	playerProfile(name, UUIDArgument(id), block)

fun PlayerProfile.property(name: String, value: String, signature: String? = null) = apply {
	properties = (properties ?: mutableListOf()) + ProfileProperty(name, value, signature)
}

fun PlayerProfile.property(name: String, value: ByteArray, signature: String? = null) = apply {
	property(name, value.decodeToString(), signature)
}

/**
 * Sets the player skin displayed on a player head item using direct texture references.
 *
 * ```
 * textureProfile(texture = Textures.Entity.Player.Slim.ALEX) // profile={texture:"minecraft:entity/player/slim/alex"}
 * ```
 */
fun ComponentsScope.textureProfile(
	cape: CapeTextureArgument? = null,
	elytra: ElytraTextureArgument? = null,
	model: MannequinModel? = null,
	texture: PlayerSkinTextureArgument? = null,
) = apply {
	this[ItemComponentTypes.PROFILE] = TextureProfile(cape, elytra, model, texture)
}

/**
 * Same as the typed overload, with textures given as resource locations, `namespace:path` or a bare path in `minecraft`.
 *
 * ```
 * textureProfile(texture = "mypack:skins/bob") // profile={texture:"mypack:skins/bob"}
 * ```
 */
fun ComponentsScope.textureProfile(
	cape: String? = null,
	elytra: String? = null,
	model: MannequinModel? = null,
	texture: String,
) = textureProfile(
	cape = cape?.splitNamespacedId()?.let { (path, namespace) -> CapeTextureArgument(path, namespace) },
	elytra = elytra?.splitNamespacedId()?.let { (path, namespace) -> ElytraTextureArgument(path, namespace) },
	model = model,
	texture = texture.splitNamespacedId().let { (path, namespace) -> PlayerSkinTextureArgument(path, namespace) },
)

fun ComponentsScope.textureProfile(block: TextureProfile.() -> Unit) = apply {
	this[ItemComponentTypes.PROFILE] = TextureProfile().apply(block)
}
