package io.github.ayfri.kore.strings

import io.github.ayfri.kore.arguments.enums.DataType
import io.github.ayfri.kore.arguments.enums.Relation
import io.github.ayfri.kore.arguments.numbers.ranges.IntRangeOrInt
import io.github.ayfri.kore.arguments.types.resources.StorageArgument
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.functions.Function

/**
 * Internal convenience layer on top of the Kore DSL.
 *
 * Every public helper of the dynamic string module routes its NBT, scoreboard and execute calls
 * through the functions defined here so they can be reviewed / ported in a single place. Only
 * macro bodies dealing with `$(var)` textual substitutions fall back to raw [Function.addLine].
 */

/** `function <ns>:<name> with storage <storage> <path>` using the Kore DSL. */
internal fun Function.callMacro(
	name: String,
	storage: StorageArgument,
	argsPath: String,
	namespace: String = datapack.name,
) = function(
	namespace = namespace,
	name = name,
	arguments = storage,
	path = argsPath,
)

/** Copies an NBT value from one path to another (set-from). */
internal fun Function.copyNbt(
	storage: StorageArgument,
	path: String,
	from: StorageArgument,
	fromPath: String,
) = data(storage) { modify(path) { set(from, fromPath) } }

/** `execute if score <a> <relation> <b> run function <ns>:<name>`. */
internal fun Function.ifScoreCompareRunFunction(
	left: ScoreCursor,
	relation: Relation,
	right: ScoreCursor,
	name: String,
	namespace: String = datapack.name,
) = addLine(
	"execute if score ${left.holder} ${left.objective} ${relation.symbol} " +
		"${right.holder} ${right.objective} run function $namespace:$name"
)

/** `execute if score <a> <relation> <b> run function <ns>:<name> with storage <storage> <path>`. */
internal fun Function.ifScoreCompareRunMacro(
	left: ScoreCursor,
	relation: Relation,
	right: ScoreCursor,
	name: String,
	storage: StorageArgument,
	path: String,
	namespace: String = datapack.name,
) = addLine(
	"execute if score ${left.holder} ${left.objective} ${relation.symbol} " +
		"${right.holder} ${right.objective} run function $namespace:$name " +
		"with storage ${storage.asString()} $path"
)

/** `execute if score <a> matches <range> run function <ns>:<name>`. */
internal fun Function.ifScoreMatchesRunFunction(
	cursor: ScoreCursor,
	range: IntRangeOrInt,
	name: String,
	namespace: String = datapack.name,
) = addLine(
	"execute if score ${cursor.holder} ${cursor.objective} matches ${range.asString()} " +
		"run function $namespace:$name"
)

/** `execute if score <a> matches <range> run function <ns>:<name> with storage <storage> <path>`. */
internal fun Function.ifScoreMatchesRunMacro(
	cursor: ScoreCursor,
	range: IntRangeOrInt,
	name: String,
	storage: StorageArgument,
	path: String,
	namespace: String = datapack.name,
) = addLine(
	"execute if score ${cursor.holder} ${cursor.objective} matches ${range.asString()} " +
		"run function $namespace:$name with storage ${storage.asString()} $path"
)

/** Scoreboard operation `<a> <op> <b>` through the Kore DSL. */
internal fun Function.scoreOperation(a: ScoreCursor, op: Operation, b: ScoreCursor) = a.operation(this, op, b)

/** Copies a literal into an NBT path. */
internal fun Function.setNbtString(storage: StorageArgument, path: String, value: String) =
	data(storage) { modify(path, value) }

/** Shortcut for `data modify <storage> <path> set string <storage> <srcPath> <start> <end>` with static bounds. */
internal fun Function.setSubstring(
	storage: StorageArgument,
	path: String,
	srcStorage: StorageArgument,
	srcPath: String,
	start: Int,
	end: Int? = null,
) = data(storage) { modify(path) { set(srcStorage, srcPath, start, end) } }

/**
 * Macro-aware substring: the DSL only accepts [Int] for start / end bounds, so we hand-craft this
 * single command with the shape expected by Minecraft when the bounds come from macros.
 */
internal fun Function.setSubstringMacro(
	storage: StorageArgument,
	path: String,
	srcStorage: StorageArgument,
	srcPath: String,
	startExpr: String,
	endExpr: String? = null,
) = addLine(buildString {
	append("data modify storage ${storage.asString()} $path set string ")
	append("storage ${srcStorage.asString()} $srcPath $startExpr")
	endExpr?.let { append(' ').append(it) }
})

/** Stores the numeric value of an NBT path into a score using `execute store result`. */
internal fun Function.storeNbtToScore(
	score: ScoreCursor,
	storage: StorageArgument,
	path: String,
	scale: Double = 1.0,
) = score.storeValueOfNbt(this, storage, path, scale)

/** Stores a score value into an NBT path using `execute store result`. */
internal fun Function.storeScoreToNbt(
	score: ScoreCursor,
	storage: StorageArgument,
	path: String,
	type: DataType = DataType.INT,
	scale: Double = 1.0,
) = score.writeToStorage(this, storage, path, type, scale)

/**
 * Stages [part] into [path] so a helper can read it as an NBT source. A [StringPart.Ref] is copied
 * rather than referenced, which is what makes `replace(needle, replacement)` safe when one of the
 * operands is the string being mutated.
 */
internal fun DynamicStringRuntime.writePart(fn: Function, part: StringPart, path: String) {
	when (part) {
		is StringPart.Literal -> fn.setNbtString(libStorageArg, path, part.value)
		is StringPart.Ref -> fn.copyNbt(libStorageArg, path, libStorageArg, part.string.nbtPath)
	}
}

/** Writes the length of [part] into [cursor], as a constant when it is known at generation time. */
internal fun DynamicStringRuntime.writePartLength(
	fn: Function,
	part: StringPart,
	path: String,
	cursor: ScoreCursor,
) {
	when (part) {
		is StringPart.Literal -> cursor.set(fn, part.value.length)
		is StringPart.Ref -> cursor.storeValueOfNbt(fn, libStorageArg, path)
	}
}

/** Subtracts the length of [part] from [cursor], reading [lengthCursor] only when the length is dynamic. */
internal fun subtractPartLength(fn: Function, part: StringPart, cursor: ScoreCursor, lengthCursor: ScoreCursor) {
	when (part) {
		is StringPart.Literal -> cursor.sub(fn, part.value.length)
		is StringPart.Ref -> cursor.subFrom(fn, lengthCursor)
	}
}
