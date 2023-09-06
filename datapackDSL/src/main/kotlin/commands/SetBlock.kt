package commands

import arguments.maths.Vec3
import arguments.types.literals.literal
import arguments.types.resources.BlockArgument
import functions.Function
import serializers.LowercaseSerializer
import utils.asArg
import kotlinx.serialization.Serializable

@Serializable(SetBlockMode.Companion.SetBlockModeSerializer::class)
enum class SetBlockMode {
	REPLACE,
	DESTROY,
	KEEP;

	companion object {
		data object SetBlockModeSerializer : LowercaseSerializer<SetBlockMode>(entries)
	}
}

fun Function.setBlock(pos: Vec3, block: BlockArgument, mode: SetBlockMode? = null) =
	addLine(command("setblock", pos, block, literal(mode?.asArg())))
