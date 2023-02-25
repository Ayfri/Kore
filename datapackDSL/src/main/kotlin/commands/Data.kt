package commands

import arguments.Argument
import arguments.float
import arguments.int
import arguments.literal
import functions.Function
import kotlinx.serialization.encodeToString
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.StringifiedNbt

object DataModifyOperation {
	fun append(from: Argument.Data, path: String) = listOf(literal("append"), literal("from"), literal(from.literalName), from, literal(path))
	fun append(value: NbtTag) = listOf(literal("append"), literal("value"), literal(StringifiedNbt.encodeToString(value)))

	fun insert(index: Int, from: Argument.Data, path: String) =
		listOf(literal("insert"), int(index), literal("from"), literal(from.literalName), from, literal(path))

	fun insert(index: Int, value: NbtTag) = listOf(literal("insert"), int(index), literal("value"), literal(StringifiedNbt.encodeToString(value)))

	fun merge(from: Argument.Data, path: String) = listOf(literal("merge"), literal("from"), literal(from.literalName), from, literal(path))
	fun merge(value: NbtTag) = listOf(literal("merge"), literal("value"), literal(StringifiedNbt.encodeToString(value)))

	fun prepend(from: Argument.Data, path: String) = listOf(literal("prepend"), literal("from"), literal(from.literalName), from, literal(path))
	fun prepend(value: NbtTag) = listOf(literal("prepend"), literal("value"), literal(StringifiedNbt.encodeToString(value)))

	fun set(from: Argument.Data, path: String) = listOf(literal("set"), literal("from"), literal(from.literalName), from, literal(path))
	fun set(value: NbtTag) = listOf(literal("set"), literal("value"), literal(StringifiedNbt.encodeToString(value)))

	fun remove(path: String) = listOf(literal("remove"), literal(path))
}

class Data(private val fn: Function, val target: Argument.Data) {
	fun get(path: String? = null, scale: Double? = null) = fn.addLine(
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
fun Function.data(target: Argument.Data) = Data(this, target)
