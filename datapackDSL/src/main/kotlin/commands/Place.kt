package commands

import arguments.maths.Vec3
import arguments.types.literals.RotationArgument
import arguments.types.literals.int
import arguments.types.literals.literal
import arguments.types.resources.worldgen.ConfiguredFeatureArgument
import arguments.types.resources.worldgen.ConfiguredStructureArgument
import arguments.types.resources.worldgen.StructureArgument
import arguments.types.resources.worldgen.TemplatePoolArgument
import functions.Function
import utils.asArg

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
) = addLine(command("place", literal("template"), template, pos, rotation, literal(mirror?.asArg()), int(seed)))
