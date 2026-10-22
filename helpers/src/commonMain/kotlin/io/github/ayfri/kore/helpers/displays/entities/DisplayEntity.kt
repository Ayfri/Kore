package io.github.ayfri.kore.helpers.displays.entities

import io.github.ayfri.kore.arguments.colors.RGB
import io.github.ayfri.kore.generated.arguments.types.EntityTypeArgument
import io.github.ayfri.kore.helpers.displays.maths.Transformation
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.JsonSerialName
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import io.github.ayfri.kore.utils.encodeToNbtTag
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.benwoodworth.knbt.nbtCompound

@Serializable(with = BillboardMode.Companion.BillboardModeSerializer::class)
enum class BillboardMode {
	FIXED,
	VERTICAL,
	HORIZONTAL,
	CENTER;

	companion object {
		data object BillboardModeSerializer : LowercaseSerializer<BillboardMode>(entries)
	}
}

@Serializable
data class Brightness(
	var block: Int? = null,
	var sky: Int? = null,
)

@GeneratedSealedSerializer
@Serializable
sealed class DisplayEntity {
	@JsonSerialName("billboard")
	var billboardMode: BillboardMode? = null
	var brightness: Brightness? = null

	@Serializable(with = RGB.Companion.ColorAsDecimalSerializer::class)
	var glowColorOverride: RGB? = null
	var height: Int? = null
	var interpolationDuration: Int? = null
	var shadowRadius: Float? = null
	var shadowStrength: Float? = null
	var startInterpolation: Int? = null
	var transformation: Transformation? = null
	var viewRange: Int? = null
	var width: Int? = null

	@Transient
	abstract val entityType: EntityTypeArgument

	fun toNbt() = encodeToNbtTag(DisplayEntitySerializer, this).nbtCompound

	companion object {
		data object DisplayEntitySerializer : NamespacedPolymorphicSerializer<DisplayEntity>(
			displayEntitySealedSerializer(),
			skipOutputName = true,
		)
	}
}
