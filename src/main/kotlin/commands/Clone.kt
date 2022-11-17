package commands

import functions.Function
import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(CloneMode.Companion.CloneModeSerializer::class)
enum class CloneMode {
	MASKED,
	NOT,
	ONLY;
	
	companion object {
		val values = values()
		
		object CloneModeSerializer : LowercaseSerializer<CloneMode>(values)
	}
}

fun Function.clone(begin: Argument.Coordinate, end: Argument.Coordinate, destination: Argument.Coordinate) = addLine(command("clone", begin, end, destination))
fun Function.cloneFiltered(begin: Argument.Coordinate, end: Argument.Coordinate, destination: Argument.Coordinate, filter: Argument.BlockOrTag, mode: CloneMode) =
	addLine(command("clone", begin, end, destination, literal("filtered"), filter, literal(mode.asArg())))

fun Function.cloneMasked(begin: Argument.Coordinate, end: Argument.Coordinate, destination: Argument.Coordinate, mode: CloneMode) =
	addLine(command("clone", begin, end, destination, literal("masked"), literal(mode.asArg())))

fun Function.cloneReplace(begin: Argument.Coordinate, end: Argument.Coordinate, destination: Argument.Coordinate, mode: CloneMode) =
	addLine(command("clone", begin, end, destination, literal("replace"), literal(mode.asArg())))
