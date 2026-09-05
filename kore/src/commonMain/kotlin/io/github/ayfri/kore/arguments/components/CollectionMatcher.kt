package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class ElementCountMatcher<T>(
	var count: IntRangeOrIntJson? = null,
	var test: T? = null,
)

@Serializable
data class CollectionMatcher<T>(
	var contains: List<T>? = null,
	var count: List<ElementCountMatcher<T>>? = null,
	var size: IntRangeOrIntJson? = null,
)

fun <T> CollectionMatcher<T>.contains(vararg elements: T) {
	contains = (contains ?: emptyList()) + elements.toList()
}

fun <T> CollectionMatcher<T>.contains(block: MutableList<T>.() -> Unit) {
	contains = (contains ?: emptyList()).toMutableList().apply(block)
}

fun <T> CollectionMatcher<T>.countElements(block: MutableList<ElementCountMatcher<T>>.() -> Unit) {
	count = (count ?: emptyList()).toMutableList().apply(block)
}

fun <T> CollectionMatcher<T>.countElement(size: IntRangeOrIntJson? = null, test: T? = null) {
	count = (count ?: emptyList()) + ElementCountMatcher(size, test)
}
