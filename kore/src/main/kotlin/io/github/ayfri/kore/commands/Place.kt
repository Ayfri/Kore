package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.literals.RotationArgument
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.worldgen.ConfiguredFeatureArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.ConfiguredStructureArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.StructureArgument
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.worldgen.types.TemplatePoolArgument
import io.github.ayfri.kore.utils.asArg

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

fun Function.placeTemplate(
	template: StructureArgument,
	pos: Vec3? = null,
	rotation: RotationArgument? = null,
	mirror: Boolean? = null,
	seed: Long? = null,
	strict: Boolean = false,
) = addLine(command("place", literal("template"), template, pos, rotation, literal(mirror?.asArg()), int(seed), literal(if (strict) "strict" else null)))
