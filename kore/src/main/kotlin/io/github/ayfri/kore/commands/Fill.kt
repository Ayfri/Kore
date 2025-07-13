package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
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

fun Function.fill(from: Vec3, to: Vec3, block: BlockArgument, fillOption: FillOption? = null, strict: Boolean = false) =
	addLine(command("fill", from, to, block, literal(fillOption?.asArg()), literal(if (strict) "strict" else null)))

fun Function.fill(from: Vec3, to: Vec3, block: BlockArgument, filter: BlockArgument, strict: Boolean = false) =
	addLine(command("fill", from, to, block, literal("replace"), filter, literal(if (strict) "strict" else null)))
