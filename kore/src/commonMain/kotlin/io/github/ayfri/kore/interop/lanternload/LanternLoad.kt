package io.github.ayfri.kore.interop.lanternload

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.numbers.ranges.asRangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.asStartRangeOrInt
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.arguments.types.resources.tagged.FunctionTagArgument
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.features.tags.Tag
import io.github.ayfri.kore.features.tags.addToTag
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.function

/** Scoreboard objective every Lantern Load pack writes its version to. */
const val LOAD_STATUS_OBJECTIVE = "load.status"

/** Namespace owning the Lantern Load boilerplate. */
const val LOAD_NAMESPACE = "load"

/** Adds [id] to this tag unless it is already there, keeping the generated boilerplate idempotent. */
private fun Tag<FunctionTagArgument>.addUnique(id: String, required: Boolean? = null) {
	if (values.none { it.name == id }) add(id, required)
}

/**
 * Builder for the [Lantern Load](https://github.com/LanternMC/load) integration of a [DataPack].
 *
 * Produces, for a pack named `my_pack` at the default [directory]:
 * - `data/my_pack/tags/function/load.json`, the pack entry point, registered in `#load:load`
 * - `data/my_pack/tags/function/load/dependencies.json`, pulling in every dependency's own load tag
 * - `data/my_pack/function/load/enumerate.mcfunction`, publishing [version] on [objective]
 * - `data/my_pack/function/load/resolve.mcfunction`, the dependency guard, only when a dependency has a version
 * - `data/my_pack/function/load/init.mcfunction`, the body of [load]
 * - the Lantern Load pack files themselves, unless [generateBoilerplate] is disabled
 *
 * Docs: https://kore.ayfri.com/docs/guides/lantern-load
 */
class LanternLoad internal constructor(val dataPack: DataPack) {
	/** Namespace owning the generated functions and tags. */
	var namespace = dataPack.name

	/** Version published on [objective] as `<scoreHolder>.major`, `.minor` and `.patch`. */
	var version = LanternVersion(1)

	/** Fake player prefix carrying [version]. Defaults to [namespace]. */
	var scoreHolder: String? = null

	/** Objective holding the load status of every pack in the world. */
	var objective = LOAD_STATUS_OBJECTIVE

	/** Directory of the generated functions inside [namespace], also the name of the pack entry tag. */
	var directory = "load"

	/** Emits the Lantern Load pack files. Harmless to leave on, packs shipping them merge cleanly. */
	var generateBoilerplate = true

	/** Message broadcast when a dependency is missing or too old. */
	var missingDependencyMessage: (LanternDependency) -> ChatComponents = { dependency ->
		val expected = dependency.version?.let { " ${it.major}.${it.minor}" } ?: ""
		textComponent("[$namespace] Missing dependency ${dependency.namespace}$expected.", Color.RED)
	}

	val dependencies = mutableListOf<LanternDependency>()

	private var preLoadBlock: (Function.() -> Unit)? = null
	private var loadBlock: (Function.() -> Unit)? = null
	private var postLoadBlock: (Function.() -> Unit)? = null

	private val holder get() = scoreHolder ?: namespace

	/** Declares a dependency, [version] being `major[.minor[.patch]]`. */
	fun dependency(namespace: String, version: String? = null, scoreHolder: String = namespace, required: Boolean = false) {
		dependencies += LanternDependency(namespace, version?.let(LanternVersion::of), scoreHolder, required)
	}

	/** Declares a dependency on an exact major and a minimum minor version. */
	fun dependency(
		namespace: String,
		major: Int,
		minor: Int = 0,
		patch: Int = 0,
		scoreHolder: String = namespace,
		required: Boolean = false,
	) {
		dependencies += LanternDependency(namespace, LanternVersion(major, minor, patch), scoreHolder, required)
	}

	/** Declares a dependency. */
	fun dependency(dependency: LanternDependency) {
		dependencies += dependency
	}

	/** Sets [version] from a `major[.minor[.patch]]` string. */
	fun version(version: String) {
		this.version = LanternVersion.of(version)
	}

	/** Sets [version]. */
	fun version(major: Int, minor: Int = 0, patch: Int = 0) {
		version = LanternVersion(major, minor, patch)
	}

	/** Body of `<namespace>:<directory>/pre_load`, registered in `#load:pre_load` and run before every `#load:load` function. */
	fun preLoad(block: Function.() -> Unit) {
		preLoadBlock = block
	}

	/** Body of `<namespace>:<directory>/init`, run once every dependency guard passed. */
	fun load(block: Function.() -> Unit) {
		loadBlock = block
	}

	/** Body of `<namespace>:<directory>/post_load`, registered in `#load:post_load` and run after every `#load:load` function. */
	fun postLoad(block: Function.() -> Unit) {
		postLoadBlock = block
	}

	internal fun build(): FunctionArgument {
		if (generateBoilerplate) dataPack.lanternLoadBoilerplate(objective)

		// captured before the tag blocks below, where `namespace` would resolve to the Tag's own nullable namespace.
		val packNamespace = namespace
		val entryTag = "#$packNamespace:$directory"

		val enumerate = dataPack.function("enumerate", namespace, directory) {
			scoreboard.players.set(literal("$holder.major"), objective, version.major)
			scoreboard.players.set(literal("$holder.minor"), objective, version.minor)
			scoreboard.players.set(literal("$holder.patch"), objective, version.patch)
		}

		val init = dataPack.function("init", namespace, directory) { loadBlock?.invoke(this) }
		val guarded = dependencies.filter { it.version != null }
		val entryPoint = if (guarded.isEmpty()) init else dataPack.function("resolve", namespace, directory) {
			execute {
				guarded.forEach { dependency ->
					val dependencyVersion = dependency.version!!
					ifCondition {
						score(literal(dependency.majorHolder), objective, dependencyVersion.major.asRangeOrInt())
						score(literal(dependency.minorHolder), objective, dependencyVersion.minor.asStartRangeOrInt())
					}
				}
				run(init)
			}

			guarded.forEach { dependency ->
				val dependencyVersion = dependency.version!!
				val message = missingDependencyMessage(dependency)

				execute {
					unlessCondition { score(literal(dependency.majorHolder), objective, dependencyVersion.major.asRangeOrInt()) }
					run { tellraw(allPlayers(), message) }
				}

				execute {
					ifCondition { score(literal(dependency.majorHolder), objective, dependencyVersion.major.asRangeOrInt()) }
					unlessCondition { score(literal(dependency.minorHolder), objective, dependencyVersion.minor.asStartRangeOrInt()) }
					run { tellraw(allPlayers(), message) }
				}
			}
		}

		if (dependencies.isNotEmpty()) dataPack.addToTag<FunctionTagArgument>("$directory/dependencies", "function", packNamespace) {
			dependencies.forEach { addUnique(it.loadTag, if (it.required) null else false) }
		}

		dataPack.addToTag<FunctionTagArgument>(directory, "function", packNamespace) {
			if (dependencies.isNotEmpty()) addUnique("$entryTag/dependencies")
			addUnique(enumerate.asId())
			addUnique(entryPoint.asId())
		}

		dataPack.addToTag<FunctionTagArgument>("load", "function", LOAD_NAMESPACE) { addUnique(entryTag) }

		preLoadBlock?.let { block ->
			val preLoad = dataPack.function("pre_load", namespace, directory, block)
			dataPack.addToTag<FunctionTagArgument>("pre_load", "function", LOAD_NAMESPACE) { addUnique(preLoad.asId()) }
		}

		postLoadBlock?.let { block ->
			val postLoad = dataPack.function("post_load", namespace, directory, block)
			dataPack.addToTag<FunctionTagArgument>("post_load", "function", LOAD_NAMESPACE) { addUnique(postLoad.asId()) }
		}

		return init
	}
}

/**
 * Wires this data pack into [Lantern Load](https://github.com/LanternMC/load), the load-ordering and versioning
 * convention every major datapack library builds on.
 *
 * Returns the `init` function holding the [LanternLoad.load] body, so it can be called from elsewhere.
 *
 * ```kotlin
 * dataPack("my_pack") {
 *     lanternLoad {
 *         version(1, 2, 3)
 *         dependency("bs.math", 3, 1)
 *         load { say("loaded") }
 *     }
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/guides/lantern-load
 */
fun DataPack.lanternLoad(block: LanternLoad.() -> Unit = {}) = LanternLoad(this).apply(block).build()

/**
 * Emits the Lantern Load pack itself: the `#minecraft:load` entry, the private load chain, the three public
 * `#load:pre_load` / `#load:load` / `#load:post_load` tags and the function creating [objective].
 *
 * Called by [lanternLoad] unless [LanternLoad.generateBoilerplate] is disabled, and idempotent so several packs
 * merged together keep a single copy.
 */
fun DataPack.lanternLoadBoilerplate(objective: String = LOAD_STATUS_OBJECTIVE) {
	addToTag<FunctionTagArgument>("load", "function", "minecraft") { addUnique("#$LOAD_NAMESPACE:_private/load") }

	addToTag<FunctionTagArgument>("_private/load", "function", LOAD_NAMESPACE) {
		addUnique("#$LOAD_NAMESPACE:_private/init")
		addUnique("#$LOAD_NAMESPACE:pre_load", required = false)
		addUnique("#$LOAD_NAMESPACE:load", required = false)
		addUnique("#$LOAD_NAMESPACE:post_load", required = false)
	}

	addToTag<FunctionTagArgument>("_private/init", "function", LOAD_NAMESPACE) { addUnique("$LOAD_NAMESPACE:_private/init") }
	addToTag<FunctionTagArgument>("pre_load", "function", LOAD_NAMESPACE)
	addToTag<FunctionTagArgument>("load", "function", LOAD_NAMESPACE)
	addToTag<FunctionTagArgument>("post_load", "function", LOAD_NAMESPACE)

	if (functions.any { it.namespace == LOAD_NAMESPACE && it.directory == "_private" && it.name == "init" }) return

	function("init", LOAD_NAMESPACE, "_private") {
		comment("Reset scoreboards so packs can set values accurate for current load.")
		scoreboard.objectives.add(objective)
		scoreboard.players.reset(literal("*"), objective)
	}
}
