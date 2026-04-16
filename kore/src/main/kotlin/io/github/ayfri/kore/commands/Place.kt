package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.JigsawArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredStructureArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.TemplatePoolArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Places the configured [feature] at the executing position or at [pos]. */
fun Function.placeFeature(feature: ConfiguredFeatureArgument, pos: Vec3? = null) =
	addLine(command("place", literal("feature"), feature, pos))

/**
 * Starts jigsaw generation from the given template [pool] and [target] pool element.
 *
 * @param maxDepth Maximum jigsaw depth to generate.
 * @param pos Optional starting position (defaults to the executing position).
 */
fun Function.placeJigsaw(
	pool: TemplatePoolArgument,
	target: JigsawArgument,
	maxDepth: Int,
	pos: Vec3? = null,
) = addLine(command("place", literal("jigsaw"), pool, target, int(maxDepth), pos))

/**
 * Starts jigsaw generation using a raw [target] resource id.
 *
 * Prefer the [JigsawArgument] overload for type-safety.
 */
fun Function.placeJigsaw(
	pool: TemplatePoolArgument,
	target: String,
	maxDepth: Int,
	pos: Vec3? = null,
) = addLine(command("place", literal("jigsaw"), pool, literal(target), int(maxDepth), pos))

/** Places the configured [structure] at the executing position or at [pos]. */
fun Function.placeStructure(structure: ConfiguredStructureArgument, pos: Vec3? = null) =
	addLine(command("place", literal("structure"), structure, pos))

/** Rotation applied to a placed template. */
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

/** Mirror applied to a placed template. */
@Serializable(with = TemplateMirror.Companion.TemplateMirrorSerializer::class)
enum class TemplateMirror {
	NONE,
	FRONT_BACK,
	LEFT_RIGHT;

	companion object {
		data object TemplateMirrorSerializer : LowercaseSerializer<TemplateMirror>(entries)
	}
}

/**
 * Places the structure [template] with optional transformation parameters.
 *
 * Grammar: `place template <template> [<pos> [<rotation> [<mirror> [<integrity> [<seed> [strict]]]]]]`.
 *
 * @param integrity Fraction of blocks (0.0 – 1.0) that are actually placed; lower values create a ruined look.
 * @param seed Seed used to randomize which blocks are skipped when [integrity] is below 1.0.
 * @param strict When true, fails hard on invalid block states.
 */
fun Function.placeTemplate(
	template: StructureArgument,
	pos: Vec3? = null,
	rotation: TemplateRotation? = null,
	mirror: TemplateMirror? = null,
	integrity: Float? = null,
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
		float(integrity),
		int(seed),
		literal(if (strict) "strict" else null)
	)
)
