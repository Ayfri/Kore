package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.DataArgument
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.bool
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.features.predicates.providers.ConstantContextFloatProvider
import io.github.ayfri.kore.features.predicates.providers.ConstantContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.ContextFloatProvider
import io.github.ayfri.kore.features.predicates.providers.ContextFloatProviderReference
import io.github.ayfri.kore.features.predicates.providers.ContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.ContextIntProviderReference
import io.github.ayfri.kore.features.predicates.providers.NumberProviderScope
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.ContextFloatProviderArgument
import io.github.ayfri.kore.generated.arguments.types.ContextIntProviderArgument
import io.github.ayfri.kore.utils.encodeToSnbt
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.toSnbt
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtTag
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

/**
 * Helpers that build the low-level `/data` subcommands used throughout Kore.
 *
 * This file is the bridge between the high-level DSL and the raw Minecraft data command:
 * read a value with `get`, copy data with `merge` or `modify`, and update NBT paths with the
 * various `set` / `append` / `insert` helpers.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/data)
 */
object DataModifyOperation {
	fun append(compute: ComputeSource) = listOf(literal("append")) + compute.arguments()

	fun append(from: DataArgument, path: String) =
		listOf(literal("append"), literal("from"), literal(from.literalName), from, literal(path))

	fun append(string: DataArgument, path: String, start: Int? = null, end: Int? = null) =
		listOfNotNull(
			literal("append"),
			literal("string"),
			literal(string.literalName),
			string,
			literal(path),
			int(start),
			int(end)
		)

	fun append(value: NbtTag) = listOf(literal("append"), literal("value"), literal(value.toSnbt()))
	fun append(value: Int) = listOf(literal("append"), literal("value"), int(value))
	fun append(value: Float) = listOf(literal("append"), literal("value"), float(value))
	fun append(value: String) = listOf(literal("append"), literal("value"), literal(value))
	fun append(value: Boolean) = listOf(literal("append"), literal("value"), bool(value))
	inline fun <reified T : Any> append(value: @Serializable T) =
		listOf(literal("append"), literal("value"), literal(encodeToSnbt(value)))

	/**
	 * Reads the context int provider [provider] with no position and no entity, as a source of an operation.
	 *
	 * ```
	 * data(storage("foo")) {
	 *     modify("bar") { set(compute(bonus)) }
	 *     // data modify storage minecraft:foo bar set compute default integer my_pack:bonus
	 * }
	 * ```
	 *
	 * An int provider stores an int tag, a float provider a float tag (never scaled). A provider that fails stores `0`
	 * (`0.0f` in float mode), where the `compute` command fails.
	 */
	fun compute(provider: ContextIntProviderArgument) =
		ComputeSource(ComputeTarget.default(), ContextIntProviderReference(provider).asArguments())

	/** Reads the context int provider [provider] at the block at [pos], see [compute]. */
	fun compute(pos: Vec3, provider: ContextIntProviderArgument) =
		ComputeSource(ComputeTarget.block(pos), ContextIntProviderReference(provider).asArguments())

	/** Reads the context int provider [provider] on the single entity [entity], see [compute]. */
	fun compute(entity: EntityArgument, provider: ContextIntProviderArgument) =
		ComputeSource(ComputeTarget.entity(entity), ContextIntProviderReference(provider).asArguments())

	/** Reads the context float provider [provider] with no position and no entity, see [compute]. */
	fun compute(provider: ContextFloatProviderArgument) =
		ComputeSource(ComputeTarget.default(), ContextFloatProviderReference(provider).asArguments())

	/** Reads the context float provider [provider] at the block at [pos], see [compute]. */
	fun compute(pos: Vec3, provider: ContextFloatProviderArgument) =
		ComputeSource(ComputeTarget.block(pos), ContextFloatProviderReference(provider).asArguments())

	/** Reads the context float provider [provider] on the single entity [entity], see [compute]. */
	fun compute(entity: EntityArgument, provider: ContextFloatProviderArgument) =
		ComputeSource(ComputeTarget.entity(entity), ContextFloatProviderReference(provider).asArguments())

	/**
	 * Reads the constant [provider] with no position and no entity in `integer` mode, see [compute].
	 *
	 * ```
	 * modify("bar") { append(compute(5)) }
	 * // data modify storage minecraft:foo bar append compute default integer {type:"minecraft:constant",value:5}
	 * ```
	 */
	fun compute(provider: Int) = ComputeSource(ComputeTarget.default(), ConstantContextIntProvider(provider).asArguments())

	/** Reads the constant [provider] at the block at [pos] in `integer` mode, see [compute]. */
	fun compute(pos: Vec3, provider: Int) =
		ComputeSource(ComputeTarget.block(pos), ConstantContextIntProvider(provider).asArguments())

	/** Reads the constant [provider] on the single entity [entity] in `integer` mode, see [compute]. */
	fun compute(entity: EntityArgument, provider: Int) =
		ComputeSource(ComputeTarget.entity(entity), ConstantContextIntProvider(provider).asArguments())

	/** Reads the constant [provider] with no position and no entity in `float` mode, see [compute]. */
	fun compute(provider: Float) =
		ComputeSource(ComputeTarget.default(), ConstantContextFloatProvider(provider).asArguments())

	/** Reads the constant [provider] at the block at [pos] in `float` mode, see [compute]. */
	fun compute(pos: Vec3, provider: Float) =
		ComputeSource(ComputeTarget.block(pos), ConstantContextFloatProvider(provider).asArguments())

	/** Reads the constant [provider] on the single entity [entity] in `float` mode, see [compute]. */
	fun compute(entity: EntityArgument, provider: Float) =
		ComputeSource(ComputeTarget.entity(entity), ConstantContextFloatProvider(provider).asArguments())

	/**
	 * Reads the context int provider built by [provider], see [compute].
	 *
	 * ```
	 * modify("bar") { append(compute(self()) { score("kills", EntityTarget.TARGET_ENTITY) + 1 }) }
	 * ```
	 */
	@OptIn(ExperimentalTypeInference::class)
	@OverloadResolutionByLambdaReturnType
	@JvmName("computeInt")
	fun compute(provider: NumberProviderScope.() -> ContextIntProvider) =
		ComputeSource(ComputeTarget.default(), ComputeScope.provider().asArguments())

	/** Reads the context int provider built by [provider] at the block at [pos], see [compute]. */
	@OptIn(ExperimentalTypeInference::class)
	@OverloadResolutionByLambdaReturnType
	@JvmName("computeInt")
	fun compute(pos: Vec3, provider: NumberProviderScope.() -> ContextIntProvider) =
		ComputeSource(ComputeTarget.block(pos), ComputeScope.provider().asArguments())

	/** Reads the context int provider built by [provider] on the single entity [entity], see [compute]. */
	@OptIn(ExperimentalTypeInference::class)
	@OverloadResolutionByLambdaReturnType
	@JvmName("computeInt")
	fun compute(entity: EntityArgument, provider: NumberProviderScope.() -> ContextIntProvider) =
		ComputeSource(ComputeTarget.entity(entity), ComputeScope.provider().asArguments())

	/** Reads the context float provider built by [provider], see [compute]. */
	@JvmName("computeFloat")
	fun compute(provider: NumberProviderScope.() -> ContextFloatProvider) =
		ComputeSource(ComputeTarget.default(), ComputeScope.provider().asArguments())

	/** Reads the context float provider built by [provider] at the block at [pos], see [compute]. */
	@JvmName("computeFloat")
	fun compute(pos: Vec3, provider: NumberProviderScope.() -> ContextFloatProvider) =
		ComputeSource(ComputeTarget.block(pos), ComputeScope.provider().asArguments())

	/** Reads the context float provider built by [provider] on the single entity [entity], see [compute]. */
	@JvmName("computeFloat")
	fun compute(entity: EntityArgument, provider: NumberProviderScope.() -> ContextFloatProvider) =
		ComputeSource(ComputeTarget.entity(entity), ComputeScope.provider().asArguments())

	fun insert(index: Int, compute: ComputeSource) = listOf(literal("insert"), int(index)) + compute.arguments()

	fun insert(index: Int, from: DataArgument, path: String) =
		listOf(literal("insert"), int(index), literal("from"), literal(from.literalName), from, literal(path))

	fun insert(index: Int, string: DataArgument, path: String, start: Int? = null, end: Int? = null) =
		listOfNotNull(
			literal("insert"),
			int(index),
			literal("string"),
			literal(string.literalName),
			string,
			literal(path),
			int(start),
			int(end),
		)

	fun insert(index: Int, value: NbtTag) =
		listOf(literal("insert"), int(index), literal("value"), literal(value.toSnbt()))

	fun insert(index: Int, value: Int) = listOf(literal("insert"), int(index), literal("value"), int(value))
	fun insert(index: Int, value: Float) = listOf(literal("insert"), int(index), literal("value"), float(value))
	fun insert(index: Int, value: String) = listOf(literal("insert"), int(index), literal("value"), literal(value))
	fun insert(index: Int, value: Boolean) = listOf(literal("insert"), int(index), literal("value"), bool(value))
	inline fun <reified T : Any> insert(index: Int, value: @Serializable T) =
		listOf(literal("insert"), int(index), literal("value"), literal(encodeToSnbt(value)))

	fun merge(compute: ComputeSource) = listOf(literal("merge")) + compute.arguments()

	fun merge(from: DataArgument, path: String) =
		listOf(literal("merge"), literal("from"), literal(from.literalName), from, literal(path))

	fun merge(string: DataArgument, path: String, start: Int? = null, end: Int? = null) =
		listOfNotNull(
			literal("merge"),
			literal("string"),
			literal(string.literalName),
			string,
			literal(path),
			int(start),
			int(end)
		)

	fun merge(value: NbtTag) = listOf(literal("merge"), literal("value"), literal(value.toSnbt()))
	fun merge(value: Int) = listOf(literal("merge"), literal("value"), int(value))
	fun merge(value: Float) = listOf(literal("merge"), literal("value"), float(value))
	fun merge(value: String) = listOf(literal("merge"), literal("value"), literal(value))
	fun merge(value: Boolean) = listOf(literal("merge"), literal("value"), bool(value))
	inline fun <reified T : Any> merge(value: @Serializable T) =
		listOf(literal("merge"), literal("value"), literal(encodeToSnbt(value)))

	fun prepend(compute: ComputeSource) = listOf(literal("prepend")) + compute.arguments()

	fun prepend(from: DataArgument, path: String) =
		listOf(literal("prepend"), literal("from"), literal(from.literalName), from, literal(path))

	fun prepend(string: DataArgument, path: String, start: Int? = null, end: Int? = null) =
		listOfNotNull(
			literal("prepend"),
			literal("string"),
			literal(string.literalName),
			string,
			literal(path),
			int(start),
			int(end)
		)

	fun prepend(value: NbtTag) = listOf(literal("prepend"), literal("value"), literal(value.toSnbt()))
	fun prepend(value: Int) = listOf(literal("prepend"), literal("value"), int(value))
	fun prepend(value: Float) = listOf(literal("prepend"), literal("value"), float(value))
	fun prepend(value: String) = listOf(literal("prepend"), literal("value"), literal(value))
	fun prepend(value: Boolean) = listOf(literal("prepend"), literal("value"), bool(value))
	inline fun <reified T : Any> prepend(value: @Serializable T) =
		listOf(literal("prepend"), literal("value"), literal(encodeToSnbt(value)))

	fun set(compute: ComputeSource) = listOf(literal("set")) + compute.arguments()

	operator fun set(from: DataArgument, path: String) =
		listOf(literal("set"), literal("from"), literal(from.literalName), from, literal(path))

	fun set(string: DataArgument, path: String, start: Int? = null, end: Int? = null) =
		listOfNotNull(literal("set"), literal("string"), literal(string.literalName), string, literal(path), int(start), int(end))

	fun set(value: NbtTag) = listOf(literal("set"), literal("value"), literal(value.toSnbt()))
	fun set(value: Int) = listOf(literal("set"), literal("value"), int(value))
	fun set(value: Float) = listOf(literal("set"), literal("value"), float(value))
	fun set(value: String) = listOf(literal("set"), literal("value"), textComponent(value))
	fun set(value: Boolean) = listOf(literal("set"), literal("value"), bool(value))
	inline fun <reified T : Any> set(value: @Serializable T) =
		listOf(literal("set"), literal("value"), literal(encodeToSnbt(value)))
}

/**
 * Builder bound to a single `/data` target.
 *
 * Use this when several data operations should target the same storage, block, or entity path.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/data)
 */
class Data(val fn: Function, val target: DataArgument) {
	/**
	 * Reads the value stored at [path] and optionally scales numeric output.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/data)
	 */
	operator fun get(path: String? = null, scale: Double? = null) =
		fn.addLine(command("data", literal("get"), literal(target.literalName), target, literal(path), float(scale)))

	/**
	 * Merges [data] into the bound target.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/data)
	 */
	fun merge(data: NbtTag) =
		fn.addLine(command("data", literal("merge"), literal(target.literalName), target, literal(data.toSnbt())))

	/**
	 * Merges the compound built by [block] into the bound target.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/data)
	 */
	fun merge(block: NbtCompoundBuilder.() -> Unit) = merge(nbt(block))

	/**
	 * Merges a serializable value into the bound target.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/data)
	 */
	inline fun <reified T : Any> merge(data: @Serializable T) =
		fn.addLine(command("data", literal("merge"), literal(target.literalName), target, literal(encodeToSnbt(data))))

	/**
	 * Applies a `/data modify` operation to the bound target at [path].
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/data)
	 */
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

	/** Copies the value from [from]/[fromPath] into [path]. */
	fun modify(path: String, from: DataArgument, fromPath: String) = modify(path) { set(from, fromPath) }
	/** Writes [value] into [path]. */
	fun modify(path: String, value: NbtTag) = modify(path) { set(value) }
	/** Writes [value] into [path]. */
	fun modify(path: String, value: Int) = modify(path) { set(value) }
	/** Writes [value] into [path]. */
	fun modify(path: String, value: Float) = modify(path) { set(value) }
	/** Writes [value] into [path]. */
	fun modify(path: String, value: String) = modify(path) { set(value) }
	/** Writes [value] into [path]. */
	fun modify(path: String, value: Boolean) = modify(path) { set(value) }
	/** Writes [value] into [path]. */
	inline fun <reified T : Any> modify(path: String, value: @Serializable T) = modify(path) { set(value) }

	/**
	 * Writes the result of the context int provider [provider], read with no position and no entity, into [path].
	 *
	 * ```
	 * data(storage("foo")).compute("bar", bonus)
	 * // data modify storage minecraft:foo bar set compute default integer my_pack:bonus
	 * ```
	 *
	 * An int provider stores an int tag, a float provider a float tag (never scaled). A provider that fails stores `0`
	 * (`0.0f` in float mode), where the `compute` command fails. In a `modify { }` block, `compute(...)` is the source of
	 * `append`, `insert`, `merge`, `prepend` and `set`.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/data)
	 */
	fun compute(path: String, provider: ContextIntProviderArgument) = modify(path) { set(compute(provider)) }

	/** Writes the result of the context int provider [provider], read at the block at [pos], into [path], see [compute]. */
	fun compute(path: String, pos: Vec3, provider: ContextIntProviderArgument) =
		modify(path) { set(compute(pos, provider)) }

	/** Writes the result of the context int provider [provider], read on the single entity [entity], into [path], see [compute]. */
	fun compute(path: String, entity: EntityArgument, provider: ContextIntProviderArgument) =
		modify(path) { set(compute(entity, provider)) }

	/** Writes the result of the context float provider [provider], read with no position and no entity, into [path], see [compute]. */
	fun compute(path: String, provider: ContextFloatProviderArgument) = modify(path) { set(compute(provider)) }

	/** Writes the result of the context float provider [provider], read at the block at [pos], into [path], see [compute]. */
	fun compute(path: String, pos: Vec3, provider: ContextFloatProviderArgument) =
		modify(path) { set(compute(pos, provider)) }

	/** Writes the result of the context float provider [provider], read on the single entity [entity], into [path], see [compute]. */
	fun compute(path: String, entity: EntityArgument, provider: ContextFloatProviderArgument) =
		modify(path) { set(compute(entity, provider)) }

	/**
	 * Writes the constant [provider] into [path], read with no position and no entity in `integer` mode, see [compute].
	 *
	 * ```
	 * data(storage("foo")).compute("bar", 5)
	 * // data modify storage minecraft:foo bar set compute default integer {type:"minecraft:constant",value:5}
	 * ```
	 */
	fun compute(path: String, provider: Int) = modify(path) { set(compute(provider)) }

	/** Writes the constant [provider] into [path], read at the block at [pos] in `integer` mode, see [compute]. */
	fun compute(path: String, pos: Vec3, provider: Int) = modify(path) { set(compute(pos, provider)) }

	/** Writes the constant [provider] into [path], read on the single entity [entity] in `integer` mode, see [compute]. */
	fun compute(path: String, entity: EntityArgument, provider: Int) = modify(path) { set(compute(entity, provider)) }

	/**
	 * Writes the constant [provider] into [path], read with no position and no entity in `float` mode, see [compute].
	 *
	 * ```
	 * data(storage("foo")).compute("bar", 2.5f)
	 * // data modify storage minecraft:foo bar set compute default float {type:"minecraft:constant",value:2.5f}
	 * ```
	 */
	fun compute(path: String, provider: Float) = modify(path) { set(compute(provider)) }

	/** Writes the constant [provider] into [path], read at the block at [pos] in `float` mode, see [compute]. */
	fun compute(path: String, pos: Vec3, provider: Float) = modify(path) { set(compute(pos, provider)) }

	/** Writes the constant [provider] into [path], read on the single entity [entity] in `float` mode, see [compute]. */
	fun compute(path: String, entity: EntityArgument, provider: Float) = modify(path) { set(compute(entity, provider)) }

	/**
	 * Writes the result of the context int provider built by [provider] into [path], see [compute].
	 *
	 * ```
	 * data(storage("foo")).compute("bar", self()) { score("kills", EntityTarget.TARGET_ENTITY) + 1 }
	 * // data modify storage minecraft:foo bar set compute entity @s integer {type:"minecraft:add",inputs:[{type:"minecraft:score",...},1]}
	 * ```
	 */
	@OptIn(ExperimentalTypeInference::class)
	@OverloadResolutionByLambdaReturnType
	@JvmName("computeInt")
	fun compute(path: String, provider: NumberProviderScope.() -> ContextIntProvider) =
		modify(path) { set(compute(provider)) }

	/** Writes the result of the context int provider built by [provider], read at the block at [pos], into [path], see [compute]. */
	@OptIn(ExperimentalTypeInference::class)
	@OverloadResolutionByLambdaReturnType
	@JvmName("computeInt")
	fun compute(path: String, pos: Vec3, provider: NumberProviderScope.() -> ContextIntProvider) =
		modify(path) { set(compute(pos, provider)) }

	/** Writes the result of the context int provider built by [provider], read on the single entity [entity], into [path], see [compute]. */
	@OptIn(ExperimentalTypeInference::class)
	@OverloadResolutionByLambdaReturnType
	@JvmName("computeInt")
	fun compute(path: String, entity: EntityArgument, provider: NumberProviderScope.() -> ContextIntProvider) =
		modify(path) { set(compute(entity, provider)) }

	/** Writes the result of the context float provider built by [provider] into [path], see [compute]. */
	@JvmName("computeFloat")
	fun compute(path: String, provider: NumberProviderScope.() -> ContextFloatProvider) =
		modify(path) { set(compute(provider)) }

	/** Writes the result of the context float provider built by [provider], read at the block at [pos], into [path], see [compute]. */
	@JvmName("computeFloat")
	fun compute(path: String, pos: Vec3, provider: NumberProviderScope.() -> ContextFloatProvider) =
		modify(path) { set(compute(pos, provider)) }

	/** Writes the result of the context float provider built by [provider], read on the single entity [entity], into [path], see [compute]. */
	@JvmName("computeFloat")
	fun compute(path: String, entity: EntityArgument, provider: NumberProviderScope.() -> ContextFloatProvider) =
		modify(path) { set(compute(entity, provider)) }

	/** Removes the value stored at [path]. */
	fun remove(path: String) = fn.addLine(command("data", literal("remove"), literal(target.literalName), target, literal(path)))

	/** Copies [value] into [path]. */
	operator fun set(path: String, value: NbtTag) = modify(path) { set(value) }
	/** Copies [value] into [path]. */
	operator fun set(path: String, value: Int) = modify(path) { set(value) }
	/** Copies [value] into [path]. */
	operator fun set(path: String, value: Float) = modify(path) { set(value) }
	/** Copies [value] into [path]. */
	operator fun set(path: String, value: String) = modify(path) { set(value) }
	/** Copies [value] into [path]. */
	operator fun set(path: String, value: Boolean) = modify(path) { set(value) }
	/** Copies [value] into [path]. */
	inline operator fun <reified T : Any> set(path: String, value: @Serializable T) = modify(path) { set(value) }
}

/** Opens the reusable [/data](https://minecraft.wiki/w/Commands/data) DSL bound to [target]. */
fun Function.data(target: DataArgument, block: Data.() -> Command) = Data(this, target).block()

/** Returns the reusable [/data](https://minecraft.wiki/w/Commands/data) DSL bound to [target]. */
fun Function.data(target: DataArgument) = Data(this, target)
