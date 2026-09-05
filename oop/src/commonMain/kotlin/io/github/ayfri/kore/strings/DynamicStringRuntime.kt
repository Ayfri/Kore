package io.github.ayfri.kore.strings

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.types.resources.StorageArgument
import io.github.ayfri.kore.arguments.types.resources.storage
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.functions.FunctionWithMacros
import io.github.ayfri.kore.functions.Macros
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.load

/**
 * Configuration knobs for the Kore dynamic string runtime. Every string helper routes its
 * reads / writes through the values exposed here, so swapping the storage namespace or the backing
 * objective is a single-call change.
 *
 * Defaults mirror the values stored on [OopConstants].
 */
data class DynamicStringConfig(
	val argsRoot: String = OopConstants.stringArgsRoot,
	val heapRoot: String = "heap",
	val lengthHolder: String = OopConstants.stringLengthHolder,
	val lengthObjective: String = OopConstants.stringLengthObjective,
	val listsRoot: String = "lists",
	val storageName: String = OopConstants.stringStorageName,
	val storageNamespace: String = OopConstants.stringStorageNamespace,
	val tmpRoot: String = OopConstants.stringTmpRoot,
)

/**
 * Holds every helper function (macros, recursive loops, …) required by the Kore dynamic string module.
 *
 * The runtime is lazy: each helper is registered on the owning [DataPack] only when its matching
 * Kotlin extension is actually used. This keeps the generated datapack minimal when only a subset of
 * the string API is consumed.
 */
class DynamicStringRuntime internal constructor(
	val datapack: DataPack,
	val config: DynamicStringConfig = DynamicStringConfig(),
) {
	internal val allocatedListNames = mutableSetOf<String>()
	internal val allocatedStringNames = mutableSetOf<String>()
	internal val libStorageArg: StorageArgument = storage(config.storageName, config.storageNamespace)
	internal val libStorage: String = libStorageArg.asString()
	internal val registered = mutableMapOf<String, FunctionWithMacros<*>>()

	private var anonymousCounter = 0

	internal fun allocateList(name: String) {
		require(allocatedListNames.add(name)) { "KoreStringList name '$name' is already allocated in this datapack." }
	}

	internal fun allocateString(name: String) {
		require(allocatedStringNames.add(name)) { "DynamicString name '$name' is already allocated in this datapack." }
	}

	internal fun argsPath(helper: String): String = "${config.argsRoot}.$helper"
	internal fun heapPath(name: String): String = "${config.heapRoot}.$name"
	internal fun listsPath(name: String): String = "${config.listsRoot}.$name"
	internal fun nextAnonymousName(prefix: String): String = "${prefix}_${anonymousCounter++}"
	internal fun tmpPath(name: String): String = "${config.tmpRoot}.$name"

	internal fun <T : Macros> ensure(
		name: String,
		factory: () -> T,
		body: FunctionWithMacros<T>.() -> Unit,
	): FunctionWithMacros<T> {
		@Suppress("UNCHECKED_CAST")
		return registered.getOrPut(name) {
			datapack.function(name = name, macros = factory, namespace = datapack.name, function = body)
		} as FunctionWithMacros<T>
	}
}

/** [DataPack] has no `equals`/`hashCode` override, so this keys on instance identity. */
private val runtimes = mutableMapOf<DataPack, DynamicStringRuntime>()

/**
 * Registers the shared infrastructure required by every [DynamicString] (load hook + scoreboard
 * objective) and returns the lazy [DynamicStringRuntime] used by every helper to self-register.
 *
 * Call this exactly once per datapack, before any `DynamicString` / `KoreStringList` helper. Pass a
 * custom [config] to swap the underlying storage namespace / heap root / scoreboard objective.
 */
fun DataPack.registerDynamicStrings(config: DynamicStringConfig = DynamicStringConfig()): DynamicStringRuntime =
	runtimes.getOrPut(this) {
		load {
			scoreboard {
				objectives {
					add(config.lengthObjective)
				}
			}
		}
		DynamicStringRuntime(this, config)
	}

/** Returns the runtime previously created by [registerDynamicStrings] or throws if missing. */
internal fun DataPack.requireDynamicStringRuntime(): DynamicStringRuntime =
	runtimes[this] ?: error("registerDynamicStrings() must be called before any DynamicString helper.")

/** Full path of an internal scratch slot inside the kore string storage. */
internal fun DataPack.tmpPath(name: String): String = requireDynamicStringRuntime().tmpPath(name)

/** Full path of the macro args compound for the given helper. */
internal fun DataPack.argsPath(helper: String): String = requireDynamicStringRuntime().argsPath(helper)

/** Top-level shortcut reading through the current active runtime. Prefer the [DataPack] receiver variants. */
internal fun tmpPath(name: String): String = "${OopConstants.stringTmpRoot}.$name"

/** Top-level shortcut reading through the current active runtime. Prefer the [DataPack] receiver variants. */
internal fun argsPath(helper: String): String = "${OopConstants.stringArgsRoot}.$helper"
