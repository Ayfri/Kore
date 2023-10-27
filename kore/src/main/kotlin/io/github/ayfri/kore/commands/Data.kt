package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.types.DataArgument
import io.github.ayfri.kore.arguments.types.literals.bool
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.utils.nbt
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.StringifiedNbt
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

object DataModifyOperation {
	fun append(from: DataArgument, path: String) =
		listOf(literal("append"), literal("from"), literal(from.literalName), from, literal(path))

	fun append(value: NbtTag) = listOf(literal("append"), literal("value"), literal(StringifiedNbt.encodeToString(value)))
	fun append(value: Int) = listOf(literal("append"), literal("value"), int(value))
	fun append(value: Float) = listOf(literal("append"), literal("value"), float(value))
	fun append(value: String) = listOf(literal("append"), literal("value"), literal(value))
	fun append(value: Boolean) = listOf(literal("append"), literal("value"), bool(value))
	inline fun <reified T : Any> append(value: @Serializable T) =
		listOf(literal("append"), literal("value"), literal(StringifiedNbt.encodeToString(value)))

	fun insert(index: Int, from: DataArgument, path: String) =
		listOf(literal("insert"), int(index), literal("from"), literal(from.literalName), from, literal(path))

	fun insert(index: Int, value: NbtTag) =
		listOf(literal("insert"), int(index), literal("value"), literal(StringifiedNbt.encodeToString(value)))

	fun insert(index: Int, value: Int) = listOf(literal("insert"), int(index), literal("value"), int(value))
	fun insert(index: Int, value: Float) = listOf(literal("insert"), int(index), literal("value"), float(value))
	fun insert(index: Int, value: String) = listOf(literal("insert"), int(index), literal("value"), literal(value))
	fun insert(index: Int, value: Boolean) = listOf(literal("insert"), int(index), literal("value"), bool(value))
	inline fun <reified T : Any> insert(index: Int, value: @Serializable T) =
		listOf(literal("insert"), int(index), literal("value"), literal(StringifiedNbt.encodeToString(value)))

	fun merge(from: DataArgument, path: String) =
		listOf(literal("merge"), literal("from"), literal(from.literalName), from, literal(path))

	fun merge(value: NbtTag) = listOf(literal("merge"), literal("value"), literal(StringifiedNbt.encodeToString(value)))
	fun merge(value: Int) = listOf(literal("merge"), literal("value"), int(value))
	fun merge(value: Float) = listOf(literal("merge"), literal("value"), float(value))
	fun merge(value: String) = listOf(literal("merge"), literal("value"), literal(value))
	fun merge(value: Boolean) = listOf(literal("merge"), literal("value"), bool(value))
	inline fun <reified T : Any> merge(value: @Serializable T) =
		listOf(literal("merge"), literal("value"), literal(StringifiedNbt.encodeToString(value)))

	fun prepend(from: DataArgument, path: String) =
		listOf(literal("prepend"), literal("from"), literal(from.literalName), from, literal(path))

	fun prepend(value: NbtTag) = listOf(literal("prepend"), literal("value"), literal(StringifiedNbt.encodeToString(value)))
	fun prepend(value: Int) = listOf(literal("prepend"), literal("value"), int(value))
	fun prepend(value: Float) = listOf(literal("prepend"), literal("value"), float(value))
	fun prepend(value: String) = listOf(literal("prepend"), literal("value"), literal(value))
	fun prepend(value: Boolean) = listOf(literal("prepend"), literal("value"), bool(value))
	inline fun <reified T : Any> prepend(value: @Serializable T) =
		listOf(literal("prepend"), literal("value"), literal(StringifiedNbt.encodeToString(value)))

	operator fun set(from: DataArgument, path: String) =
		listOf(literal("set"), literal("from"), literal(from.literalName), from, literal(path))

	fun set(string: DataArgument, path: String, start: Int? = null, end: Int? = null) =
		listOfNotNull(literal("set"), literal("string"), literal(string.literalName), string, literal(path), int(start), int(end))

	fun set(value: NbtTag) = listOf(literal("set"), literal("value"), literal(StringifiedNbt.encodeToString(value)))
	fun set(value: Int) = listOf(literal("set"), literal("value"), int(value))
	fun set(value: Float) = listOf(literal("set"), literal("value"), float(value))
	fun set(value: String) = listOf(literal("set"), literal("value"), textComponent(value))
	fun set(value: Boolean) = listOf(literal("set"), literal("value"), bool(value))
	inline fun <reified T : Any> set(value: @Serializable T) =
		listOf(literal("set"), literal("value"), literal(StringifiedNbt.encodeToString(value)))
}

class Data(val fn: Function, val target: DataArgument) {
	operator fun get(path: String? = null, scale: Double? = null) =
		fn.addLine(command("data", literal("get"), literal(target.literalName), target, literal(path), float(scale)))

	fun merge(data: NbtTag) =
		fn.addLine(command("data", literal("merge"), literal(target.literalName), target, literal(StringifiedNbt.encodeToString(data))))

	fun merge(block: NbtCompoundBuilder.() -> Unit) = merge(nbt(block))

	inline fun <reified T : Any> merge(data: @Serializable T) =
		fn.addLine(command("data", literal("merge"), literal(target.literalName), target, literal(StringifiedNbt.encodeToString(data))))

	fun modify(path: String, value: DataModifyOperation.() -> List<Argument>) =
		fn.addLine(
			command(
				"data",
				literal("modify"),
				literal(target.literalName),
				target,
				literal(path),
				*DataModifyOperation.value().toTypedArray()
			)
		)

	fun modify(path: String, from: DataArgument, fromPath: String) = modify(path) { set(from, fromPath) }
	fun modify(path: String, value: NbtTag) = modify(path) { set(value) }
	fun modify(path: String, value: Int) = modify(path) { set(value) }
	fun modify(path: String, value: Float) = modify(path) { set(value) }
	fun modify(path: String, value: String) = modify(path) { set(value) }
	fun modify(path: String, value: Boolean) = modify(path) { set(value) }
	inline fun <reified T : Any> modify(path: String, value: @Serializable T) = modify(path) { set(value) }

	fun remove(path: String) = fn.addLine(command("data", literal("remove"), literal(target.literalName), target, literal(path)))

	operator fun set(path: String, value: NbtTag) = modify(path) { set(value) }
	operator fun set(path: String, value: Int) = modify(path) { set(value) }
	operator fun set(path: String, value: Float) = modify(path) { set(value) }
	operator fun set(path: String, value: String) = modify(path) { set(value) }
	operator fun set(path: String, value: Boolean) = modify(path) { set(value) }
	inline operator fun <reified T : Any> set(path: String, value: @Serializable T) = modify(path) { set(value) }
}

fun Function.data(target: DataArgument, block: Data.() -> Command) = Data(this, target).block()
fun Function.data(target: DataArgument) = Data(this, target)
