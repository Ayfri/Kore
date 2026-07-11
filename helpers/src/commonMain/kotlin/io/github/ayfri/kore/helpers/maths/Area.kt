package io.github.ayfri.kore.helpers.maths

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.PosNumber

data class Area(var first: Vec3, var second: Vec3 = first) {
	constructor(x1: Number, y1: Number, z1: Number, x2: Number, y2: Number, z2: Number) : this(
		Vec3(x1, y1, z1),
		Vec3(x2, y2, z2)
	)

	constructor(x1: PosNumber, y1: PosNumber, z1: PosNumber, x2: PosNumber, y2: PosNumber, z2: PosNumber) : this(
		Vec3(x1, y1, z1),
		Vec3(x2, y2, z2)
	)

	val x1 get() = first.x
	val y1 get() = first.y
	val z1 get() = first.z

	val x2 get() = second.x
	val y2 get() = second.y
	val z2 get() = second.z

	val xCenter get() = (x1 + x2) / 2
	val yCenter get() = (y1 + y2) / 2
	val zCenter get() = (z1 + z2) / 2

	val center get() = Vec3(xCenter, yCenter, zCenter)

	val xSize get() = x2 - x1
	val ySize get() = y2 - y1
	val zSize get() = z2 - z1

	val size get() = Vec3(xSize, ySize, zSize)

	val xRadius get() = xSize / 2
	val yRadius get() = ySize / 2
	val zRadius get() = zSize / 2

	val radius get() = Vec3(xRadius, yRadius, zRadius)

	operator fun contains(vec3: Vec3) = vec3.x in x1..x2 && vec3.y in y1..y2 && vec3.z in z1..z2
	operator fun contains(area: Area) = area.first in this && area.second in this

	fun move(vec3: Vec3) = Area(first + vec3, second + vec3)
	fun expand(vec3: Vec3) = Area(first - vec3, second + vec3)
	fun expand(factor: Number) = Area(first - factor, second + factor)
	fun expand(x: Number, y: Number, z: Number) = Area(Vec3(x1 - x, y1 - y, z1 - z), Vec3(x2 + x, y2 + y, z2 + z))
	fun contract(vec3: Vec3) = Area(first + vec3, second - vec3)
	fun contract(factor: Number) = Area(first + factor, second - factor)
	fun contract(x: Number, y: Number, z: Number) = Area(Vec3(x1 + x, y1 + y, z1 + z), Vec3(x2 - x, y2 - y, z2 - z))
	fun intersect(area: Area) = Area(first max area.first, second min area.second)
	fun union(area: Area) = Area(first min area.first, second max area.second)
}

operator fun Vec3.rangeTo(other: Vec3) = Area(this, other)
operator fun Area.plus(vec3: Vec3) = move(vec3)
operator fun Area.minus(vec3: Vec3) = move(-vec3)

/** Creates an empty area at [0,0,0]. */
fun area() = Area(vec3(), vec3())

/** Creates an area with the size [xSize, ySize, zSize] at [0,0,0]. */
fun area(xSize: Number, ySize: Number, zSize: Number) = Area(vec3(), vec3(xSize, ySize, zSize))

/** Creates an area at [x1,y1,z1] and ends at [x2,y2,z2]. */
fun area(x1: Number, y1: Number, z1: Number, x2: Number, y2: Number, z2: Number) =
	Area(vec3(x1, y1, z1), vec3(x2, y2, z2))

/** Creates an area at [x1,y1,z1] with size [xSize, ySize, zSize]. */
fun area(x1: PosNumber, y1: PosNumber, z1: PosNumber, xSize: PosNumber, ySize: PosNumber, zSize: PosNumber) =
	Area(vec3(x1, y1, z1), vec3(x1 + xSize, y1 + ySize, z1 + zSize))

/** Creates an area at [first.x, first.y, first.z] and ends at [second.x, second.y, second.z]. */
fun area(first: Vec3, second: Vec3) = Area(first, second)

/** Creates an area at [first.x, first.y, first.z] with size [size, size, size]. */
fun area(first: Vec3, size: Number) = Area(first, first + size)

/** Creates an area at [first.x, first.y, first.z] with size [sizeX, sizeY, sizeZ]. */
fun area(first: Vec3, sizeX: Number, sizeY: Number, sizeZ: Number) = Area(first, first + vec3(sizeX, sizeY, sizeZ))
