package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredStructureArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.TemplatePoolArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

fun Function.placeFeature(feature: ConfiguredFeatureArgument, pos: Vec3) =
	addLine(command("place", literal("feature"), feature, pos))

fun Function.placeJigsaw(
	jigsaw: TemplatePoolArgument,
	target: String,
	maxDepth: Int,
	pos: Vec3? = null,
) = addLine(command("place", literal("jigsaw"), jigsaw, literal(target), int(maxDepth), pos))

fun Function.placeStructure(structure: ConfiguredStructureArgument, pos: Vec3) =
	addLine(command("place", literal("structure"), structure, pos))

@Serializable(with = TemplateRotation.Companion.TemplateRotationSerializer::class)
enum class TemplateRotation {
	NONE,
	CLOCKWISE_90,

	@SerialName("180")
	CLOCKWISE_180,
	COUNTERCLOCKWISE_90
	;

	companion object {
		data object TemplateRotationSerializer : LowercaseSerializer<TemplateRotation>(entries)
	}
}

fun Function.placeTemplate(
	template: StructureArgument,
	pos: Vec3? = null,
	rotation: TemplateRotation? = null,
	mirror: Boolean? = null,
	seed: Long? = null,
	strict: Boolean = false,
) = addLine(
	command(
		"place",
		literal("template"),
		template,
		pos,
		literal(rotation?.asArg()),
		literal(mirror?.asArg()),
		int(seed),
		literal(if (strict) "strict" else null)
	)
)
