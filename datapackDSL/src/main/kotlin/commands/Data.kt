package commands

import arguments.*
import functions.Function
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.StringifiedNbt

object DataModifyOperation {
	fun append(from: Argument.Data, path: String) =
		listOf(literal("append"), literal("from"), literal(from.literalName), from, literal(path))

	fun append(value: NbtTag) = listOf(literal("append"), literal("value"), literal(StringifiedNbt.encodeToString(value)))
	fun append(value: Int) = listOf(literal("append"), literal("value"), int(value))
	fun append(value: Float) = listOf(literal("append"), literal("value"), float(value))
	fun append(value: String) = listOf(literal("append"), literal("value"), literal(value))
	fun append(value: Boolean) = listOf(literal("append"), literal("value"), bool(value))
	inline fun <reified T : Any> append(value: @Serializable T) =
		listOf(literal("append"), literal("value"), literal(StringifiedNbt.encodeToString(value)))

	fun insert(index: Int, from: Argument.Data, path: String) =
		listOf(literal("insert"), int(index), literal("from"), literal(from.literalName), from, literal(path))

	fun insert(index: Int, value: NbtTag) =
		listOf(literal("insert"), int(index), literal("value"), literal(StringifiedNbt.encodeToString(value)))

	fun insert(index: Int, value: Int) = listOf(literal("insert"), int(index), literal("value"), int(value))
	fun insert(index: Int, value: Float) = listOf(literal("insert"), int(index), literal("value"), float(value))
	fun insert(index: Int, value: String) = listOf(literal("insert"), int(index), literal("value"), literal(value))
	fun insert(index: Int, value: Boolean) = listOf(literal("insert"), int(index), literal("value"), bool(value))
	inline fun <reified T : Any> insert(index: Int, value: @Serializable T) =
		listOf(literal("insert"), int(index), literal("value"), literal(StringifiedNbt.encodeToString(value)))

	fun merge(from: Argument.Data, path: String) = listOf(literal("merge"), literal("from"), literal(from.literalName), from, literal(path))
	fun merge(value: NbtTag) = listOf(literal("merge"), literal("value"), literal(StringifiedNbt.encodeToString(value)))
	fun merge(value: Int) = listOf(literal("merge"), literal("value"), int(value))
	fun merge(value: Float) = listOf(literal("merge"), literal("value"), float(value))
	fun merge(value: String) = listOf(literal("merge"), literal("value"), literal(value))
	fun merge(value: Boolean) = listOf(literal("merge"), literal("value"), bool(value))
	inline fun <reified T : Any> merge(value: @Serializable T) =
		listOf(literal("merge"), literal("value"), literal(StringifiedNbt.encodeToString(value)))

	fun prepend(from: Argument.Data, path: String) =
		listOf(literal("prepend"), literal("from"), literal(from.literalName), from, literal(path))

	fun prepend(value: NbtTag) = listOf(literal("prepend"), literal("value"), literal(StringifiedNbt.encodeToString(value)))
	fun prepend(value: Int) = listOf(literal("prepend"), literal("value"), int(value))
	fun prepend(value: Float) = listOf(literal("prepend"), literal("value"), float(value))
	fun prepend(value: String) = listOf(literal("prepend"), literal("value"), literal(value))
	fun prepend(value: Boolean) = listOf(literal("prepend"), literal("value"), bool(value))
	inline fun <reified T : Any> prepend(value: @Serializable T) =
		listOf(literal("prepend"), literal("value"), literal(StringifiedNbt.encodeToString(value)))

	fun set(from: Argument.Data, path: String) = listOf(literal("set"), literal("from"), literal(from.literalName), from, literal(path))
	fun set(string: Argument.Data, path: String, start: Int? = null, end: Int? = null) =
		listOfNotNull(literal("set"), literal("string"), literal(string.literalName), string, literal(path), int(start), int(end))

	fun set(value: NbtTag) = listOf(literal("set"), literal("value"), literal(StringifiedNbt.encodeToString(value)))
	fun set(value: Int) = listOf(literal("set"), literal("value"), int(value))
	fun set(value: Float) = listOf(literal("set"), literal("value"), float(value))
	fun set(value: String) = listOf(literal("set"), literal("value"), literal(value))
	fun set(value: Boolean) = listOf(literal("set"), literal("value"), bool(value))
	inline fun <reified T : Any> set(value: @Serializable T) =
		listOf(literal("set"), literal("value"), literal(StringifiedNbt.encodeToString(value)))
}

class Data(val fn: Function, val target: Argument.Data) {
	fun get(path: String? = null, scale: Double? = null) = fn.addLine(
		command(
			"data", literal("get"), literal(target.literalName), target, literal(path), float(scale)
		)
	)

	fun merge(data: NbtTag) = fn.addLine(
		command(
			"data", literal("merge"), literal(target.literalName), target, literal(StringifiedNbt.encodeToString(data))
		)
	)

	inline fun <reified T : Any> merge(data: @Serializable T) = fn.addLine(
		command(
			"data", literal("merge"), literal(target.literalName), target, literal(StringifiedNbt.encodeToString(data))
		)
	)

	fun modify(path: String, value: DataModifyOperation.() -> List<Argument>) = fn.addLine(
		command(
			"data", literal("modify"), literal(target.literalName), target, literal(path), *DataModifyOperation.value().toTypedArray()
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
