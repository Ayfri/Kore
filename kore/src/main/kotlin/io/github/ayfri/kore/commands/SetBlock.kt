package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
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

fun Function.setBlock(pos: Vec3, block: BlockArgument, mode: SetBlockMode? = null, strict: Boolean = false) =
	addLine(command("setblock", pos, block, literal(mode?.asArg()), literal(if (strict) "strict" else null)))
