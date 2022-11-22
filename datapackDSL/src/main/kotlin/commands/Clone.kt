package commands

import arguments.Argument
import arguments.Coordinate
import arguments.literal
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

fun Function.clone(begin: Coordinate, end: Coordinate, destination: Coordinate) = addLine(command("clone", begin, end, destination))
fun Function.cloneFiltered(begin: Coordinate, end: Coordinate, destination: Coordinate, filter: Argument.BlockOrTag, mode: CloneMode) =
	addLine(command("clone", begin, end, destination, literal("filtered"), filter, literal(mode.asArg())))

fun Function.cloneMasked(begin: Coordinate, end: Coordinate, destination: Coordinate, mode: CloneMode) =
	addLine(command("clone", begin, end, destination, literal("masked"), literal(mode.asArg())))

fun Function.cloneReplace(begin: Coordinate, end: Coordinate, destination: Coordinate, mode: CloneMode) =
	addLine(command("clone", begin, end, destination, literal("replace"), literal(mode.asArg())))
