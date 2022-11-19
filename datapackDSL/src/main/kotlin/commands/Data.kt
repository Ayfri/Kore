package commands

import arguments.Argument
import arguments.float
import arguments.int
import arguments.literal
import functions.Function

object DataModifyOperation {
	fun append(from: Argument.Data, path: String) = listOf(literal("append"), literal("from"), literal(from.literalName), from, literal(path))
	fun append(value: Argument) = listOf(literal("append"), literal("value"), value)
	
	fun insert(index: Int, from: Argument.Data, path: String) = listOf(literal("insert"), int(index), literal("from"), literal(from.literalName), from, literal(path))
	fun insert(index: Int, value: Argument) = listOf(literal("insert"), int(index), literal("value"), value)
	
	fun merge(from: Argument.Data, path: String) = listOf(literal("merge"), literal("from"), literal(from.literalName), from, literal(path))
	fun merge(value: Argument) = listOf(literal("merge"), literal("value"), value)
	
	fun prepend(from: Argument.Data, path: String) = listOf(literal("prepend"), literal("from"), literal(from.literalName), from, literal(path))
	fun prepend(value: Argument) = listOf(literal("prepend"), literal("value"), value)
	
	fun set(from: Argument.Data, path: String) = listOf(literal("set"), literal("from"), literal(from.literalName), from, literal(path))
	fun set(value: Argument) = listOf(literal("set"), literal("value"), value)
	
	fun remove(path: String) = listOf(literal("remove"), literal(path))
}

class Data(private val fn: Function, val target: Argument.Data) {
	fun get(path: String, scale: Double? = null) = fn.addLine(
		command(
			"data", literal("get"), literal(target.literalName), target, literal(path), float(scale)
		)
	)
	
	fun modify(path: String, value: DataModifyOperation.() -> List<Argument>) = fn.addLine(
		command(
			"data", literal("modify"), literal(target.literalName), target, literal(path), *DataModifyOperation.value().toTypedArray()
		)
	)
	
	fun merge(from: Argument.Data, path: String) = fn.addLine(
		command(
			"data", literal("merge"), literal(target.literalName), target, literal("from"), literal(from.literalName), from, literal(path)
		)
	)
	
	fun remove(path: String) = fn.addLine(
		command(
			"data", literal("remove"), literal(target.literalName), target, literal(path)
		)
	)
}

fun Function.data(target: Argument.Data, block: Data.() -> Command) = Data(this, target).block()
