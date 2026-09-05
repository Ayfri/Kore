package io.github.ayfri.kore.strings

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.enums.Relation
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.StorageArgument
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.functions.*
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.commands.function as callFunction

/**
 * Typed wrapper around a persistent NBT list of strings stored inside the Kore string library
 * storage, at `<storage> <listsRoot>.<name>` (by default `kore_string_lib:memory lists.<name>`).
 *
 * It is primarily used as the return type of split-like operations on [DynamicString], but can
 * also be built manually (for example from parsing helpers).
 */
class KoreStringList internal constructor(val name: String, val runtime: DynamicStringRuntime) {
	val nbtPath: String get() = runtime.listsPath(name)
	val storage: StorageArgument get() = runtime.libStorageArg

	override fun equals(other: Any?) = other is KoreStringList && other.name == name && other.runtime === runtime
	override fun hashCode() = name.hashCode()
	override fun toString() = "KoreStringList($name)"
}

/** Creates a new [KoreStringList] pointing at `<storage> <listsRoot>.<name>`, allocating the name. */
fun DynamicStringRuntime.koreStringList(name: String): KoreStringList {
	allocateList(name)
	return KoreStringList(name, this)
}

/** Creates a new [KoreStringList] in the context of the given [DataPack], allocating its name. */
fun DataPack.koreStringList(name: String): KoreStringList = requireDynamicStringRuntime().koreStringList(name)

/** Appends the literal [value] at the end of the list. */
context(fn: Function)
fun KoreStringList.append(value: String) = fn.data(storage) {
	modify(nbtPath) { append<String>(value) }
}

/** Appends the value of [source] at the end of the list. */
context(fn: Function)
fun KoreStringList.append(source: DynamicString) = fn.data(storage) {
	modify(nbtPath) { append(source.storage, source.nbtPath) }
}

/** Initializes the list to an empty NBT array, clearing any previous content. */
context(fn: Function)
fun KoreStringList.clear() = fn.data(storage) {
	modify(nbtPath, emptyList<String>())
}

/** Copies the element located at [index] into [target]. */
context(fn: Function)
fun KoreStringList.elementAt(index: Int, target: DynamicString) = fn.data(target.storage) {
	modify(target.nbtPath) { set(storage, "$nbtPath[$index]") }
}

/**
 * Iterates over every element of the list, binding the current element into [element] and running
 * [body] for each iteration. Generates one dedicated macro function per call site; the generated
 * helper reuses the string library storage for the bound element.
 */
context(fn: Function)
fun KoreStringList.forEach(element: DynamicString, body: Function.() -> Unit) {
	val id = runtime.nextAnonymousId()
	val bodyName = "${INTERNAL_NAME_PREFIX}foreach_body_$id"
	val loopName = "${INTERNAL_NAME_PREFIX}foreach_loop_$id"
	val obj = runtime.config.lengthObjective

	val bodyFn = fn.datapack.function(
		name = bodyName,
		namespace = fn.datapack.name,
	) { body() }

	val iCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}foreach_i", obj)
	val sizeCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}foreach_size", obj)
	val loopArgs = runtime.argsPath(loopName)

	val loopFn: FunctionWithMacros<ForEachMacros> = fn.datapack.function(
		name = loopName,
		namespace = fn.datapack.name,
		macros = ::ForEachMacros,
	) {
		addLine(
			"data modify storage ${runtime.libStorage} ${element.nbtPath} set from " +
				"storage ${runtime.libStorage} $nbtPath[${macros.index}]"
		)
		callFunction(namespace = datapack.name, name = bodyFn.name)
		iCursor.add(this, 1)
		storeScoreToNbt(iCursor, runtime.libStorageArg, "$loopArgs.index")
		ifScoreCompareRunMacro(
			left = iCursor,
			relation = Relation.LESS_THAN,
			right = sizeCursor,
			name = loopName,
			storage = runtime.libStorageArg,
			path = loopArgs,
		)
	}

	size(sizeCursor.holder)
	iCursor.set(fn, 0)
	iCursor.writeToStorage(fn, runtime.libStorageArg, "$loopArgs.index")
	fn.ifScoreCompareRunMacro(
		left = iCursor,
		relation = Relation.LESS_THAN,
		right = sizeCursor,
		name = loopFn.name,
		storage = runtime.libStorageArg,
		path = loopArgs,
	)
}

/** Alias of [elementAt] for ergonomic access. */
context(fn: Function)
operator fun KoreStringList.get(index: Int): GetElementInto = GetElementInto(this, index)

/** Inserts the literal [value] at [index]. */
context(fn: Function)
fun KoreStringList.insertAt(index: Int, value: String) = fn.data(storage) {
	modify(nbtPath) { insert<String>(index, value) }
}

/** Inserts the value of [source] at [index]. */
context(fn: Function)
fun KoreStringList.insertAt(index: Int, source: DynamicString) = fn.data(storage) {
	modify(nbtPath) { insert(index, source.storage, source.nbtPath) }
}

/** Prepends the literal [value] at the beginning of the list. */
context(fn: Function)
fun KoreStringList.prepend(value: String) = fn.data(storage) {
	modify(nbtPath) { prepend<String>(value) }
}

/** Appends the value of [source] at the beginning of the list. */
context(fn: Function)
fun KoreStringList.prepend(source: DynamicString) = fn.data(storage) {
	modify(nbtPath) { prepend(source.storage, source.nbtPath) }
}

/** Removes the element at [index]. */
context(fn: Function)
fun KoreStringList.removeAt(index: Int) = fn.data(storage) {
	remove("$nbtPath[$index]")
}

/** Replaces the element at [index] with the literal [value]. */
context(fn: Function)
fun KoreStringList.setAt(index: Int, value: String) = fn.data(storage) {
	modify("$nbtPath[$index]", value)
}

/** Replaces the element at [index] with the value of [source]. */
context(fn: Function)
fun KoreStringList.setAt(index: Int, source: DynamicString) = fn.data(storage) {
	modify("$nbtPath[$index]") { set(source.storage, source.nbtPath) }
}

/**
 * Stores the number of elements of this list into [holder] on the configured length objective
 * using `execute store result`. Returns [holder] so callers can chain the resulting score.
 */
context(fn: Function)
fun KoreStringList.size(holder: String = runtime.config.lengthHolder): String {
	fn.execute {
		storeResult { score(literal(holder), runtime.config.lengthObjective) }
		run { data(storage) { get(nbtPath) } }
	}
	return holder
}

/** Transitional wrapper returned by `list[index] into target` style calls. */
data class GetElementInto(val list: KoreStringList, val index: Int) {
	context(fn: Function)
	infix fun into(target: DynamicString) = list.elementAt(index, target)
}

/** Macros holder for the generated per-call `forEach` loop. */
class ForEachMacros internal constructor() : Macros() {
	val index by "index"
}
