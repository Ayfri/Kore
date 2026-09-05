package io.github.ayfri.kore.helpers.maths

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.scores.ScoreboardCriteria
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.helpers.HelpersConstants
import io.github.ayfri.kore.helpers.state.ScoreboardDelegate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private val initializedMathModules = mutableSetOf<String>()

const val MATH_SCALE = 1000
const val MATH_TABLE_SIZE = 360

data class MathHandle(val objective: String) {
	context(fn: Function)
	private fun normalizeAngle(entity: Entity, score: String) {
		val sel = entity.asSelector()

		fn.scoreboard {
			players {
				operation(sel, score, Operation.MODULO, literal(HelpersConstants.mathConst360), objective)
				operation(sel, score, Operation.ADD, literal(HelpersConstants.mathConst360), objective)
				operation(sel, score, Operation.MODULO, literal(HelpersConstants.mathConst360), objective)
			}
		}
	}

	context(fn: Function)
	fun cos(entity: Entity, inputScore: String, outputScore: String) {
		val sel = entity.asSelector()

		fn.scoreboard {
			players {
				operation(sel, outputScore, Operation.SET, sel, inputScore)
			}
		}

		normalizeAngle(entity, outputScore)

		for (deg in 0 until MATH_TABLE_SIZE) {
			val cosVal = (cos(PI * deg / 180.0) * MATH_SCALE).roundToInt()
			fn.execute {
				ifCondition {
					score(sel, outputScore, rangeOrInt(deg))
				}
				run {
					scoreboard {
						players {
							set(sel, outputScore, cosVal)
						}
					}
				}
			}
		}
	}

	context(fn: Function)
	fun cos(input: ScoreboardDelegate, output: ScoreboardDelegate) {
		cos(input.entity, input.objectiveName, output.objectiveName)
	}

	context(fn: Function)
	fun distanceSquared(
		entity: Entity,
		x1: String, y1: String, z1: String,
		x2: String, y2: String, z2: String,
		outputScore: String,
	) {
		val sel = entity.asSelector()
		val dx = "_dist_dx"
		val dy = "_dist_dy"
		val dz = "_dist_dz"

		fn.scoreboard {
			players {
				operation(sel, dx, Operation.SET, sel, x2)
				operation(sel, dx, Operation.REMOVE, sel, x1)

				operation(sel, dy, Operation.SET, sel, y2)
				operation(sel, dy, Operation.REMOVE, sel, y1)

				operation(sel, dz, Operation.SET, sel, z2)
				operation(sel, dz, Operation.REMOVE, sel, z1)

				operation(sel, outputScore, Operation.SET, sel, dx)
				operation(sel, outputScore, Operation.MULTIPLY, sel, dx)

				operation(sel, dy, Operation.MULTIPLY, sel, dy)
				operation(sel, outputScore, Operation.ADD, sel, dy)

				operation(sel, dz, Operation.MULTIPLY, sel, dz)
				operation(sel, outputScore, Operation.ADD, sel, dz)
			}
		}
	}

	context(fn: Function)
	fun distanceSquared(
		first: Triple<ScoreboardDelegate, ScoreboardDelegate, ScoreboardDelegate>,
		second: Triple<ScoreboardDelegate, ScoreboardDelegate, ScoreboardDelegate>,
		output: ScoreboardDelegate,
	) {
		distanceSquared(
			output.entity,
			first.first.objectiveName,
			first.second.objectiveName,
			first.third.objectiveName,
			second.first.objectiveName,
			second.second.objectiveName,
			second.third.objectiveName,
			output.objectiveName,
		)
	}

	context(fn: Function)
	fun parabola(
		entity: Entity,
		tScore: String,
		initialVelocity: String,
		gravity: String,
		outputScore: String,
	) {
		val sel = entity.asSelector()
		val tempA = "_para_a"
		val tempB = "_para_b"
		val two = HelpersConstants.mathConst2

		fn.scoreboard {
			players {
				operation(sel, tempA, Operation.SET, literal(initialVelocity), objective)
				operation(sel, tempA, Operation.MULTIPLY, sel, tScore)

				operation(sel, tempB, Operation.SET, literal(gravity), objective)
				operation(sel, tempB, Operation.MULTIPLY, sel, tScore)
				operation(sel, tempB, Operation.MULTIPLY, sel, tScore)

				operation(sel, tempB, Operation.DIVIDE, literal(two), objective)

				operation(sel, outputScore, Operation.SET, sel, tempA)
				operation(sel, outputScore, Operation.REMOVE, sel, tempB)
			}
		}
	}

	context(fn: Function)
	fun parabola(
		time: ScoreboardDelegate,
		initialVelocity: ScoreboardDelegate,
		gravity: ScoreboardDelegate,
		output: ScoreboardDelegate,
	) {
		val outputSelector = output.entity.asSelector()
		val timeSelector = time.entity.asSelector()
		val velocitySelector = initialVelocity.entity.asSelector()
		val gravitySelector = gravity.entity.asSelector()
		val tempA = "_para_a"
		val tempB = "_para_b"
		val two = HelpersConstants.mathConst2

		fn.scoreboard {
			players {
				operation(outputSelector, tempA, Operation.SET, velocitySelector, initialVelocity.objectiveName)
				operation(outputSelector, tempA, Operation.MULTIPLY, timeSelector, time.objectiveName)

				operation(outputSelector, tempB, Operation.SET, gravitySelector, gravity.objectiveName)
				operation(outputSelector, tempB, Operation.MULTIPLY, timeSelector, time.objectiveName)
				operation(outputSelector, tempB, Operation.MULTIPLY, timeSelector, time.objectiveName)

				operation(outputSelector, tempB, Operation.DIVIDE, literal(two), objective)

				operation(outputSelector, output.objectiveName, Operation.SET, outputSelector, tempA)
				operation(outputSelector, output.objectiveName, Operation.REMOVE, outputSelector, tempB)
			}
		}
	}

	context(fn: Function)
	fun sin(entity: Entity, inputScore: String, outputScore: String) {
		val sel = entity.asSelector()

		fn.scoreboard {
			players {
				operation(sel, outputScore, Operation.SET, sel, inputScore)
			}
		}

		normalizeAngle(entity, outputScore)

		for (deg in 0 until MATH_TABLE_SIZE) {
			val sinVal = (sin(PI * deg / 180.0) * MATH_SCALE).roundToInt()
			fn.execute {
				ifCondition {
					score(sel, outputScore, rangeOrInt(deg))
				}
				run {
					scoreboard {
						players {
							set(sel, outputScore, sinVal)
						}
					}
				}
			}
		}
	}

	context(fn: Function)
	fun sin(input: ScoreboardDelegate, output: ScoreboardDelegate) {
		sin(input.entity, input.objectiveName, output.objectiveName)
	}

	context(fn: Function)
	fun sqrt(entity: Entity, inputScore: String, outputScore: String) {
		val sel = entity.asSelector()
		val tempX = "_sqrt_x"
		val tempG = "_sqrt_g"
		val two = HelpersConstants.mathConst2

		fn.scoreboard {
			players {
				operation(sel, tempX, Operation.SET, sel, inputScore)
				operation(sel, tempG, Operation.SET, sel, inputScore)
				operation(sel, tempG, Operation.DIVIDE, literal(two), objective)
				add(sel, tempG, 1)
			}
		}

		repeat(HelpersConstants.mathSqrtIterations) {
			fn.scoreboard {
				players {
					operation(sel, outputScore, Operation.SET, sel, tempX)
					operation(sel, outputScore, Operation.DIVIDE, sel, tempG)
					operation(sel, outputScore, Operation.ADD, sel, tempG)
					operation(sel, outputScore, Operation.DIVIDE, literal(two), objective)
					operation(sel, tempG, Operation.SET, sel, outputScore)
				}
			}
		}
	}

	context(fn: Function)
	fun sqrt(input: ScoreboardDelegate, output: ScoreboardDelegate) {
		sqrt(input.entity, input.objectiveName, output.objectiveName)
	}
}

context(_: Function, math: MathHandle)
infix fun ScoreboardDelegate.cosTo(output: ScoreboardDelegate) {
	math.cos(this, output)
}

context(_: Function, math: MathHandle)
infix fun ScoreboardDelegate.sinTo(output: ScoreboardDelegate) {
	math.sin(this, output)
}

context(_: Function, math: MathHandle)
infix fun ScoreboardDelegate.sqrtTo(output: ScoreboardDelegate) {
	math.sqrt(this, output)
}

fun DataPack.registerMath(objective: String = HelpersConstants.mathObjective): MathHandle {
	val key = "$name:$objective"
	if (key !in initializedMathModules) {
		initializedMathModules += key

		load(HelpersConstants.mathInitFunction) {
			scoreboard {
				objectives {
					add(objective, ScoreboardCriteria.DUMMY)
				}
			}

			scoreboard {
				players {
					set(literal(HelpersConstants.mathConst2), objective, 2)
					set(literal(HelpersConstants.mathConst360), objective, MATH_TABLE_SIZE)
					set(literal(HelpersConstants.mathConstScale), objective, MATH_SCALE)
				}
			}
		}
	}

	return MathHandle(objective)
}
