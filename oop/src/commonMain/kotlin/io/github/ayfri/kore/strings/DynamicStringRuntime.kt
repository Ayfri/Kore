package io.github.ayfri.kore.strings

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.types.resources.StorageArgument
import io.github.ayfri.kore.arguments.types.resources.storage
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.FunctionWithMacros
import io.github.ayfri.kore.functions.Macros
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.load

/** Name prefix reserved for the scratch slots the module allocates for itself. */
internal const val INTERNAL_NAME_PREFIX = "kore_string_"

/**
 * Configuration knobs for the Kore dynamic string runtime. Every string helper routes its
 * reads / writes through the values exposed here, so swapping the storage namespace, the NBT roots
 * or the backing objective is a single-call change.
 *
 * Defaults mirror the values stored on [OopConstants], which are themselves mutable if you would
 * rather change them globally than per datapack.
 */
data class DynamicStringConfig(
	val argsRoot: String = OopConstants.stringArgsRoot,
	val heapRoot: String = OopConstants.stringHeapRoot,
	val lengthHolder: String = OopConstants.stringLengthHolder,
	val lengthObjective: String = OopConstants.stringLengthObjective,
	val listsRoot: String = OopConstants.stringListsRoot,
	val storageName: String = OopConstants.stringStorageName,
	val storageNamespace: String = OopConstants.stringStorageNamespace,
	val tablesRoot: String = OopConstants.stringTablesRoot,
	val tmpRoot: String = OopConstants.stringTmpRoot,
	val trimWhitespace: List<String> = OopConstants.stringTrimWhitespace,
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
	internal val libStorageArg: StorageArgument = storage(config.storageName, config.storageNamespace)
	internal val libStorage: String = libStorageArg.asString()

	private val allocatedListNames = mutableSetOf<String>()
	private val allocatedStringNames = mutableSetOf<String>()
	private var anonymousCounter = 0
	private var tempCounter = 0
	private val loadedKeys = mutableSetOf<String>()
	private val registered = mutableMapOf<String, FunctionWithMacros<*>>()

	internal fun allocateList(name: String) {
		requireUserName(name, "KoreStringList")
		require(allocatedListNames.add(name)) { "KoreStringList name '$name' is already allocated in this datapack." }
	}

	internal fun allocateString(name: String) {
		requireUserName(name, "DynamicString")
		require(allocatedStringNames.add(name)) { "DynamicString name '$name' is already allocated in this datapack." }
	}

	internal fun argsPath(helper: String): String = "${config.argsRoot}.$helper"
	internal fun heapPath(name: String): String = "${config.heapRoot}.$name"
	internal fun listsPath(name: String): String = "${config.listsRoot}.$name"
	internal fun nextAnonymousId(): Int = anonymousCounter++
	internal fun nextTempId(): Int = tempCounter++
	internal fun tablesPath(name: String): String = "${config.tablesRoot}.$name"
	internal fun tmpPath(name: String): String = "${config.tmpRoot}.$name"

	/** Builds a handle on one of the module's own scratch slots, bypassing the user-facing name allocation. */
	internal fun scratchList(name: String) = KoreStringList(name, this)

	/** Builds a handle on one of the module's own scratch slots, bypassing the user-facing name allocation. */
	internal fun scratchString(name: String) = DynamicString(name, this)

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

	/** Registers [body] in the load tag once per [key], used by helpers needing world-load initialisation. */
	internal fun ensureLoaded(key: String, body: Function.() -> Unit) {
		if (!loadedKeys.add(key)) return
		datapack.load(name = "${key}_init", namespace = datapack.name, block = body)
	}

	private fun requireUserName(name: String, kind: String) = require(!name.startsWith(INTERNAL_NAME_PREFIX)) {
		"$kind name '$name' uses the reserved '$INTERNAL_NAME_PREFIX' prefix, which the string module keeps for its own scratch slots."
	}
}

/** [DataPack] has no `equals`/`hashCode` override, so this keys on instance identity. */
private val runtimes = mutableMapOf<DataPack, DynamicStringRuntime>()

/**
 * Registers the shared infrastructure required by every [DynamicString] (load hook + scoreboard
 * objective) and returns the lazy [DynamicStringRuntime] used by every helper to self-register.
 *
 * Call this exactly once per datapack, before any `DynamicString` / `KoreStringList` helper. Pass a
 * custom [config] to swap the underlying storage namespace / NBT roots / scoreboard objective.
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
