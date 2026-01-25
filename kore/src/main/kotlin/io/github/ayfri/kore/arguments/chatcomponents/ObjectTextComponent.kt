package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.arguments.types.resources.ModelArgument
import io.github.ayfri.kore.generated.arguments.types.AtlasArgument
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import io.github.ayfri.kore.utils.set
import io.github.ayfri.kore.utils.snbtSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.encodeToNbtTag

@Serializable(with = ObjectTextComponentSerializer::class)
sealed class ObjectTextComponent : ChatComponent(), SimpleComponent {
	final override val type = ChatComponentType.OBJECT

	override fun toNbtTag() = buildNbtCompound {
		super.toNbtTag().entries.forEach { (key, value) -> if (key != "text") this[key] = value }
		this["object"] = ObjectTextComponentSerializer.getContentName(this@ObjectTextComponent)
	}
}

data object ObjectTextComponentSerializer : NamespacedPolymorphicSerializer<ObjectTextComponent>(
	kClass = ObjectTextComponent::class,
	outputName = "object",
	useMinecraftPrefix = false
)

@Serializable
@SerialName("atlas")
data class AtlasObjectTextComponent(
	var atlas: AtlasArgument? = null,
	var sprite: ModelArgument,
) : ObjectTextComponent() {
	override fun toNbtTag() = buildNbtCompound {
		super.toNbtTag().entries.forEach { (key, value) -> this[key] = value }
		atlas?.let { this["atlas"] = it.asId() }
		this["sprite"] = sprite.asId()
	}
}

@Serializable
@SerialName("player")
data class PlayerObjectTextComponent(
	var player: PlayerProfile,
	var hat: Boolean? = null,
) : ObjectTextComponent() {
	override fun toNbtTag() = buildNbtCompound {
		super.toNbtTag().entries.forEach { (key, value) -> this[key] = value }
		this["player"] = snbtSerializer.encodeToNbtTag(player)
		hat?.let { this["hat"] = it }
	}
}

fun PlayerObjectTextComponent.player(block: PlayerProfile.() -> Unit) = apply { player.block() }

@Serializable
data class PlayerProfile(
	var id: UUIDArgument? = null,
	var name: String? = null,
	var properties: List<PlayerProperty>? = null,
)

fun PlayerProfile.property(name: String, value: String, signature: String? = null) {
	properties = (properties ?: mutableListOf()) + PlayerProperty(name, value, signature)
}

@Serializable
data class PlayerProperty(
	var name: String,
	var value: String,
	var signature: String? = null,
)

fun objectComponent(
	sprite: ModelArgument,
	atlas: AtlasArgument? = null,
	block: AtlasObjectTextComponent.() -> Unit = {},
) = ChatComponents(AtlasObjectTextComponent(atlas, sprite).apply(block))

fun playerObjectComponent(
	player: PlayerProfile,
	hat: Boolean? = null,
	block: PlayerObjectTextComponent.() -> Unit = {},
) = ChatComponents(PlayerObjectTextComponent(player, hat).apply(block))

fun playerObjectComponent(
	playerName: String,
	hat: Boolean? = null,
	block: PlayerObjectTextComponent.() -> Unit = {},
) = playerObjectComponent(PlayerProfile(name = playerName), hat, block)

fun playerObjectComponent(
	playerId: UUIDArgument,
	hat: Boolean? = null,
	block: PlayerObjectTextComponent.() -> Unit = {},
) = playerObjectComponent(PlayerProfile(id = playerId), hat, block)
