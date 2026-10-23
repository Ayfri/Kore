package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.float
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
import io.github.ayfri.kore.utils.encodeToDataPackSnbt
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.toSnbt
import net.benwoodworth.knbt.put
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

internal class ComputeTarget private constructor(val arguments: List<Argument>) {
	companion object {
		fun default() = ComputeTarget(listOf(literal("default")))
		fun block(pos: Vec3) = ComputeTarget(listOf(literal("block"), pos))
		fun entity(entity: EntityArgument) = ComputeTarget(listOf(literal("entity"), entity))
	}
}

private object ComputeScope : NumberProviderScope

/**
 * The `compute` source of `/data modify`: a context provider evaluated with no position (no target), at a block (`pos`)
 * or on a single entity (`entity`), stored as an int tag for a context int provider and as a float tag for a context
 * float provider. The provider is an ID, a plain `Int` or `Float` constant or a lambda building it. A provider that
 * fails (a division by zero, an overflow, a value that isn't finite) stores `0`.
 *
 * ```
 * ComputeSource(bonus)                      // compute default integer my_pack:bonus
 * ComputeSource(vec3(1, 2, 3), bonus)       // compute block 1 2 3 integer my_pack:bonus
 * ComputeSource(self(), 2.5f)               // compute entity @s float {type:"minecraft:constant",value:2.5f}
 * ComputeSource { constant(5) }             // compute default integer {type:"minecraft:constant",value:5}
 * data(storage("foo"))["bar"] = ComputeSource(self(), bonus)
 * // data modify storage minecraft:foo bar set compute entity @s integer my_pack:bonus
 * ```
 */
class ComputeSource internal constructor(
	internal val target: ComputeTarget,
	internal val provider: List<Argument>,
) {
	companion object {
		operator fun invoke(provider: ContextIntProviderArgument) =
			ComputeSource(ComputeTarget.default(), ContextIntProviderReference(provider).asArguments())

		operator fun invoke(pos: Vec3, provider: ContextIntProviderArgument) =
			ComputeSource(ComputeTarget.block(pos), ContextIntProviderReference(provider).asArguments())

		operator fun invoke(entity: EntityArgument, provider: ContextIntProviderArgument) =
			ComputeSource(ComputeTarget.entity(entity), ContextIntProviderReference(provider).asArguments())

		operator fun invoke(provider: ContextFloatProviderArgument) =
			ComputeSource(ComputeTarget.default(), ContextFloatProviderReference(provider).asArguments())

		operator fun invoke(pos: Vec3, provider: ContextFloatProviderArgument) =
			ComputeSource(ComputeTarget.block(pos), ContextFloatProviderReference(provider).asArguments())

		operator fun invoke(entity: EntityArgument, provider: ContextFloatProviderArgument) =
			ComputeSource(ComputeTarget.entity(entity), ContextFloatProviderReference(provider).asArguments())

		operator fun invoke(provider: Int) =
			ComputeSource(ComputeTarget.default(), ConstantContextIntProvider(provider).asArguments())

		operator fun invoke(pos: Vec3, provider: Int) =
			ComputeSource(ComputeTarget.block(pos), ConstantContextIntProvider(provider).asArguments())

		operator fun invoke(entity: EntityArgument, provider: Int) =
			ComputeSource(ComputeTarget.entity(entity), ConstantContextIntProvider(provider).asArguments())

		operator fun invoke(provider: Float) =
			ComputeSource(ComputeTarget.default(), ConstantContextFloatProvider(provider).asArguments())

		operator fun invoke(pos: Vec3, provider: Float) =
			ComputeSource(ComputeTarget.block(pos), ConstantContextFloatProvider(provider).asArguments())

		operator fun invoke(entity: EntityArgument, provider: Float) =
			ComputeSource(ComputeTarget.entity(entity), ConstantContextFloatProvider(provider).asArguments())

		@OptIn(ExperimentalTypeInference::class)
		@OverloadResolutionByLambdaReturnType
		@JvmName("invokeInt")
		operator fun invoke(provider: NumberProviderScope.() -> ContextIntProvider) =
			ComputeSource(ComputeTarget.default(), ComputeScope.provider().asArguments())

		@OptIn(ExperimentalTypeInference::class)
		@OverloadResolutionByLambdaReturnType
		@JvmName("invokeInt")
		operator fun invoke(pos: Vec3, provider: NumberProviderScope.() -> ContextIntProvider) =
			ComputeSource(ComputeTarget.block(pos), ComputeScope.provider().asArguments())

		@OptIn(ExperimentalTypeInference::class)
		@OverloadResolutionByLambdaReturnType
		@JvmName("invokeInt")
		operator fun invoke(entity: EntityArgument, provider: NumberProviderScope.() -> ContextIntProvider) =
			ComputeSource(ComputeTarget.entity(entity), ComputeScope.provider().asArguments())

		@JvmName("invokeFloat")
		operator fun invoke(provider: NumberProviderScope.() -> ContextFloatProvider) =
			ComputeSource(ComputeTarget.default(), ComputeScope.provider().asArguments())

		@JvmName("invokeFloat")
		operator fun invoke(pos: Vec3, provider: NumberProviderScope.() -> ContextFloatProvider) =
			ComputeSource(ComputeTarget.block(pos), ComputeScope.provider().asArguments())

		@JvmName("invokeFloat")
		operator fun invoke(entity: EntityArgument, provider: NumberProviderScope.() -> ContextFloatProvider) =
			ComputeSource(ComputeTarget.entity(entity), ComputeScope.provider().asArguments())
	}

	internal fun arguments() = listOf(literal("compute"), *target.arguments.toTypedArray(), *provider.toTypedArray())
}

/** The argument reads a bare number as an ID (`5` is `minecraft:5`), so a top-level constant needs its compound form. */
private fun ContextIntProvider.asArgument(): Argument = when (this) {
	is ConstantContextIntProvider -> literal(nbt {
		put("type", "minecraft:constant")
		put("value", value)
	}.toSnbt())
	is ContextIntProviderReference -> id
	else -> literal(encodeToDataPackSnbt(ContextIntProvider.serializer(), this))
}

/** The argument reads a bare number as an ID (`5.0` is `minecraft:5.0`), so a top-level constant needs its compound form. */
private fun ContextFloatProvider.asArgument(): Argument = when (this) {
	is ConstantContextFloatProvider -> {
		require(value.isFinite()) { "A 'compute' constant must be finite, got $value." }
		literal(nbt {
			put("type", "minecraft:constant")
			put("value", value)
		}.toSnbt())
	}
	is ContextFloatProviderReference -> id
	else -> literal(encodeToDataPackSnbt(ContextFloatProvider.serializer(), this))
}

private fun ContextIntProvider.asArguments() = listOf(literal("integer"), asArgument())

private fun ContextFloatProvider.asArguments() = listOf(literal("float"), asArgument())

private fun Function.addCompute(target: ComputeTarget, provider: List<Argument>, scale: Float? = null) =
	addLine(command("compute", *target.arguments.toTypedArray(), *provider.toTypedArray(), float(scale)))

/**
 * Evaluates a context int provider in `integer` mode and stores the result as the command result. With no target the
 * provider has no position and no entity, with a [pos] it runs at that block, with an [entity] it runs on that single
 * entity. The provider is an ID, a plain `Int` constant or a lambda building it (`compute { myProvider }` for one you
 * already hold). The command fails when the provider fails (a division by zero, an overflow).
 *
 * ```
 * compute(bonus)                                 // compute default integer my_pack:bonus
 * compute(vec3(0, 64, 0), bonus)                 // compute block 0 64 0 integer my_pack:bonus
 * compute(5)                                     // compute default integer {type:"minecraft:constant",value:5}
 * compute(self()) { score("kills", EntityTarget.THIS) }  // compute entity @s integer {type:"minecraft:score",...}
 * ```
 *
 * `execute store result score ... run compute ...` stores the result in a score.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/compute)
 */
fun Function.compute(provider: ContextIntProviderArgument) =
	addCompute(ComputeTarget.default(), ContextIntProviderReference(provider).asArguments())

/** Evaluates the context int provider [provider] at the block at [pos], see [compute]. */
fun Function.compute(pos: Vec3, provider: ContextIntProviderArgument) =
	addCompute(ComputeTarget.block(pos), ContextIntProviderReference(provider).asArguments())

/** Evaluates the context int provider [provider] on the single entity [entity], see [compute]. */
fun Function.compute(entity: EntityArgument, provider: ContextIntProviderArgument) =
	addCompute(ComputeTarget.entity(entity), ContextIntProviderReference(provider).asArguments())

/** Evaluates the constant [provider] in `integer` mode, see [compute]. */
fun Function.compute(provider: Int) =
	addCompute(ComputeTarget.default(), ConstantContextIntProvider(provider).asArguments())

/** Evaluates the constant [provider] at the block at [pos] in `integer` mode, see [compute]. */
fun Function.compute(pos: Vec3, provider: Int) =
	addCompute(ComputeTarget.block(pos), ConstantContextIntProvider(provider).asArguments())

/** Evaluates the constant [provider] on the single entity [entity] in `integer` mode, see [compute]. */
fun Function.compute(entity: EntityArgument, provider: Int) =
	addCompute(ComputeTarget.entity(entity), ConstantContextIntProvider(provider).asArguments())

/** Evaluates the context int provider built by [provider], see [compute]. */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
fun Function.compute(provider: NumberProviderScope.() -> ContextIntProvider) =
	addCompute(ComputeTarget.default(), ComputeScope.provider().asArguments())

/** Evaluates the context int provider built by [provider] at the block at [pos], see [compute]. */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
fun Function.compute(pos: Vec3, provider: NumberProviderScope.() -> ContextIntProvider) =
	addCompute(ComputeTarget.block(pos), ComputeScope.provider().asArguments())

/** Evaluates the context int provider built by [provider] on the single entity [entity], see [compute]. */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
fun Function.compute(entity: EntityArgument, provider: NumberProviderScope.() -> ContextIntProvider) =
	addCompute(ComputeTarget.entity(entity), ComputeScope.provider().asArguments())

/**
 * Evaluates a context float provider in `float` mode and stores the result, multiplied by [scale] and rounded down, as the
 * command result (`2.5` gives `2`). The targets and providers are the ones of the integer [compute], with a `Float`
 * constant or a lambda returning a float provider. The command fails when the result isn't finite or the provider fails.
 *
 * ```
 * compute(ContextFloatProviderArgument("speed", "my_pack"), 10f)  // compute default float my_pack:speed 10
 * compute(vec3(0, 64, 0), ContextFloatProviders.Cooking.SPEED_DEFAULT)  // compute block 0 64 0 float minecraft:cooking/speed_default
 * compute(5f, 3f)                                                  // compute default float {type:"minecraft:constant",value:5.0f} 3
 * compute(2f) { sqrt(constant(4f)) }                               // compute default float {type:"minecraft:sqrt",input:4} 2
 * ```
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/compute)
 */
fun Function.compute(provider: ContextFloatProviderArgument, scale: Float? = null) =
	addCompute(ComputeTarget.default(), ContextFloatProviderReference(provider).asArguments(), scale)

/** Evaluates the context float provider [provider] at the block at [pos], see the float [compute]. */
fun Function.compute(pos: Vec3, provider: ContextFloatProviderArgument, scale: Float? = null) =
	addCompute(ComputeTarget.block(pos), ContextFloatProviderReference(provider).asArguments(), scale)

/** Evaluates the context float provider [provider] on the single entity [entity], see the float [compute]. */
fun Function.compute(entity: EntityArgument, provider: ContextFloatProviderArgument, scale: Float? = null) =
	addCompute(ComputeTarget.entity(entity), ContextFloatProviderReference(provider).asArguments(), scale)

/** Evaluates the constant [provider] in `float` mode, see the float [compute]. */
fun Function.compute(provider: Float, scale: Float? = null) =
	addCompute(ComputeTarget.default(), ConstantContextFloatProvider(provider).asArguments(), scale)

/** Evaluates the constant [provider] at the block at [pos] in `float` mode, see the float [compute]. */
fun Function.compute(pos: Vec3, provider: Float, scale: Float? = null) =
	addCompute(ComputeTarget.block(pos), ConstantContextFloatProvider(provider).asArguments(), scale)

/** Evaluates the constant [provider] on the single entity [entity] in `float` mode, see the float [compute]. */
fun Function.compute(entity: EntityArgument, provider: Float, scale: Float? = null) =
	addCompute(ComputeTarget.entity(entity), ConstantContextFloatProvider(provider).asArguments(), scale)

/** Evaluates the context float provider built by [provider], see the float [compute]. */
fun Function.compute(scale: Float? = null, provider: NumberProviderScope.() -> ContextFloatProvider) =
	addCompute(ComputeTarget.default(), ComputeScope.provider().asArguments(), scale)

/** Evaluates the context float provider built by [provider] at the block at [pos], see the float [compute]. */
fun Function.compute(pos: Vec3, scale: Float? = null, provider: NumberProviderScope.() -> ContextFloatProvider) =
	addCompute(ComputeTarget.block(pos), ComputeScope.provider().asArguments(), scale)

/** Evaluates the context float provider built by [provider] on the single entity [entity], see the float [compute]. */
fun Function.compute(entity: EntityArgument, scale: Float? = null, provider: NumberProviderScope.() -> ContextFloatProvider) =
	addCompute(ComputeTarget.entity(entity), ComputeScope.provider().asArguments(), scale)
