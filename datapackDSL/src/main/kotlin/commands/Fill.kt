package commands

import arguments.maths.Vec3
import arguments.types.literals.literal
import arguments.types.resources.BlockArgument
import functions.Function
import serializers.LowercaseSerializer
import utils.asArg
import kotlinx.serialization.Serializable

@Serializable(FillOption.Companion.FillOptionSerializer::class)
enum class FillOption {
	DESTROY,
	HOLLOW,
	KEEP,
	OUTLINE;

	companion object {
		data object FillOptionSerializer : LowercaseSerializer<FillOption>(entries)
	}
}

fun Function.fill(from: Vec3, to: Vec3, block: BlockArgument, fillOption: FillOption? = null) =
	addLine(command("fill", from, to, block, literal(fillOption?.asArg())))

fun Function.fill(from: Vec3, to: Vec3, block: BlockArgument, filter: BlockArgument) =
	addLine(command("fill", from, to, block, literal("replace"), filter))
