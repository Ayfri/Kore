package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.Serializable

/** Behavior of the `fill` command on existing blocks in the region. */
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

/**
 * Fills the cuboid region between [from] and [to] with [block].
 *
 * See the [Minecraft wiki](https://minecraft.wiki/w/Commands/fill).
 *
 * @param fillOption Optional mode controlling how existing blocks are treated.
 * @param strict When true, the command fails hard instead of silently skipping invalid placements.
 */
fun Function.fill(from: Vec3, to: Vec3, block: BlockArgument, fillOption: FillOption? = null, strict: Boolean = false) =
	addLine(command("fill", from, to, block, literal(fillOption?.asArg()), literal(if (strict) "strict" else null)))

/**
 * Fills the cuboid region [[from], [to]] with [block], only replacing blocks matching [filter].
 *
 * @param fillOption Optional `destroy`, `hollow` or `outline` post-processing mode.
 * @param strict When true, fails hard on invalid block states.
 */
fun Function.fill(
	from: Vec3,
	to: Vec3,
	block: BlockArgument,
	filter: BlockArgument,
	fillOption: FillOption? = null,
	strict: Boolean = false,
) = addLine(
	command(
		"fill",
		from,
		to,
		block,
		literal("replace"),
		filter,
		literal(fillOption?.asArg()),
		literal(if (strict) "strict" else null)
	)
)
