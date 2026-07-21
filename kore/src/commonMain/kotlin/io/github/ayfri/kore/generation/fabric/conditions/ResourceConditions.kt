package io.github.ayfri.kore.generation.fabric.conditions

import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** The root JSON key Fabric API reads resource conditions from. */
const val FABRIC_LOAD_CONDITIONS_KEY = "fabric:load_conditions"

/**
 * A Fabric API resource condition, serialized inside the [FABRIC_LOAD_CONDITIONS_KEY] array of a data file to control
 * whether the game loads that file (based on loaded mods, populated tags, feature flags, ...).
 *
 * Fabric applies these to recipes, advancements, loot tables, predicates and item modifiers by default. Attach them to
 * any Kore [Generator] with [fabricLoadConditions]; the array is injected at the JSON root during generation.
 *
 * Docs: https://kore.ayfri.com/docs/guides/fabric-resource-conditions
 * Fabric reference: https://docs.fabricmc.net/develop/resource-conditions
 */
@GeneratedSealedSerializer
@Serializable(with = ResourceCondition.Companion.ResourceConditionSerializer::class)
sealed class ResourceCondition {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object ResourceConditionSerializer : NamespacedPolymorphicSerializer<ResourceCondition>(
			resourceConditionSealedSerializer(),
			outputName = "condition",
			useMinecraftPrefix = false,
			contentName = { "fabric:$it" },
		)
	}
}

/** Succeeds when all mod ids in [values] are loaded. Serialized as `fabric:all_mods_loaded`. */
@Serializable
@SerialName("all_mods_loaded")
data class AllModsLoadedResourceCondition(val values: List<String>) : ResourceCondition()

/** Succeeds when every condition in [values] succeeds (and when empty). Serialized as `fabric:and`. */
@Serializable
@SerialName("and")
data class AndResourceCondition(val values: List<ResourceCondition>) : ResourceCondition()

/** Succeeds when at least one mod id in [values] is loaded. Serialized as `fabric:any_mods_loaded`. */
@Serializable
@SerialName("any_mods_loaded")
data class AnyModsLoadedResourceCondition(val values: List<String>) : ResourceCondition()

/** Succeeds when every feature flag in [features] is enabled. Serialized as `fabric:features_enabled`. */
@Serializable
@SerialName("features_enabled")
data class FeaturesEnabledResourceCondition(val features: List<String>) : ResourceCondition()

/** Succeeds when the wrapped [value] fails. Serialized as `fabric:not`. */
@Serializable
@SerialName("not")
data class NotResourceCondition(val value: ResourceCondition) : ResourceCondition()

/** Succeeds when at least one condition in [values] succeeds (and never when empty). Serialized as `fabric:or`. */
@Serializable
@SerialName("or")
data class OrResourceCondition(val values: List<ResourceCondition>) : ResourceCondition()

/**
 * Succeeds when [registry] (defaults to `minecraft:item` on the Fabric side when omitted) contains every id in
 * [values]. Serialized as `fabric:registry_contains`.
 */
@Serializable
@SerialName("registry_contains")
data class RegistryContainsResourceCondition(
	val registry: String? = null,
	val values: List<String>,
) : ResourceCondition()

/**
 * Succeeds when all tag ids in [values] are populated in [registry] (defaults to `minecraft:item` on the Fabric side
 * when omitted). Serialized as `fabric:tags_populated`.
 */
@Serializable
@SerialName("tags_populated")
data class TagsPopulatedResourceCondition(
	val registry: String? = null,
	val values: List<String>,
) : ResourceCondition()

/** Always succeeds. Serialized as `{"condition": "fabric:true"}`. */
@Serializable
@SerialName("true")
data object TrueResourceCondition : ResourceCondition()

/**
 * Scoped builder for Fabric [ResourceCondition]s, used inside [fabricLoadConditions]. Each factory both creates a
 * condition and adds it to the list, so calls read directly (`allModsLoaded(...)`). [and]/[or] open nested scopes,
 * and [not] re-parents the single leaf passed to it, keeping every name scoped to this builder instead of the global
 * namespace.
 */
class ResourceConditionsBuilder {
	val conditions = mutableListOf<ResourceCondition>()

	private fun <T : ResourceCondition> register(condition: T): T {
		conditions += condition
		return condition
	}

	// Removes the most recently registered condition equal to [condition], so [not] can adopt the leaf that was
	// auto-added when it was created as its argument.
	private fun consume(condition: ResourceCondition): ResourceCondition {
		val index = conditions.indexOfLast { it == condition }
		if (index >= 0) conditions.removeAt(index)
		return condition
	}

	/** Succeeds when all [modIds] are loaded. */
	fun allModsLoaded(vararg modIds: String) = register(AllModsLoadedResourceCondition(modIds.toList()))

	/** Succeeds when every condition declared in [block] succeeds (and when empty). */
	fun and(block: ResourceConditionsBuilder.() -> Unit) =
		register(AndResourceCondition(ResourceConditionsBuilder().apply(block).conditions))

	/** Succeeds when at least one of [modIds] is loaded. */
	fun anyModsLoaded(vararg modIds: String) = register(AnyModsLoadedResourceCondition(modIds.toList()))

	/** Always-succeeding condition. */
	fun conditionTrue() = register(TrueResourceCondition)

	/** Succeeds when all [features] flags are enabled. */
	fun featuresEnabled(vararg features: String) = register(FeaturesEnabledResourceCondition(features.toList()))

	/** Inverts [condition]. */
	fun not(condition: ResourceCondition) = register(NotResourceCondition(consume(condition)))

	/** Succeeds when at least one condition declared in [block] succeeds (and never when empty). */
	fun or(block: ResourceConditionsBuilder.() -> Unit) =
		register(OrResourceCondition(ResourceConditionsBuilder().apply(block).conditions))

	/** Succeeds when [registry] (defaults to `minecraft:item`) contains all [entries]. */
	fun registryContains(vararg entries: String, registry: String? = null) =
		register(RegistryContainsResourceCondition(registry, entries.toList()))

	/** Succeeds when all [tags] are populated in [registry] (defaults to `minecraft:item`). */
	fun tagsPopulated(vararg tags: String, registry: String? = null) =
		register(TagsPopulatedResourceCondition(registry, tags.toList()))
}

/**
 * Attaches Fabric [ResourceCondition]s to this generator via a scoped builder. They are injected as a
 * [FABRIC_LOAD_CONDITIONS_KEY] array at the JSON root during generation, so the game only loads the file when the
 * conditions pass.
 *
 * ```kotlin
 * advancement("secret") {
 *     fabricLoadConditions {
 *         allModsLoaded("create")
 *         not(anyModsLoaded("badmod"))
 *     }
 * }
 * ```
 *
 * Conditions only apply to resources whose JSON root is an object (recipes, advancements, loot tables, single-entry
 * predicates/item modifiers). Multi-entry predicates/item modifiers serialize as JSON arrays and cannot carry
 * conditions; a warning is emitted and they are ignored.
 *
 * Docs: https://kore.ayfri.com/docs/guides/fabric-resource-conditions
 * Fabric reference: https://docs.fabricmc.net/develop/resource-conditions
 */
fun Generator.fabricLoadConditions(block: ResourceConditionsBuilder.() -> Unit) {
	fabricLoadConditions = fabricLoadConditions + ResourceConditionsBuilder().apply(block).conditions
}
