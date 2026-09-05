package io.github.ayfri.kore.helpers.nbt

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.enums.DataType
import io.github.ayfri.kore.arguments.types.DataArgument
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.resources.StorageArgument
import io.github.ayfri.kore.commands.Data
import io.github.ayfri.kore.commands.DataModifyOperation
import io.github.ayfri.kore.commands.execute.ExecuteStore
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.scoreboard.ScoreboardEntity
import io.github.ayfri.kore.scoreboard.copyTo
import io.github.ayfri.kore.utils.stringifiedNbt
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtTag

/** Segment characters Minecraft accepts unquoted inside an NBT path. */
private val UNQUOTED_SEGMENT = Regex("[A-Za-z0-9_+-]+")

/**
 * A typed NBT path, built from segments instead of string interpolation.
 *
 * ```kotlin
 * val enchantments = nbtPath("equipment") / slot / "components" / "minecraft:enchantments"
 * // equipment.mainhand.components."minecraft:enchantments"
 * ```
 *
 * Segments are quoted automatically when they contain anything outside `A-Za-z0-9_+-`, so namespaced keys and keys
 * carrying a dot stay valid.
 */
data class NbtPath(val segments: List<String> = emptyList()) {
	/** Whether this path points at the root compound, where only `data merge` applies. */
	val isRoot get() = segments.isEmpty()

	/** Appends [name] as a child key. */
	operator fun div(name: String) = NbtPath(segments + quote(name))

	/** Appends every segment of [other]. */
	operator fun div(other: NbtPath) = NbtPath(segments + other.segments)

	/** Indexes the last segment, producing `foo[2]`. Negative indices count from the end, as vanilla does. */
	operator fun get(index: Int) = withLastSuffix("[$index]")

	/** Filters the last segment on a compound, producing `foo[{id:"minecraft:stone"}]`. */
	fun matching(block: NbtCompoundBuilder.() -> Unit) = withLastSuffix("[${stringifiedNbt(block)}]")

	/** Selects every element of the last segment, producing `foo[]`. */
	fun all() = withLastSuffix("[]")

	private fun withLastSuffix(suffix: String): NbtPath {
		require(segments.isNotEmpty()) { "Cannot index the root NBT path." }
		return NbtPath(segments.dropLast(1) + (segments.last() + suffix))
	}

	override fun toString() = segments.joinToString(".")

	companion object {
		fun quote(name: String) = if (UNQUOTED_SEGMENT.matches(name)) name else "\"${name.replace("\"", "\\\"")}\""
	}
}

/** Builds an [NbtPath] from [names], one segment per argument. */
fun nbtPath(vararg names: String) = names.fold(NbtPath()) { path, name -> path / name }

/**
 * Parses a path already written in Minecraft's syntax, so an existing string constant can join the typed API:
 * `"Items[{Slot:0b}].components.\"minecraft:custom_data\"".toNbtPath()`.
 *
 * Splits on the dots that separate segments, leaving quoted keys, list indices, and compound filters intact. Segments
 * keep the exact form they had in [this], so a round-trip through [NbtPath.toString] returns the original string.
 */
fun String.toNbtPath(): NbtPath {
	if (isEmpty()) return NbtPath()

	val segments = mutableListOf<String>()
	val segment = StringBuilder()
	var depth = 0
	var quoted = false
	var escaped = false

	for (char in this) when {
		escaped -> {
			segment.append(char)
			escaped = false
		}

		quoted -> {
			segment.append(char)
			when (char) {
				'\\' -> escaped = true
				'"' -> quoted = false
			}
		}

		char == '"' -> {
			segment.append(char)
			quoted = true
		}

		char == '[' || char == '{' -> {
			segment.append(char)
			depth++
		}

		char == ']' || char == '}' -> {
			segment.append(char)
			depth--
		}

		char == '.' && depth == 0 -> {
			segments += segment.toString()
			segment.clear()
		}

		else -> segment.append(char)
	}

	require(depth == 0 && !quoted) { "Unbalanced NBT path '$this'." }
	segments += segment.toString()
	return NbtPath(segments)
}

/** Applies a `/data modify` operation at [path]. */
fun Data.modify(path: NbtPath, value: DataModifyOperation.() -> List<Argument>) = modify(path.toString(), value)

/** Writes [value] at [path]. */
fun Data.modify(path: NbtPath, value: NbtTag) = modify(path.toString(), value)

/** Writes [value] at [path]. */
fun Data.modify(path: NbtPath, value: Int) = modify(path.toString(), value)

/** Writes [value] at [path]. */
fun Data.modify(path: NbtPath, value: Float) = modify(path.toString(), value)

/** Writes [value] at [path]. */
fun Data.modify(path: NbtPath, value: String) = modify(path.toString(), value)

/** Writes [value] at [path]. */
fun Data.modify(path: NbtPath, value: Boolean) = modify(path.toString(), value)

/** Copies the value from [from]/[fromPath] into [path]. */
fun Data.modify(path: NbtPath, from: DataArgument, fromPath: NbtPath) = modify(path.toString(), from, fromPath.toString())

/** Reads the value stored at [path]. */
fun Data.get(path: NbtPath, scale: Double? = null) = get(path.toString(), scale)

/** Removes the value stored at [path]. */
fun Data.remove(path: NbtPath) = remove(path.toString())

/** Stores into [path] of [target]. */
fun ExecuteStore.storage(target: StorageArgument, path: NbtPath, type: DataType = DataType.INT, scale: Double = 1.0) =
	storage(target, path.toString(), type, scale)

/** Stores into [path] of [target]. */
fun ExecuteStore.entity(target: EntityArgument, path: NbtPath, type: DataType = DataType.INT, scale: Double = 1.0) =
	entity(target, path.toString(), type, scale)

/** Stores this score value into [path] of [target]. */
context(fn: Function)
fun ScoreboardEntity.copyTo(target: Entity, path: NbtPath, type: DataType = DataType.INT, scale: Double = 1.0) =
	copyTo(target, path.toString(), type, scale)

/** Stores this score value into [path] of [target]. */
context(fn: Function)
fun ScoreboardEntity.copyTo(target: StorageArgument, path: NbtPath, type: DataType = DataType.INT, scale: Double = 1.0) =
	copyTo(target, path.toString(), type, scale)
