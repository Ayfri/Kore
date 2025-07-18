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

@Serializable(CloneMode.Companion.CloneModeSerializer::class)
enum class CloneMode {
	FORCE,
	MOVE,
	NORMAL;

	companion object {
		data object CloneModeSerializer : LowercaseSerializer<CloneMode>(entries)
	}
}

class Clone(private val fn: Function) {
	private var type: Type? = null
	private var cloneMode: CloneMode? = null
	private var filter: BlockOrTagArgument? = null
	var begin = vec3()
	var destination = vec3()
	var end = vec3()
	var from: DimensionArgument? = null
	var strict: Boolean = false
	var to: DimensionArgument? = null

	private val fromArgs get() = from?.let { arrayOf(literal("from"), it) } ?: emptyArray()
	private val toArgs get() = to?.let { arrayOf(literal("to"), it) } ?: emptyArray()

	fun filter(filter: BlockOrTagArgument, mode: CloneMode? = null) {
		type = Type.FILTERED
		this.filter = filter
		cloneMode = mode
	}

	fun masked(mode: CloneMode? = null) {
		type = Type.MASKED
		cloneMode = mode
	}

	fun replace(mode: CloneMode? = null) {
		type = Type.REPLACE
		cloneMode = mode
	}

	internal fun build() = fn.addLine(
		command(
			"clone",
			*fromArgs,
			begin,
			end,
			*toArgs,
			destination,
			literal(type?.asArg()),
			filter,
			literal(cloneMode?.asArg()),
			literal(if (strict) "strict" else null)
		)
	)
}

fun Function.clone(begin: Vec3, end: Vec3, destination: Vec3, strict: Boolean = false) = addLine(command("clone", begin, end, destination, literal(if (strict) "strict" else null)))
fun Function.cloneFiltered(begin: Vec3, end: Vec3, destination: Vec3, filter: BlockOrTagArgument, mode: CloneMode? = null, strict: Boolean = false) =
	addLine(command("clone", begin, end, destination, literal("filtered"), filter, literal(mode?.asArg()), literal(if (strict) "strict" else null)))

fun Function.cloneMasked(begin: Vec3, end: Vec3, destination: Vec3, mode: CloneMode? = null, strict: Boolean = false) =
	addLine(command("clone", begin, end, destination, literal("masked"), literal(mode?.asArg()), literal(if (strict) "strict" else null)))

fun Function.cloneReplace(begin: Vec3, end: Vec3, destination: Vec3, mode: CloneMode? = null, strict: Boolean = false) =
	addLine(command("clone", begin, end, destination, literal("replace"), literal(mode?.asArg()), literal(if (strict) "strict" else null)))

fun Function.clone(block: Clone.() -> Unit) = Clone(this).apply(block).build()
