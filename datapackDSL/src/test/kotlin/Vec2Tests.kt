import arguments.enums.Axis
import arguments.maths.vec2
import arguments.maths.vec3
import arguments.numbers.PosNumber
import arguments.numbers.pos
import arguments.numbers.relativePos
import assertions.assertsIs
import kotlin.math.sqrt

fun vec2Tests() {
	vec2() assertsIs vec2(0.relativePos, 0.relativePos)
	vec2() assertsIs vec2(PosNumber.Type.RELATIVE)
	vec2(0, 0) assertsIs vec2().world

	val point1 = vec2(PosNumber.Type.WORLD)
	val point2 = vec2(2, 2)
	val point3 = vec2(5, 5)
	val point4 = vec2(2, 10)

	point1.x.value assertsIs 0.0
	point1.y.value assertsIs 0.0

	point1.array.toList() assertsIs listOf(0.0, 0.0)
	point2.length assertsIs sqrt(point2.lengthSquared)
	point2.lengthSquared assertsIs 8.0
	point2.manhattanLength assertsIs 4.0
	point2.values assertsIs listOf(2.pos, 2.pos)

	point1 + point2 assertsIs vec2(2, 2)
	point2 * point2 assertsIs vec2(4, 4)
	point1 - point2 assertsIs vec2(-2, -2)
	point2 / point2 assertsIs vec2(1, 1)

	point1 + 2 assertsIs vec2(2, 2)
	point2 * 2 assertsIs vec2(4, 4)
	point1 - 2 assertsIs vec2(-2, -2)
	point2 / 2 assertsIs vec2(1, 1)

	point1[Axis.X].value assertsIs 0.0
	point1[Axis.Y].value assertsIs 0.0

	val point5 = point1.set(Axis.X, -1.5)
	point5[Axis.X].value assertsIs -1.5

	point5.abs() assertsIs vec2(1.5, 0)
	point5.ceil() assertsIs vec2(-1, 0)
	point5.floor() assertsIs vec2(-2, 0)
	point5.negate() assertsIs vec2(1.5, 0)
	point5.normalize() assertsIs vec2(-1.0, 0)
	point5.round() assertsIs vec2(-1, 0)

	val angleTo = vec2(5, 0).angleTo(point3)
	val angleInRadians = Math.toRadians(45.0)
	(angleTo - angleInRadians < 0.0001) assertsIs true

	point3.cross(point4) assertsIs vec2(50, 10)
	point1.distanceTo(point2) assertsIs sqrt(8.0)
	point1.distanceSquaredTo(point2) assertsIs 8.0
	point2.dot(point3) assertsIs 20
	point1.manhattanDistanceTo(point2) assertsIs -4.0
	point3.max(point4) assertsIs vec2(5, 10)
	point3.min(point4) assertsIs vec2(2, 5)

	point3.toVec3() assertsIs vec3(5, 5, 0)
}
