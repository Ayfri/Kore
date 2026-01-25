package io.github.ayfri.kore.helpers.mannequins

import io.github.ayfri.kore.arguments.components.item.ProfileProperty
import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.generated.arguments.types.EntityTypeArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import io.github.ayfri.kore.utils.snbtSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.benwoodworth.knbt.nbtCompound

/**
 * Represents a Mannequin entity helper.
 *
 * JSON format reference: https://minecraft.wiki/w/Mannequin
 * Docs: https://kore.ayfri.com/docs/helpers/mannequins
 *
 * @property hiddenLayers The layers to hide.
 * @property mainHand The main hand of the mannequin.
 * @property profile The profile of the mannequin.
 */
@Serializable
data class Mannequin(
	@SerialName("hidden_layers")
	var hiddenLayers: List<MannequinLayer>? = null,
	@SerialName("main_hand")
	var mainHand: MannequinHand? = null,
	var profile: MannequinProfile? = null,
) {
	/** The entity type of the mannequin. */
	@Transient
	val entityType = EntityTypeArgument("mannequin")

	/**
	 * Converts the mannequin to an NBT compound.
	 *
	 * @return The NBT compound.
	 */
	fun toNbt() = snbtSerializer.encodeToNbtTag(serializer(), this).nbtCompound
}

/**
 * Represents the hand of a mannequin.
 */
@Serializable(with = MannequinHand.Companion.MannequinHandSerializer::class)
enum class MannequinHand {
	/** Left hand. */
	LEFT,

	/** Right hand. */
	RIGHT;

	companion object {
		data object MannequinHandSerializer : LowercaseSerializer<MannequinHand>(entries)
	}
}

/**
 * Represents a layer of a mannequin that can be hidden.
 */
@Serializable(with = MannequinLayer.Companion.MannequinLayerSerializer::class)
enum class MannequinLayer {
	/** The cape layer. */
	CAPE,

	/** The hat layer. */
	HAT,

	/** The jacket layer. */
	JACKET,

	/** The left pants leg layer. */
	LEFT_PANTS_LEG,

	/** The left sleeve layer. */
	LEFT_SLEEVE,

	/** The right pants leg layer. */
	RIGHT_PANTS_LEG,

	/** The right sleeve layer. */
	RIGHT_SLEEVE;

	companion object {
		data object MannequinLayerSerializer : LowercaseSerializer<MannequinLayer>(entries)
	}
}

/**
 * Represents the model type of a mannequin.
 */
@Serializable(with = MannequinModel.Companion.MannequinModelSerializer::class)
enum class MannequinModel {
	/** Slim model (Alex-style). */
	SLIM,

	/** Wide model (Steve-style). */
	WIDE;

	companion object {
		data object MannequinModelSerializer : LowercaseSerializer<MannequinModel>(entries)
	}
}

/**
 * Represents the profile of a mannequin.
 */
@Serializable(with = MannequinProfile.Companion.MannequinProfileSerializer::class)
sealed class MannequinProfile {
	companion object {
		data object MannequinProfileSerializer : NamespacedPolymorphicSerializer<MannequinProfile>(
			MannequinProfile::class,
			skipOutputName = true
		)
	}
}

/**
 * Represents a standard Player Profile for a mannequin.
 *
 * @property id The UUID of the player.
 * @property name The name of the player.
 * @property properties The properties of the player.
 */
@Serializable
data class PlayerMannequinProfile(
	var id: UUIDArgument? = null,
	var name: String? = null,
	var properties: List<ProfileProperty>? = null,
) : MannequinProfile()

/**
 * Represents a texture-based profile for a mannequin.
 *
 * @property cape The texture for the cape.
 * @property elytra The texture for the elytra.
 * @property model The model type of the mannequin.
 * @property texture The main texture.
 */
@Serializable
data class TextureMannequinProfile(
	var cape: String? = null,
	var elytra: String? = null,
	var model: MannequinModel? = null,
	var texture: String,
) : MannequinProfile()
