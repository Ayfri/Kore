package io.github.ayfri.kore.entities

import io.github.ayfri.kore.arguments.selector.SelectorArguments
import io.github.ayfri.kore.arguments.types.literals.literal

/**
 * A scoreboard-only holder such as `#total` or `§0`.
 *
 * Vanilla accepts any string as a score holder, and holders starting with `#` are hidden from the sidebar, which is
 * how datapacks store globals and constants. There is no entity behind one, so [asSelector] throws instead of
 * emitting `@e[name=#total]`, which matches a custom name and would silently target nothing.
 */
class FakePlayer(val holderName: String) : Entity() {
	override val isPlayer get() = false

	override fun asScoreHolder() = literal(holderName)

	override fun asSelector(limitToOne: Boolean, modification: SelectorArguments.() -> Unit): Nothing =
		error("Fake player '$holderName' has no entity selector, it can only be used as a score holder.")

	override fun equals(other: Any?) = other is FakePlayer && other.holderName == holderName

	override fun hashCode() = holderName.hashCode()

	override fun toString() = holderName
}

/**
 * Creates a [FakePlayer] holder, prefixing [name] with `#` unless it already starts with one.
 *
 * Use the [FakePlayer] constructor directly for holders that must not carry that prefix, such as the `§0`..`§f`
 * holders backing sidebar lines.
 */
fun fakePlayer(name: String) = FakePlayer(if (name.startsWith("#")) name else "#$name")
