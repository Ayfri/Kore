package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.features.predicates.providers.ConstantNumberProvider
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.NumberProviderReference
import io.github.ayfri.kore.features.predicates.providers.NumberProviderScope
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.NumberProviderArgument
import io.github.ayfri.kore.utils.encodeToDataPackNbtTag
import io.github.ayfri.kore.utils.encodeToDataPackSnbt
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.toSnbt
import net.benwoodworth.knbt.put

internal class ComputeTarget private constructor(val arguments: List<Argument>) {
	companion object {
		fun default() = ComputeTarget(listOf(literal("default")))
		fun block(pos: Vec3) = ComputeTarget(listOf(literal("block"), pos))
		fun entity(entity: EntityArgument) = ComputeTarget(listOf(literal("entity"), entity))
	}
}

/**
 * The `compute` source of `/data modify`: a number provider evaluated with no position (no target), at a block
 * (`pos`) or on a single entity (`entity`), stored as an int or float tag. The provider is an ID, a plain `Float`
 * constant or a lambda building it.
 *
 * ```
 * ComputeSource(bonus)                            // compute default my_pack:bonus
 * ComputeSource(vec3(1, 2, 3), bonus)             // compute block 1 2 3 my_pack:bonus
 * ComputeSource(self(), bonus, integer = true)    // compute entity @s my_pack:bonus integer
 * ComputeSource(5f, integer = true)               // compute default {type:"minecraft:constant",value:5} integer
 * ComputeSource(integer = true) { constant(5f) }  // compute default {type:"minecraft:constant",value:5} integer
 * data(storage("foo"))["bar"] = ComputeSource(self(), bonus)
 * // data modify storage minecraft:foo bar set compute entity @s my_pack:bonus
 * ```
 *
 * @property integer Stores an int tag rounded to the nearest integer instead of a float tag.
 */
class ComputeSource internal constructor(
	internal val target: ComputeTarget,
	internal val provider: NumberProvider,
	val integer: Boolean = false,
) {
	constructor(provider: NumberProviderArgument, integer: Boolean = false) :
		this(ComputeTarget.default(), NumberProviderReference(provider), integer)

	constructor(pos: Vec3, provider: NumberProviderArgument, integer: Boolean = false) :
		this(ComputeTarget.block(pos), NumberProviderReference(provider), integer)

	constructor(entity: EntityArgument, provider: NumberProviderArgument, integer: Boolean = false) :
		this(ComputeTarget.entity(entity), NumberProviderReference(provider), integer)

	constructor(provider: Float, integer: Boolean = false) :
		this(ComputeTarget.default(), ConstantNumberProvider(provider), integer)

	constructor(pos: Vec3, provider: Float, integer: Boolean = false) :
		this(ComputeTarget.block(pos), ConstantNumberProvider(provider), integer)

	constructor(entity: EntityArgument, provider: Float, integer: Boolean = false) :
		this(ComputeTarget.entity(entity), ConstantNumberProvider(provider), integer)

	constructor(integer: Boolean = false, provider: NumberProviderScope.() -> NumberProvider) :
		this(ComputeTarget.default(), object : NumberProviderScope {}.provider(), integer)

	constructor(pos: Vec3, integer: Boolean = false, provider: NumberProviderScope.() -> NumberProvider) :
		this(ComputeTarget.block(pos), object : NumberProviderScope {}.provider(), integer)

	constructor(entity: EntityArgument, integer: Boolean = false, provider: NumberProviderScope.() -> NumberProvider) :
		this(ComputeTarget.entity(entity), object : NumberProviderScope {}.provider(), integer)

	internal fun arguments() =
		listOfNotNull(literal("compute"), *target.arguments.toTypedArray(), provider.asArgument(), literal("integer").takeIf { integer })
}

/** The argument reads a bare number as an ID (`5` is `minecraft:5`), so a top-level constant needs its compound form. */
private fun NumberProvider.asArgument(): Argument = when (this) {
	is ConstantNumberProvider -> literal(nbt {
		put("type", "minecraft:constant")
		put("value", encodeToDataPackNbtTag(this@asArgument))
	}.toSnbt())
	is NumberProviderReference -> id
	else -> literal(encodeToDataPackSnbt(this))
}

private fun Function.addCompute(target: ComputeTarget, provider: NumberProvider, suffix: Argument?) =
	addLine(command("compute", *target.arguments.toTypedArray(), provider.asArgument(), suffix))

/**
 * Evaluates a number provider and stores the result, multiplied by [scale] and rounded down, as the command result.
 * With no target the provider has no position and no entity, with a [pos] it runs at that block, with an [entity] it runs
 * on that single entity. The provider is an ID, a plain `Float` constant or a lambda building it (`compute { myProvider }`
 * for one you already hold).
 *
 * ```
 * compute(bonus)                                   // compute default my_pack:bonus
 * compute(vec3(0, 64, 0), bonus, 2f)               // compute block 0 64 0 my_pack:bonus 2
 * compute(5f, 3f)                                  // compute default {type:"minecraft:constant",value:5} 3
 * compute(self()) { scoreNumber("kills", EntityTarget.THIS) }  // compute entity @s {type:"minecraft:score",...}
 * ```
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/compute)
 */
fun Function.compute(provider: NumberProviderArgument, scale: Float? = null) =
	addCompute(ComputeTarget.default(), NumberProviderReference(provider), float(scale))

/** Evaluates the number provider [provider] at the block at [pos], see [compute]. */
fun Function.compute(pos: Vec3, provider: NumberProviderArgument, scale: Float? = null) =
	addCompute(ComputeTarget.block(pos), NumberProviderReference(provider), float(scale))

/** Evaluates the number provider [provider] on the single entity [entity], see [compute]. */
fun Function.compute(entity: EntityArgument, provider: NumberProviderArgument, scale: Float? = null) =
	addCompute(ComputeTarget.entity(entity), NumberProviderReference(provider), float(scale))

/** Evaluates the constant [provider], see [compute]. */
fun Function.compute(provider: Float, scale: Float? = null) =
	addCompute(ComputeTarget.default(), ConstantNumberProvider(provider), float(scale))

/** Evaluates the constant [provider] at the block at [pos], see [compute]. */
fun Function.compute(pos: Vec3, provider: Float, scale: Float? = null) =
	addCompute(ComputeTarget.block(pos), ConstantNumberProvider(provider), float(scale))

/** Evaluates the constant [provider] on the single entity [entity], see [compute]. */
fun Function.compute(entity: EntityArgument, provider: Float, scale: Float? = null) =
	addCompute(ComputeTarget.entity(entity), ConstantNumberProvider(provider), float(scale))

/** Evaluates the number provider built by [provider], see [compute]. */
fun Function.compute(scale: Float? = null, provider: NumberProviderScope.() -> NumberProvider) =
	addCompute(ComputeTarget.default(), object : NumberProviderScope {}.provider(), float(scale))

/** Evaluates the number provider built by [provider] at the block at [pos], see [compute]. */
fun Function.compute(pos: Vec3, scale: Float? = null, provider: NumberProviderScope.() -> NumberProvider) =
	addCompute(ComputeTarget.block(pos), object : NumberProviderScope {}.provider(), float(scale))

/** Evaluates the number provider built by [provider] on the single entity [entity], see [compute]. */
fun Function.compute(entity: EntityArgument, scale: Float? = null, provider: NumberProviderScope.() -> NumberProvider) =
	addCompute(ComputeTarget.entity(entity), object : NumberProviderScope {}.provider(), float(scale))

/**
 * Evaluates a number provider in integer mode, which rounds the result to the nearest integer (`2.5` gives `3`) where the
 * float mode of [compute] rounds it down, and takes no scale. The targets and providers are the ones of [compute].
 *
 * ```
 * computeInteger(bonus)           // compute default my_pack:bonus integer
 * computeInteger(self(), bonus)   // compute entity @s my_pack:bonus integer
 * computeInteger(5f)              // compute default {type:"minecraft:constant",value:5} integer
 * ```
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/compute)
 */
fun Function.computeInteger(provider: NumberProviderArgument) =
	addCompute(ComputeTarget.default(), NumberProviderReference(provider), literal("integer"))

/** Evaluates the number provider [provider] at the block at [pos] in integer mode, see [computeInteger]. */
fun Function.computeInteger(pos: Vec3, provider: NumberProviderArgument) =
	addCompute(ComputeTarget.block(pos), NumberProviderReference(provider), literal("integer"))

/** Evaluates the number provider [provider] on the single entity [entity] in integer mode, see [computeInteger]. */
fun Function.computeInteger(entity: EntityArgument, provider: NumberProviderArgument) =
	addCompute(ComputeTarget.entity(entity), NumberProviderReference(provider), literal("integer"))

/** Evaluates the constant [provider] in integer mode, see [computeInteger]. */
fun Function.computeInteger(provider: Float) =
	addCompute(ComputeTarget.default(), ConstantNumberProvider(provider), literal("integer"))

/** Evaluates the constant [provider] at the block at [pos] in integer mode, see [computeInteger]. */
fun Function.computeInteger(pos: Vec3, provider: Float) =
	addCompute(ComputeTarget.block(pos), ConstantNumberProvider(provider), literal("integer"))

/** Evaluates the constant [provider] on the single entity [entity] in integer mode, see [computeInteger]. */
fun Function.computeInteger(entity: EntityArgument, provider: Float) =
	addCompute(ComputeTarget.entity(entity), ConstantNumberProvider(provider), literal("integer"))

/** Evaluates the number provider built by [provider] in integer mode, see [computeInteger]. */
fun Function.computeInteger(provider: NumberProviderScope.() -> NumberProvider) =
	addCompute(ComputeTarget.default(), object : NumberProviderScope {}.provider(), literal("integer"))

/** Evaluates the number provider built by [provider] at the block at [pos] in integer mode, see [computeInteger]. */
fun Function.computeInteger(pos: Vec3, provider: NumberProviderScope.() -> NumberProvider) =
	addCompute(ComputeTarget.block(pos), object : NumberProviderScope {}.provider(), literal("integer"))

/** Evaluates the number provider built by [provider] on the single entity [entity] in integer mode, see [computeInteger]. */
fun Function.computeInteger(entity: EntityArgument, provider: NumberProviderScope.() -> NumberProvider) =
	addCompute(ComputeTarget.entity(entity), object : NumberProviderScope {}.provider(), literal("integer"))
