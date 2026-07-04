package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.DimensionArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.Serializable

@Serializable(Type.Companion.TypeSerializer::class)
private enum class Type {
	FILTERED,
	MASKED,
	REPLACE;

	companion object {
		data object TypeSerializer : LowercaseSerializer<Type>(entries)
	}
}

/** Clone mode controlling how the source region interacts with the destination. */
@Serializable(CloneMode.Companion.CloneModeSerializer::class)
enum class CloneMode {
	FORCE,
	MOVE,
	NORMAL;

	companion object {
		data object CloneModeSerializer : LowercaseSerializer<CloneMode>(entries)
	}
}

/**
 * Builder for the `/clone` command.
 *
 * `/clone` copies a cuboid region into a destination area. The builder exposes the more complex
 * forms where the source or destination dimension can be specified and where the clone mode can be
 * refined with `filtered`, `masked`, or `replace`.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/clone)
 */
class Clone(private val fn: Function) {
	private var type: Type? = null
	private var cloneMode: CloneMode? = null
	private var filter: BlockOrTagArgument? = null
	var begin = vec3()
	var destination = vec3()
	var end = vec3()
	var from: DimensionArgument? = null

	/** When true, the command fails on invalid blocks instead of silently skipping them. */
	var strict: Boolean = false
	var to: DimensionArgument? = null

	private val fromArgs get() = from?.let { arrayOf(literal("from"), it) } ?: emptyArray()
	private val toArgs get() = to?.let { arrayOf(literal("to"), it) } ?: emptyArray()

	/**
	 * Clones only blocks matching [filter].
	 *
	 * This activates the `filtered` clone form and lets you optionally choose a
	 * [CloneMode] for the destination behavior.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/clone)
	 */
	fun filter(filter: BlockOrTagArgument, mode: CloneMode? = null) {
		type = Type.FILTERED
		this.filter = filter
		cloneMode = mode
	}

	/**
	 * Clones every non-air block.
	 *
	 * This maps to the `masked` clone form, which ignores air in the source region.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/clone)
	 */
	fun masked(mode: CloneMode? = null) {
		type = Type.MASKED
		cloneMode = mode
	}

	/**
	 * Overwrites every block in the destination, including air.
	 *
	 * This maps to the `replace` clone form, which is the most direct copy mode.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/clone)
	 */
	fun replace(mode: CloneMode? = null) {
		type = Type.REPLACE
		cloneMode = mode
	}

	internal fun build() = fn.addLine(
		command(
			"clone",
			*fromArgs,
			begin.truncate(),
			end.truncate(),
			*toArgs,
			destination.truncate(),
			literal(if (strict) "strict" else null),
			literal(type?.asArg()),
			filter,
			literal(cloneMode?.asArg())
		)
	)
}

/**
 * Copies the region [[begin], [end]] to [destination].
 *
 * This is the simplest `/clone` form and optionally enables `strict` block handling.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/clone)
 */
fun Function.clone(begin: Vec3, end: Vec3, destination: Vec3, strict: Boolean = false) =
	addLine(command("clone", begin, end, destination, literal(if (strict) "strict" else null)))

/**
 * Copies the region [[begin], [end]] to [destination] keeping only blocks matching [filter].
 *
 * This uses the `filtered` form and supports an optional [CloneMode] to control what
 * happens when the destination already contains blocks.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/clone)
 */
fun Function.cloneFiltered(begin: Vec3, end: Vec3, destination: Vec3, filter: BlockOrTagArgument, mode: CloneMode? = null, strict: Boolean = false) =
	addLine(
		command(
			"clone",
			begin.truncate(),
			end.truncate(),
			destination.truncate(),
			literal(if (strict) "strict" else null),
			literal("filtered"),
			filter,
			literal(mode?.asArg())
		)
	)

/**
 * Copies the region [[begin], [end]] to [destination] skipping air blocks.
 *
 * This uses the `masked` form, which preserves only non-air blocks from the source
 * region.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/clone)
 */
fun Function.cloneMasked(begin: Vec3, end: Vec3, destination: Vec3, mode: CloneMode? = null, strict: Boolean = false) =
	addLine(
		command(
			"clone",
			begin.truncate(),
			end.truncate(),
			destination.truncate(),
			literal(if (strict) "strict" else null),
			literal("masked"),
			literal(mode?.asArg())
		)
	)

/**
 * Copies the region [[begin], [end]] to [destination] overwriting every block.
 *
 * This uses the `replace` form, which copies the source region exactly into the
 * destination area.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/clone)
 */
fun Function.cloneReplace(begin: Vec3, end: Vec3, destination: Vec3, mode: CloneMode? = null, strict: Boolean = false) =
	addLine(
		command(
			"clone",
			begin.truncate(),
			end.truncate(),
			destination.truncate(),
			literal(if (strict) "strict" else null),
			literal("replace"),
			literal(mode?.asArg())
		)
	)

/**
 * Opens the [Clone] DSL for a cross-dimensional or flag-rich clone.
 *
 * This builder is useful when you need `from`/`to` dimensions, `strict` handling, or an explicit
 * clone mode in the same call.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/clone)
 */
fun Function.clone(block: Clone.() -> Unit) = Clone(this).apply(block).build()
