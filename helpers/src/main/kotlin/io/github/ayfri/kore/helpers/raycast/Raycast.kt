package io.github.ayfri.kore.helpers.raycast

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.PosNumber
import io.github.ayfri.kore.arguments.numbers.localPos
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.execute.Anchor
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.commands.tag
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.helpers.HelpersConstants
import io.github.ayfri.kore.commands.function as functionCommand

private val initializedRaycastObjective = mutableSetOf<String>()

/** Configures how a generated raycast behaves and which callbacks it should trigger. */
data class RaycastConfig(
	var maxDistance: Int = 100,
	var name: String = "raycast",
	var onHitBlock: Function.() -> Unit = {},
	var onMaxDistance: (Function.() -> Unit)? = null,
	var onStep: (Function.() -> Unit)? = null,
	var step: Double = 0.5,
)

/** Holds the generated entry point used to launch a configured raycast. */
data class RaycastHandle(
	val config: RaycastConfig,
	val startFunctionId: String,
) {
	/** Calls the generated start function that begins stepping the ray forward. */
	context(fn: Function)
	fun cast() {
		fn.functionCommand(startFunctionId)
	}
}

/** Ensures the shared scoreboard objective required by raycasts exists once per datapack. */
private fun DataPack.ensureRaycastObjective() {
	val key = name
	if (key in initializedRaycastObjective) return
	initializedRaycastObjective += key

	load(HelpersConstants.raycastInitFunction) {
		scoreboard {
			objectives {
				add(HelpersConstants.raycastObjective)
			}
		}
	}
}

/** Registers a raycast from a fully built [RaycastConfig]. */
fun DataPack.raycast(config: RaycastConfig): RaycastHandle {
	ensureRaycastObjective()

	val stepVec = Vec3(
		PosNumber(0.0, PosNumber.Type.LOCAL),
		PosNumber(0.0, PosNumber.Type.LOCAL),
		config.step.localPos,
	)
	val here = vec3(PosNumber.Type.RELATIVE)

	val onHitFn = generatedFunction(HelpersConstants.raycastHitFunctionName(config.name)) {
		config.onHitBlock.invoke(this)
		tag(self()) { remove(HelpersConstants.raycastTag) }
	}

	val onMaxFn = config.onMaxDistance?.let { callback ->
		generatedFunction(HelpersConstants.raycastMaxFunctionName(config.name)) {
			callback.invoke(this)
			tag(self()) { remove(HelpersConstants.raycastTag) }
		}
	}

	val stepFn = generatedFunction(HelpersConstants.raycastStepFunctionName(config.name)) {
		config.onStep?.invoke(this)

		scoreboard {
			players {
				add(self(), HelpersConstants.raycastObjective, 1)
			}
		}

		execute {
			unlessCondition { block(here, Blocks.AIR) }
			run { functionCommand(onHitFn) }
		}

		execute {
			ifCondition { score(self(), HelpersConstants.raycastObjective, rangeOrInt(config.maxDistance)) }
			run {
				if (onMaxFn != null) functionCommand(onMaxFn)
				else tag(self()) { remove(HelpersConstants.raycastTag) }
			}
		}

		execute {
			ifCondition { entity(self { tag = HelpersConstants.raycastTag }) }
			positioned(stepVec)
			run { functionCommand(asId()) }
		}
	}

	val startFn = generatedFunction(HelpersConstants.raycastStartFunctionName(config.name)) {
		tag(self()) { add(HelpersConstants.raycastTag) }
		scoreboard {
			players {
				set(self(), HelpersConstants.raycastObjective, 0)
			}
		}
		execute {
			anchored(Anchor.EYES)
			positioned(stepVec)
			run { functionCommand(stepFn) }
		}
	}

	return RaycastHandle(config, startFn.asId())
}

/** Registers a raycast by configuring a fresh [RaycastConfig] inline. */
inline fun DataPack.raycast(block: RaycastConfig.() -> Unit) = raycast(RaycastConfig().apply(block))
