import arguments.enums.Axis
import arguments.maths.vec2
import arguments.maths.vec3
import arguments.numbers.PosNumber
import arguments.numbers.pos
import arguments.numbers.relativePos
import assertions.assertsIs
import kotlin.math.sqrt

fun vec3Tests() {
	vec3() assertsIs vec3(0.relativePos, 0.relativePos, 0.relativePos)
	vec3() assertsIs vec3(PosNumber.Type.RELATIVE)
	vec3(0, 0, 0) assertsIs vec3().world

	val point1 = vec3(PosNumber.Type.WORLD)
	val point2 = vec3(2, 2, 2)
	val point3 = vec3(5, 5, 5)
	val point4 = vec3(2, 5, 10)

	point1.x.value assertsIs 0.0
	point1.y.value assertsIs 0.0
	point1.z.value assertsIs 0.0

	point1.array.toList() assertsIs listOf(0.0, 0.0, 0.0)
	point2.length assertsIs sqrt(point2.lengthSquared)
	point2.lengthSquared assertsIs 12.0
	point2.manhattanLength assertsIs 6.0
	point2.values assertsIs listOf(2.pos, 2.pos, 2.pos)

	point1 + point2 assertsIs vec3(2, 2, 2)
	point2 * point2 assertsIs vec3(4, 4, 4)
	point1 - point2 assertsIs vec3(-2, -2, -2)
	point2 / point2 assertsIs vec3(1, 1, 1)

	point1 + 2 assertsIs vec3(2, 2, 2)
	point2 * 2 assertsIs vec3(4, 4, 4)
	point1 - 2 assertsIs vec3(-2, -2, -2)
	point2 / 2 assertsIs vec3(1, 1, 1)

	point1[Axis.X].value assertsIs 0.0
	point1[Axis.Y].value assertsIs 0.0
	point1[Axis.Z].value assertsIs 0.0

	val point5 = point1.set(Axis.X, -1.5)
	point5[Axis.X].value assertsIs -1.5

	point5.abs() assertsIs vec3(1.5, 0, 0)
	point5.ceil() assertsIs vec3(-1, 0, 0)
	point5.floor() assertsIs vec3(-2, 0, 0)
	point5.negate() assertsIs vec3(1.5, 0, 0)
	point5.normalize() assertsIs vec3(-1, 0, 0)
	point5.round() assertsIs vec3(-1, 0, 0)

	val angleTo = vec3(5, 5, 0).angleTo(point3)
	val angleInRadians = Math.toRadians(35.264)
	(angleTo - angleInRadians < 0.0001) assertsIs true

	point3.cross(point4) assertsIs vec3(25, -40, 15)
	point1.distanceTo(point2) assertsIs sqrt(12.0)
	point1.distanceSquaredTo(point2) assertsIs 12.0
	point2.dot(point3) assertsIs 30
	point1.manhattanDistanceTo(point2) assertsIs -6.0
	point3.max(point4) assertsIs vec3(5, 5, 10)
	point3.min(point4) assertsIs vec3(2, 5, 5)

	point3.toVec2() assertsIs vec2(5, 5)
}
