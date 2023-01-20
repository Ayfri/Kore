package commands

import arguments.Argument
import arguments.Vec3
import arguments.literal
import functions.Function
import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(CloneMode.Companion.CloneModeSerializer::class)
enum class CloneMode {
	FORCE,
	MOVE,
	NORMAL;

	companion object {
		val values = values()

		object CloneModeSerializer : LowercaseSerializer<CloneMode>(values)
	}
}

fun Function.clone(begin: Vec3, end: Vec3, destination: Vec3) = addLine(command("clone", begin, end, destination))
fun Function.cloneFiltered(begin: Vec3, end: Vec3, destination: Vec3, filter: Argument.BlockOrTag, mode: CloneMode) =
	addLine(command("clone", begin, end, destination, literal("filtered"), filter, literal(mode.asArg())))

fun Function.cloneMasked(begin: Vec3, end: Vec3, destination: Vec3, mode: CloneMode) =
	addLine(command("clone", begin, end, destination, literal("masked"), literal(mode.asArg())))

fun Function.cloneReplace(begin: Vec3, end: Vec3, destination: Vec3, mode: CloneMode) =
	addLine(command("clone", begin, end, destination, literal("replace"), literal(mode.asArg())))
