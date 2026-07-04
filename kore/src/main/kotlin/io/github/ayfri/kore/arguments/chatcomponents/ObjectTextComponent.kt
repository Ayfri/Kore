package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.arguments.types.resources.ModelArgument
import io.github.ayfri.kore.generated.arguments.types.AtlasArgument
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import io.github.ayfri.kore.utils.snbtSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.encodeToNbtTag

/**
 * A `minecraft:object` component that renders a 3D object inline in text. Use [AtlasObjectTextComponent] for a sprite,
 * or [PlayerObjectTextComponent] for a player head/model.
 *
 * The optional [fallback] component is used when the object cannot be displayed (e.g. when printing in server logs or during narration).
 *
 * Docs: [Text component format - Object](https://minecraft.wiki/w/Text_component_format#Object)
 */
@GeneratedSealedSerializer
@Serializable(with = ObjectTextComponentSerializer::class)
sealed class ObjectTextComponent : ChatComponent(), SimpleComponent {
	final override val type = ChatComponentType.OBJECT
	open var fallback: ChatComponents? = null

	override fun toNbtTag() = nbt {
		super.toNbtTag().entries.forEach { (key, value) -> if (key != "text") this[key] = value }
		this["object"] = ObjectTextComponentSerializer.getContentName(this@ObjectTextComponent)
		fallback?.let { this["fallback"] = it.toNbtTag() }
	}
}

@OptIn(InternalSerializationApi::class)
data object ObjectTextComponentSerializer : NamespacedPolymorphicSerializer<ObjectTextComponent>(
	objectTextComponentSealedSerializer(),
	outputName = "object",
	useMinecraftPrefix = false
)

/** An [ObjectTextComponent] that renders a sprite from a texture atlas. */
@Serializable
@SerialName("atlas")
data class AtlasObjectTextComponent(
	/** Atlas resource location to sample the sprite from. Defaults to the item atlas when `null`. */
	var atlas: AtlasArgument? = null,
	/** Model path of the sprite within the [atlas]. */
	var sprite: ModelArgument,
	/** Text component used when the object cannot be displayed (e.g. in server logs or during narration). */
	override var fallback: ChatComponents? = null,
) : ObjectTextComponent() {
	override fun toNbtTag() = nbt {
		super.toNbtTag().entries.forEach { (key, value) -> this[key] = value }
		atlas?.let { this["atlas"] = it.asId() }
		this["sprite"] = sprite.asId()
	}
}

/** An [ObjectTextComponent] that renders a player's skin as a head or full-body model. */
@Serializable
@SerialName("player")
data class PlayerObjectTextComponent(
	/** Profile used to resolve the player's skin textures. */
	var player: PlayerProfile,
	/** When `true`, renders the outer hat/overlay skin layer. */
	var hat: Boolean? = null,
	/** Text component used when the object cannot be displayed (e.g. in server logs or during narration). */
	override var fallback: ChatComponents? = null,
) : ObjectTextComponent() {
	override fun toNbtTag() = nbt {
		super.toNbtTag().entries.forEach { (key, value) -> this[key] = value }
		this["player"] = snbtSerializer.encodeToNbtTag(player)
		hat?.let { this["hat"] = it }
	}
}

/** Applies [block] to the [PlayerObjectTextComponent.player] profile. */
fun PlayerObjectTextComponent.player(block: PlayerProfile.() -> Unit) = apply { player.block() }

/** Identifies a player by optional [id] (UUID), [name], and optional skin [properties]. */
@Serializable
data class PlayerProfile(
	/** UUID of the player. Takes priority over [name] for skin resolution. */
	var id: UUIDArgument? = null,
	/** Player name used to look up the profile when [id] is absent. */
	var name: String? = null,
	/** Pre-fetched skin property list. When provided, no network lookup is performed. */
	var properties: List<PlayerProperty>? = null,
)

/** Appends a skin [property] with the given [name], [value], and optional [signature] to this profile. */
fun PlayerProfile.property(name: String, value: String, signature: String? = null) {
	properties = (properties ?: mutableListOf()) + PlayerProperty(name, value, signature)
}

/** A single Mojang-signed skin property (typically `"textures"`). */
@Serializable
data class PlayerProperty(
	/** Property name (e.g. `"textures"`). */
	var name: String,
	/** Base64-encoded property value. */
	var value: String,
	/** Optional Mojang RSA signature verifying [value]. */
	var signature: String? = null,
)

/** Creates an [AtlasObjectTextComponent] for [sprite], optionally scoped to an [atlas] with an optional [fallback] component. */
fun objectComponent(
	sprite: ModelArgument,
	atlas: AtlasArgument? = null,
	fallback: ChatComponents? = null,
	block: AtlasObjectTextComponent.() -> Unit = {},
) = ChatComponents(AtlasObjectTextComponent(atlas, sprite, fallback).apply(block))

/** Creates a [PlayerObjectTextComponent] from a [PlayerProfile] with an optional [fallback] component. */
fun playerObjectComponent(
	player: PlayerProfile,
	hat: Boolean? = null,
	fallback: ChatComponents? = null,
	block: PlayerObjectTextComponent.() -> Unit = {},
) = ChatComponents(PlayerObjectTextComponent(player, hat, fallback).apply(block))

/** Creates a [PlayerObjectTextComponent] from a player [playerName] with an optional [fallback] component. */
fun playerObjectComponent(
	playerName: String,
	hat: Boolean? = null,
	fallback: ChatComponents? = null,
	block: PlayerObjectTextComponent.() -> Unit = {},
) = playerObjectComponent(PlayerProfile(name = playerName), hat, fallback, block)

/** Creates a [PlayerObjectTextComponent] from a player [playerId] UUID with an optional [fallback] component. */
fun playerObjectComponent(
	playerId: UUIDArgument,
	hat: Boolean? = null,
	fallback: ChatComponents? = null,
	block: PlayerObjectTextComponent.() -> Unit = {},
) = playerObjectComponent(PlayerProfile(id = playerId), hat, fallback, block)
