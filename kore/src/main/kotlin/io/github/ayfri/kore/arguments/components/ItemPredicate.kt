package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.components.matchers.ComponentMatcher
import io.github.ayfri.kore.arguments.components.types.Component
import io.github.ayfri.kore.arguments.components.types.CustomComponent
import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.utils.*
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.encodeToNbtTag
import net.benwoodworth.knbt.nbtCompound
import net.benwoodworth.knbt.nbtList

const val COUNT_ITEM_PREDICATE = "count"

@Serializable
private data class Range(val min: Int, val max: Int)

private fun String.negateIf(condition: Boolean) = if (condition) "!$this" else this

private data class ComponentEntry(val key: String, val negated: Boolean, val sign: String, val value: String?)

data class ItemPredicate(
	var itemArgument: ItemOrTagArgument? = null,
) : ComponentsPatch() {
	val componentsAlternatives = mutableMapOf<String, MutableList<Component>>()
	var countSubPredicates = mutableListOf<Pair<IntRangeOrIntJson, Boolean>>()
	var subPredicates = mutableListOf(ItemStackSubPredicates())
	var subPredicatesKeys = ComponentMatcher::class.sealedSubclasses.map(ComponentMatcher.Companion::getComponentName)

	override val lastAddedComponent get() = componentsAlternatives.values.lastOrNull()?.lastOrNull()
	override val lastAddedComponentName get() = componentsAlternatives.keys.lastOrNull()

	override val components
		get() = componentsAlternatives.mapValues { it.value.first() }.toMutableMap()

	override fun set(name: String, component: Component) {
		componentsAlternatives.getOrPut(name, ::mutableListOf).add(component)
	}

	override fun get(name: String) = componentsAlternatives[name]?.firstOrNull()

	override fun setToRemove(name: String) {
		componentsAlternatives[name]?.removeLastOrNull()
		componentsAlternatives.getOrPut("!$name", ::mutableListOf).add(EmptyComponent())
	}

	/**
	 * Set the last added component as negated. **Negated isn't similar to removed.**
	 * - Negated means that the value should not be equal to the one set.
	 * - Removed means that the component should not be present.
	 *
	 * Example:
	 * ```kotlin
	 * itemPredicate {
	 *    !damage(1)
	 *    damage(2)
	 * }
	 * ```
	 * Will output: `*[!damage=1|damage=2]`
	 */
	override fun ComponentsScope.not() {
		val subPredicatePredicateKeysList = subPredicatesKeys.joinToString("', '")
		val currentClassName = this::class.simpleName
		setNegated(
			lastAddedComponentName ?: error(
				"No component to set as removed, note that it doesn't work with '$COUNT_ITEM_PREDICATE' nor '$subPredicatePredicateKeysList' in '$currentClassName'."
			)
		)
	}

	fun setExpected(component: ItemComponentTypes) = setExpected(component.name.lowercase())
	fun setExpected(component: String) {
		componentsAlternatives.getOrPut(component, ::mutableListOf).add(EmptyComponent())
	}

	fun setNegated(component: ItemComponentTypes) = setNegated(component.name.lowercase())
	fun setNegated(name: String) = when (name) {
		COUNT_ITEM_PREDICATE -> {
			countSubPredicates.removeLastOrNull()?.let { countSubPredicates.add(it.first to true) }
		}

		else -> {
			val component =
				componentsAlternatives[name]?.withIndex()?.last() ?: error("The component '$name' is not present, can't negate its value.")
			componentsAlternatives[name]?.removeAt(component.index)
			componentsAlternatives.getOrPut("!$name", ::mutableListOf).add(component.value)
		}
	}

	fun setPartial(component: ItemComponentTypes) = setPartial(component.name.lowercase())
	fun setPartial(name: String) {
		val component = componentsAlternatives[name]?.lastOrNull() ?: error("The component '$name' is not present, can't make it partial.")
		componentsAlternatives[name]?.removeLastOrNull()
		componentsAlternatives.getOrPut("~$name", ::mutableListOf).add(component)
	}

	override fun asNbt(): NbtCompound {
		val classicComponents = componentsAlternatives.filterValues { it !is CustomComponent }
		val customComponents = componentsAlternatives.filterValues { it is CustomComponent } as Map<String, List<CustomComponent>>

		val classicComponentsSerialized = nbt {
			classicComponents.forEach { (key, value) -> put(key, snbtSerializer.encodeToNbtTag(value)) }
		}

		val customComponentsSerialized = nbt {
			customComponents.forEach { (key, value) -> put(key, snbtSerializer.encodeToNbtTag(value.map { it.nbt })) }
		}

		return nbt {
			classicComponentsSerialized.forEach { (key, value) -> put(key, value) }
			customComponentsSerialized.forEach { (key, value) -> put(key, value) }
		}
	}


	fun buildPredicateString(): String {
		val validEntries = asNbt().entries.filter { (_, values) -> values.nbtList.isNotEmpty() }
			.map { (key, values) -> key to values.nbtList }
			.toMutableList()

		val countSubPredicateKeys = countSubPredicates.map { (value, negated) ->
			value to COUNT_ITEM_PREDICATE.negateIf(negated)
		}

		countSubPredicateKeys.map { (value, key) ->
			validEntries += if (value.range != null) {
				"~${key}" to nbtList {
					this += snbtSerializer.encodeToNbtTag(Range(value.range.start!!, value.range.end!!)).nbtCompound
				}
			} else {
				key to nbtListOf(value.int!!)
			}
		}

		subPredicates.forEach { subPredicate ->
			val subPredicateEntries = snbtSerializer.encodeToNbtTag(subPredicate).nbtCompound
			for ((key, value) in subPredicateEntries) {
				val keyName = key.removePrefix("minecraft:")
				val prefix = if (value is NbtCompound) "~" else ""
				validEntries += "$prefix$keyName" to snbtSerializer.encodeToNbtTag(listOf(value)).nbtList
			}
		}

		val entries = mutableListOf<ComponentEntry>()
		for ((key, values) in validEntries) {
			val keyName = key.removePrefix("~").removePrefix("!")
			entries += values.nbtList.map {
				val value = when {
					keyName in CHAT_COMPONENTS_COMPONENTS_TYPES -> {
						val unescaped = it.toString().unescape()
							// The quotes are added by the serializer, we just need to unescape the string.
							.replace(Regex("\"\'\"(.+?)\"\'\"", RegexOption.DOT_MATCHES_ALL), "'\"$1\"'")
							// we also need a fix for JSON Components as they are serialized as JSON but single quoted.
							.replace(Regex("\"\'\\{(.+?)\\}\'\"", RegexOption.DOT_MATCHES_ALL), "'{$1}'")
						unescaped
					}

					it == nbt {} -> null
					else -> it.toString()
				}

				ComponentEntry(
					key = keyName,
					negated = "!" in key,
					sign = if ("~" in key) "~" else "=",
					value = value
				)
			}
		}

		if (entries.isEmpty()) return ""

		return entries.groupBy(ComponentEntry::key).values.joinToString(",", prefix = "[", postfix = "]") { values ->
			values.joinToString("|") { (key, negated, sign, value) ->
				val negation = if (negated) "!" else ""
				val valueString = value?.let { "$sign$value" } ?: ""
				"$negation$key$valueString"
			}
		}
	}

	override fun toString(): String {
		val itemString = itemArgument?.asString() ?: "*"
		val arrayComponents = buildPredicateString()

		return itemString + arrayComponents
	}
}
