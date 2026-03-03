package io.github.ayfri.kore.maths

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.scores.ScoreboardCriteria
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private val initializedMathModules = mutableSetOf<String>()

const val MATH_SCALE = 1000
const val MATH_TABLE_SIZE = 360

class MathHandle(val objective: String) {
	context(fn: Function)
	fun cos(entity: Entity, inputScore: String, outputScore: String) {
		val sel = entity.asSelector()

		fn.scoreboard {
			players {
				operation(sel, outputScore, Operation.SET, sel, inputScore)
                operation(sel, outputScore, Operation.MODULO, literal(OopConstants.mathConst360), objective)
			}
		}

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
        val two = OopConstants.mathConst2

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
    fun sin(entity: Entity, inputScore: String, outputScore: String) {
        val sel = entity.asSelector()

        fn.scoreboard {
            players {
                operation(sel, outputScore, Operation.SET, sel, inputScore)
                operation(sel, outputScore, Operation.MODULO, literal(OopConstants.mathConst360), objective)
            }
        }

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
    fun sqrt(entity: Entity, inputScore: String, outputScore: String) {
        val sel = entity.asSelector()
        val tempX = "_sqrt_x"
        val tempG = "_sqrt_g"
        val two = OopConstants.mathConst2

        fn.scoreboard {
            players {
                operation(sel, tempX, Operation.SET, sel, inputScore)
                operation(sel, tempG, Operation.SET, sel, inputScore)
                operation(sel, tempG, Operation.DIVIDE, literal(two), objective)
            }
        }

        repeat(OopConstants.mathSqrtIterations) {
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
}

fun DataPack.registerMath(objective: String = OopConstants.mathObjective): MathHandle {
	val key = "$name:$objective"
	if (key !in initializedMathModules) {
		initializedMathModules += key

        load(OopConstants.mathInitFunction) {
			scoreboard {
				objectives {
					add(objective, ScoreboardCriteria.DUMMY)
				}
			}

			scoreboard {
				players {
                    set(literal(OopConstants.mathConst2), objective, 2)
                    set(literal(OopConstants.mathConst360), objective, MATH_TABLE_SIZE)
                    set(literal(OopConstants.mathConstScale), objective, MATH_SCALE)
				}
			}
		}
	}

	return MathHandle(objective)
}
