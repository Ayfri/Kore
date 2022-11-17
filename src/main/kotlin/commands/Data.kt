package commands

import arguments.Argument
import arguments.float
import arguments.int
import arguments.literal
import functions.Function

class DataModifyOperation(private val fn: Function) {
	fun append(from: Argument.Data, path: String) = listOf(fn.literal("append"), fn.literal("from"), fn.literal(from.literalName), from, fn.literal("path"), fn.literal(path))
	fun append(value: Argument) = listOf(fn.literal("append"), fn.literal("value"), value)
	
	fun insert(index: Int, from: Argument.Data, path: String) = listOf(fn.literal("insert"), fn.int(index), fn.literal("from"), fn.literal(from.literalName), from, fn.literal(path))
	fun insert(index: Int, value: Argument) = listOf(fn.literal("insert"), fn.int(index), fn.literal("value"), value)
	
	fun merge(from: Argument.Data, path: String) = listOf(fn.literal("merge"), fn.literal("from"), fn.literal(from.literalName), from, fn.literal("path"), fn.literal(path))
	fun merge(value: Argument) = listOf(fn.literal("merge"), fn.literal("value"), value)
	
	fun prepend(from: Argument.Data, path: String) = listOf(fn.literal("prepend"), fn.literal("from"), fn.literal(from.literalName), from, fn.literal("path"), fn.literal(path))
	fun prepend(value: Argument) = listOf(fn.literal("prepend"), fn.literal("value"), value)
	
	fun set(from: Argument.Data, path: String) = listOf(fn.literal("set"), fn.literal("from"), fn.literal(from.literalName), from, fn.literal("path"), fn.literal(path))
	fun set(value: Argument) = listOf(fn.literal("set"), fn.literal("value"), value)
	
	fun remove(path: String) = listOf(fn.literal("remove"), fn.literal(path))
}

class Data(private val fn: Function, val target: Argument.Data) {
	fun get(path: String, scale: Double? = null) = fn.addLine(
		command(
			"data", fn.literal("get"), fn.literal(target.literalName), target, fn.literal(path), fn.float(scale)
		)
	)
	
	fun modify(path: String, value: DataModifyOperation.() -> List<Argument>) = fn.addLine(
		command(
			"data", fn.literal("modify"), fn.literal(target.literalName), target, fn.literal(path), *DataModifyOperation(fn).value().toTypedArray()
		)
	)
	
	fun merge(from: Argument.Data, path: String) = fn.addLine(
		command(
			"data", fn.literal("merge"), fn.literal(target.literalName), target, fn.literal("from"), fn.literal(from.literalName), from, fn.literal(path)
		)
	)
	
	fun remove(path: String) = fn.addLine(
		command(
			"data", fn.literal("remove"), fn.literal(target.literalName), target, fn.literal(path)
		)
	)
}

fun Function.data(target: Argument.Data, block: Data.() -> Unit) = Data(this, target).apply(block)
