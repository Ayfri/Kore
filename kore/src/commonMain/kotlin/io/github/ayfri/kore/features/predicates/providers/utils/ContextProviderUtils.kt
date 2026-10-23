package io.github.ayfri.kore.features.predicates.providers.utils

import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument
import io.github.ayfri.kore.features.predicates.providers.AddContextFloatProvider
import io.github.ayfri.kore.features.predicates.providers.AddContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.ConstantContextFloatProvider
import io.github.ayfri.kore.features.predicates.providers.ConstantContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.ContextFloatProvider
import io.github.ayfri.kore.features.predicates.providers.ContextFloatProviderReference
import io.github.ayfri.kore.features.predicates.providers.ContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.ContextIntProviderReference
import io.github.ayfri.kore.features.predicates.providers.MulContextFloatProvider
import io.github.ayfri.kore.features.predicates.providers.MulContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.NegateContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.SubContextIntProvider

private val Any?.isTagReference
	get() = this is ContextIntProviderReference && id is TaggedResourceLocationArgument ||
		this is ContextFloatProviderReference && id is TaggedResourceLocationArgument

private val ContextIntProvider.addOperands
	get() = if (this is AddContextIntProvider && inputs.none { it.isTagReference }) inputs else listOf(this)

private val ContextIntProvider.mulOperands
	get() = if (this is MulContextIntProvider && inputs.none { it.isTagReference }) inputs else listOf(this)

private val ContextFloatProvider.addOperands
	get() = if (this is AddContextFloatProvider && inputs.none { it.isTagReference }) inputs else listOf(this)

private val ContextFloatProvider.mulOperands
	get() = if (this is MulContextFloatProvider && inputs.none { it.isTagReference }) inputs else listOf(this)

/** The game reads `inputs` as a holder set, so a `#tag` is valid as the whole field and never next to other inputs. */
internal fun List<*>.requireTagOnlyAlone() = require(size == 1 || none { it.isTagReference }) {
	"A tag can only be the whole list, not one of its elements."
}

/**
 * The game combines every input of an `add` or `mul` in a `long` and checks the result once, so merging the constants
 * into one is exact as long as the merged value fits an int, otherwise the constants stay as written.
 */
private fun shapeInts(
	operands: List<ContextIntProvider>,
	identity: Long,
	combine: (Long, Long) -> Long,
	build: (List<ContextIntProvider>) -> ContextIntProvider,
): ContextIntProvider {
	val constants = operands.filterIsInstance<ConstantContextIntProvider>()
	val folded = constants.fold(identity) { acc, constant -> combine(acc, constant.value.toLong()) }
	if (constants.isEmpty() || folded !in Int.MIN_VALUE..Int.MAX_VALUE) return build(operands)

	val items = operands.filter { it !is ConstantContextIntProvider } +
		listOfNotNull(ConstantContextIntProvider(folded.toInt()).takeIf { folded != identity })
	return when (items.size) {
		0 -> ConstantContextIntProvider(identity.toInt())
		1 -> items.single()
		else -> build(items)
	}
}

internal fun addInts(left: ContextIntProvider, right: ContextIntProvider) =
	shapeInts(left.addOperands + right.addOperands, 0, Long::plus, ::AddContextIntProvider)

internal fun mulInts(left: ContextIntProvider, right: ContextIntProvider) =
	shapeInts(left.mulOperands + right.mulOperands, 1, Long::times, ::MulContextIntProvider)

internal fun subInts(left: ContextIntProvider, right: ContextIntProvider): ContextIntProvider {
	if (left is ConstantContextIntProvider && right is ConstantContextIntProvider) {
		val difference = left.value.toLong() - right.value
		if (difference in Int.MIN_VALUE..Int.MAX_VALUE) return ConstantContextIntProvider(difference.toInt())
	}
	return SubContextIntProvider(left, right)
}

internal fun negateInt(input: ContextIntProvider) =
	if (input is ConstantContextIntProvider && input.value != Int.MIN_VALUE) ConstantContextIntProvider(-input.value)
	else NegateContextIntProvider(input)

private fun ContextFloatProvider.isConstant(value: Float) = this is ConstantContextFloatProvider && this.value == value

internal fun addFloats(left: ContextFloatProvider, right: ContextFloatProvider) = when {
	right.isConstant(0f) -> left
	left.isConstant(0f) -> right
	else -> AddContextFloatProvider(left.addOperands + right)
}

internal fun mulFloats(left: ContextFloatProvider, right: ContextFloatProvider) = when {
	right.isConstant(1f) -> left
	left.isConstant(1f) -> right
	else -> MulContextFloatProvider(left.mulOperands + right)
}
