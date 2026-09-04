package io.github.ayfri.kore.optimization

import io.github.ayfri.kore.DataPack

/**
 * A whole-pack transformation applied to a finished [DataPack], right before it is written.
 *
 * Passes are collected in [Optimization.passes] and run in order, so a pass may rely on what the previous ones did.
 * They mutate the pack in place, which means they only see the final state of the pack and never a half-built one.
 *
 * Docs: https://kore.ayfri.com/docs/guides/optimization
 */
interface DataPackPass {
	/** Identifier used in the summary line printed when [Optimization.verbose] is on, e.g. `prune-empty-functions`. */
	val name: String

	fun run(dataPack: DataPack): PassResult
}

/**
 * Outcome of a single [DataPackPass] run.
 *
 * @property changes how many things the pass touched, `0` meaning it did nothing and stays silent
 * @property summary human-readable line printed when [Optimization.verbose] is on, e.g. `pruned 12 empty functions`
 */
data class PassResult(val changes: Int, val summary: String? = null) {
	companion object {
		val NONE = PassResult(0)
	}
}
