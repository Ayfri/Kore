package io.github.ayfri.kore.optimization

import io.github.ayfri.kore.Configuration
import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.optimization.passes.DedupeFunctionsPass
import io.github.ayfri.kore.optimization.passes.HoistConditionsIntoSelectorsPass
import io.github.ayfri.kore.optimization.passes.PruneEmptyFunctionsPass
import io.github.ayfri.kore.optimization.passes.PruneUnreferencedGeneratedFunctionsPass
import io.github.ayfri.kore.optimization.passes.ReorderSelectorArgumentsPass
import io.github.ayfri.kore.optimization.passes.SimplifyExecuteChainsPass
import io.github.ayfri.kore.optimization.passes.WarnUnreachableCodePass

/**
 * Whole-pack optimization settings, disabled by default so generation stays predictable.
 *
 * Docs: https://kore.ayfri.com/docs/guides/optimization
 *
 * @property enabled whether the passes run at all
 * @property verbose whether each pass that changed something prints a summary line
 * @property passes the passes to run, in order, defaulting to every built-in one
 */
data class Optimization(
	var enabled: Boolean = false,
	var verbose: Boolean = true,
	var passes: MutableList<DataPackPass> = defaultPasses(),
) {
	/** Appends a custom pass, running after the ones already registered. */
	operator fun plusAssign(pass: DataPackPass) {
		passes += pass
	}

	/** Removes a built-in pass, by identity or by [DataPackPass.name]. */
	operator fun minusAssign(pass: DataPackPass) {
		passes.removeAll { it == pass || it.name == pass.name }
	}

	companion object {
		fun defaultPasses(): MutableList<DataPackPass> = mutableListOf(
			PruneEmptyFunctionsPass,
			SimplifyExecuteChainsPass,
			HoistConditionsIntoSelectorsPass,
			ReorderSelectorArgumentsPass,
			DedupeFunctionsPass(),
			PruneUnreferencedGeneratedFunctionsPass,
			WarnUnreachableCodePass,
		)
	}
}

/** Enables the optimization passes on this configuration and lets the block tune them. */
fun Configuration.optimization(block: Optimization.() -> Unit = {}) {
	optimization = Optimization(enabled = true).apply(block)
}

/** Runs every enabled pass over the pack, printing a summary line per pass that changed something. */
internal fun DataPack.runOptimizationPasses() {
	val optimization = configuration.optimization
	if (!optimization.enabled) return

	optimization.passes.forEach { pass ->
		val result = pass.run(this)
		if (optimization.verbose && result.changes > 0) println("[kore:${pass.name}] ${result.summary ?: "${result.changes} changes"}")
	}
}
