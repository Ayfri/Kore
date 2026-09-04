package io.github.ayfri.kore.interop.lanternload

/**
 * A pack this data pack loads after, following the Lantern Load convention.
 *
 * Every dependency pulls `#<namespace>:load` into `<pack>:<directory>/dependencies` so the dependency initializes
 * first. A dependency carrying a [version] also gets a runtime guard: its major must match exactly and its minor
 * must be at least the requested one, which is the compatibility rule the ecosystem follows. A dependency without a
 * version only affects load ordering.
 *
 * Docs: https://kore.ayfri.com/docs/guides/lantern-load
 * Lantern Load: https://github.com/LanternMC/load
 *
 * @param namespace the dependency namespace, e.g. `bs.math`
 * @param scoreHolder the fake player prefix the dependency writes its version to, defaults to [namespace]
 * @param required whether Minecraft should error out when the dependency is absent, `false` by default so a missing
 * dependency is reported by the generated guard instead of breaking the load tag
 */
data class LanternDependency(
	val namespace: String,
	val version: LanternVersion? = null,
	val scoreHolder: String = namespace,
	val required: Boolean = false,
) {
	/** Entry added to the `<pack>:<directory>/dependencies` function tag. */
	val loadTag = "#$namespace:load"
	val majorHolder = "$scoreHolder.major"
	val minorHolder = "$scoreHolder.minor"
	val patchHolder = "$scoreHolder.patch"
}
