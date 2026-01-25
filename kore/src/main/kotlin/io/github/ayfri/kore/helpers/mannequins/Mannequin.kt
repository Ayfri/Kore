package io.github.ayfri.kore.helpers.mannequins

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.components.item.ProfileComponent
import io.github.ayfri.kore.generated.arguments.types.EntityTypeArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
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
 * @property description The description of the mannequin.
 * @property hiddenLayers The layers to hide.
 * @property hideDescription Whether to hide the description.
 * @property immovable Whether the mannequin is immovable.
 * @property mainHand The main hand of the mannequin.
 * @property pose The pose of the mannequin.
 * @property profile The profile of the mannequin.
 */
@Serializable
data class Mannequin(
	var description: ChatComponents? = null,
	@SerialName("hidden_layers")
	var hiddenLayers: List<MannequinLayer>? = null,
	@SerialName("hide_description")
	var hideDescription: Boolean? = null,
	var immovable: Boolean? = null,
	@SerialName("main_hand")
	var mainHand: MannequinHand? = null,
	var pose: MannequinPose? = null,
	var profile: ProfileComponent? = null,
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
 * Represents the pose of a mannequin.
 */
@Serializable(with = MannequinPose.Companion.MannequinPoseSerializer::class)
enum class MannequinPose {
	STANDING,
	CROUCHING,
	SWIMMING,
	FALL_FLYING,
	SLEEPING;

	companion object {
		data object MannequinPoseSerializer : LowercaseSerializer<MannequinPose>(entries)
	}
}
