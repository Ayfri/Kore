package io.github.ayfri.kore.raycast

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
import io.github.ayfri.kore.OopConstants.Raycast as C
import io.github.ayfri.kore.commands.function as functionCommand

private val initializedRaycastObjective = mutableSetOf<String>()

/**
 * Configuration for a raycast.
 *
 * @property name unique identifier, used to derive generated function names.
 * @property maxDistance maximum number of steps before giving up.
 * @property step distance (in blocks) per step along the local Z axis.
 * @property onHitBlock called when the ray hits a non-air block.
 * @property onMaxDistance called when [maxDistance] is reached without a hit (optional).
 * @property onStep called at every step - useful for particles (optional).
 */
data class RaycastConfig(
	var name: String = "raycast",
	var maxDistance: Int = 100,
	var step: Double = 0.5,
	var onHitBlock: Function.() -> Unit = {},
	var onMaxDistance: (Function.() -> Unit)? = null,
	var onStep: (Function.() -> Unit)? = null,
)

/** DSL builder - configures a [RaycastConfig]. */
inline fun raycastConfig(block: RaycastConfig.() -> Unit) = RaycastConfig().apply(block)

/**
 * Handle returned by [raycast] to fire the ray from any function context.
 */
class RaycastHandle(
	val config: RaycastConfig,
	private val startFunctionId: String,
) {
	/** Emits the `function` command to start the raycast from the executing entity's eyes. */
	context(fn: Function)
	fun cast() {
		fn.functionCommand(startFunctionId)
	}
}

private fun DataPack.ensureRaycastObjective() {
	val key = name
	if (key in initializedRaycastObjective) return
	initializedRaycastObjective += key

	load(C.INIT_FUNCTION) {
		scoreboard {
			objectives {
				add(C.OBJECTIVE)
			}
		}
	}
}

/**
 * Registers a raycast in the datapack.
 *
 * The generated functions work by:
 * 1. Tagging the executing entity and resetting a scoreboard counter.
 * 2. From the entity's eyes, stepping forward in local coordinates.
 * 3. At each step checking for a non-air block (hit) or max distance.
 * 4. Recursively calling the step function until a termination condition is met.
 *
 * @return a [RaycastHandle] to fire the ray from any function context.
 */
fun DataPack.raycast(config: RaycastConfig): RaycastHandle {
	ensureRaycastObjective()

	val stepVec = Vec3(
		PosNumber(0.0, PosNumber.Type.LOCAL),
		PosNumber(0.0, PosNumber.Type.LOCAL),
		config.step.localPos,
	)
	val here = vec3(PosNumber.Type.RELATIVE)

	val onHitFn = generatedFunction(C.hitFunctionName(config.name)) {
		config.onHitBlock.invoke(this)
		tag(self()) { remove(C.TAG) }
	}

	val onMaxFn = config.onMaxDistance?.let { callback ->
		generatedFunction(C.maxFunctionName(config.name)) {
			callback.invoke(this)
			tag(self()) { remove(C.TAG) }
		}
	}

	val stepFn = generatedFunction(C.stepFunctionName(config.name)) {
		config.onStep?.invoke(this)

		scoreboard {
			players {
				add(self(), C.OBJECTIVE, 1)
			}
		}

		execute {
			unlessCondition { block(here, Blocks.AIR) }
			run { functionCommand(onHitFn) }
		}

		execute {
			ifCondition { score(self(), C.OBJECTIVE, rangeOrInt(config.maxDistance)) }
			run {
				if (onMaxFn != null) functionCommand(onMaxFn)
				else tag(self()) { remove(C.TAG) }
			}
		}

		execute {
			ifCondition { entity(self { tag = C.TAG }) }
			positioned(stepVec)
			run { functionCommand(asId()) }
		}
	}

	val startFn = generatedFunction(C.startFunctionName(config.name)) {
		tag(self()) { add(C.TAG) }
		scoreboard {
			players {
				set(self(), C.OBJECTIVE, 0)
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

/** DSL shorthand - configures and registers a raycast in one call. */
inline fun DataPack.raycast(block: RaycastConfig.() -> Unit) = raycast(RaycastConfig().apply(block))
