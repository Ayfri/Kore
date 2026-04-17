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
 * DSL builder for the `clone` command.
 *
 * Grammar (see [Minecraft wiki](https://minecraft.wiki/w/Commands/clone)):
 * ```
 * clone [from <sourceDimension>] <begin> <end> [to <targetDimension>] <destination>
 *       [strict] [filtered <filter>|masked|replace] [force|move|normal]
 * ```
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

	/** Clones only blocks matching [filter]. */
	fun filter(filter: BlockOrTagArgument, mode: CloneMode? = null) {
		type = Type.FILTERED
		this.filter = filter
		cloneMode = mode
	}

	/** Clones every non-air block. */
	fun masked(mode: CloneMode? = null) {
		type = Type.MASKED
		cloneMode = mode
	}

	/** Overwrites every block in the destination, including air. */
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

/** Copies the region [[begin], [end]] to [destination]. */
fun Function.clone(begin: Vec3, end: Vec3, destination: Vec3, strict: Boolean = false) =
	addLine(command("clone", begin, end, destination, literal(if (strict) "strict" else null)))

/** Copies the region [[begin], [end]] to [destination] keeping only blocks matching [filter]. */
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

/** Copies the region [[begin], [end]] to [destination] skipping air blocks. */
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

/** Copies the region [[begin], [end]] to [destination] overwriting every block. */
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

/** Opens the [Clone] DSL for a cross-dimensional or flag-rich clone. */
fun Function.clone(block: Clone.() -> Unit) = Clone(this).apply(block).build()
