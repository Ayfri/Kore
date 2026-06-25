package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.arguments.types.resources.ModelArgument
import io.github.ayfri.kore.generated.arguments.types.AtlasArgument
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import io.github.ayfri.kore.utils.snbtSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.encodeToNbtTag

/**
 * A `minecraft:object` component that renders a 3D object inline in text. Use [AtlasObjectTextComponent] for a sprite,
 * or [PlayerObjectTextComponent] for a player head/model.
 *
 * Docs: [Text component format - Object](https://minecraft.wiki/w/Text_component_format#Object)
 */
@Serializable(with = ObjectTextComponentSerializer::class)
sealed class ObjectTextComponent : ChatComponent(), SimpleComponent {
	final override val type = ChatComponentType.OBJECT

	override fun toNbtTag() = nbt {
		super.toNbtTag().entries.forEach { (key, value) -> if (key != "text") this[key] = value }
		this["object"] = ObjectTextComponentSerializer.getContentName(this@ObjectTextComponent)
	}
}

data object ObjectTextComponentSerializer : NamespacedPolymorphicSerializer<ObjectTextComponent>(
	kClass = ObjectTextComponent::class,
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

/** Creates an [AtlasObjectTextComponent] for [sprite], optionally scoped to an [atlas]. */
fun objectComponent(
	sprite: ModelArgument,
	atlas: AtlasArgument? = null,
	block: AtlasObjectTextComponent.() -> Unit = {},
) = ChatComponents(AtlasObjectTextComponent(atlas, sprite).apply(block))

/** Creates a [PlayerObjectTextComponent] from a [PlayerProfile]. */
fun playerObjectComponent(
	player: PlayerProfile,
	hat: Boolean? = null,
	block: PlayerObjectTextComponent.() -> Unit = {},
) = ChatComponents(PlayerObjectTextComponent(player, hat).apply(block))

/** Creates a [PlayerObjectTextComponent] from a player [playerName]. */
fun playerObjectComponent(
	playerName: String,
	hat: Boolean? = null,
	block: PlayerObjectTextComponent.() -> Unit = {},
) = playerObjectComponent(PlayerProfile(name = playerName), hat, block)

/** Creates a [PlayerObjectTextComponent] from a player [playerId] UUID. */
fun playerObjectComponent(
	playerId: UUIDArgument,
	hat: Boolean? = null,
	block: PlayerObjectTextComponent.() -> Unit = {},
) = playerObjectComponent(PlayerProfile(id = playerId), hat, block)
