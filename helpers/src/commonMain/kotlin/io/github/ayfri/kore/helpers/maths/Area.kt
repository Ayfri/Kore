package io.github.ayfri.kore.helpers.maths

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.PosNumber
import io.github.ayfri.kore.arguments.numbers.pos

/** An axis-aligned box between [first] and [second], both corners in whichever coordinate space they were built with. */
data class Area(var first: Vec3, var second: Vec3 = first) {
	/** Builds an area between world-coordinate corners [x1,y1,z1] and [x2,y2,z2]. */
	constructor(x1: Number, y1: Number, z1: Number, x2: Number, y2: Number, z2: Number) : this(
		Vec3(x1, y1, z1),
		Vec3(x2, y2, z2)
	)

	/** Builds an area between corners [x1,y1,z1] and [x2,y2,z2], preserving each [PosNumber]'s coordinate space. */
	constructor(x1: PosNumber, y1: PosNumber, z1: PosNumber, x2: PosNumber, y2: PosNumber, z2: PosNumber) : this(
		Vec3(x1, y1, z1),
		Vec3(x2, y2, z2)
	)

	/** X component of [first]. */
	val x1 get() = first.x

	/** Y component of [first]. */
	val y1 get() = first.y

	/** Z component of [first]. */
	val z1 get() = first.z

	/** X component of [second]. */
	val x2 get() = second.x

	/** Y component of [second]. */
	val y2 get() = second.y

	/** Z component of [second]. */
	val z2 get() = second.z

	/** X component of the midpoint between [first] and [second]. */
	val xCenter get() = (x1 + x2) / 2

	/** Y component of the midpoint between [first] and [second]. */
	val yCenter get() = (y1 + y2) / 2

	/** Z component of the midpoint between [first] and [second]. */
	val zCenter get() = (z1 + z2) / 2

	/** Midpoint between [first] and [second]. */
	val center get() = Vec3(xCenter, yCenter, zCenter)

	/** Extent along X, [x2] minus [x1]. */
	val xSize get() = x2 - x1

	/** Extent along Y, [y2] minus [y1]. */
	val ySize get() = y2 - y1

	/** Extent along Z, [z2] minus [z1]. */
	val zSize get() = z2 - z1

	/** Extent along each axis, [second] minus [first]. */
	val size get() = Vec3(xSize, ySize, zSize)

	/** Half of [xSize]. */
	val xRadius get() = xSize / 2

	/** Half of [ySize]. */
	val yRadius get() = ySize / 2

	/** Half of [zSize]. */
	val zRadius get() = zSize / 2

	/** Half of [size], on each axis. */
	val radius get() = Vec3(xRadius, yRadius, zRadius)

	/** Whether [vec3] falls within this area on every axis. */
	operator fun contains(vec3: Vec3) = vec3.x in x1..x2 && vec3.y in y1..y2 && vec3.z in z1..z2

	/** Whether both corners of [area] fall within this area. */
	operator fun contains(area: Area) = area.first in this && area.second in this

	/** Translates both corners by [vec3]. */
	fun move(vec3: Vec3) = Area(first + vec3, second + vec3)

	/** Grows the area by [vec3] on every axis, symmetrically around [center]. */
	fun expand(vec3: Vec3) = Area(first - vec3, second + vec3)

	/** Grows the area by [factor] on every axis, symmetrically around [center]. */
	fun expand(factor: Number) = Area(first - factor, second + factor)

	/** Grows the area by [x], [y], [z] on their respective axes, symmetrically around [center]. */
	fun expand(x: Number, y: Number, z: Number) = Area(Vec3(x1 - x, y1 - y, z1 - z), Vec3(x2 + x, y2 + y, z2 + z))

	/** Shrinks the area by [vec3] on every axis, symmetrically around [center]. */
	fun contract(vec3: Vec3) = Area(first + vec3, second - vec3)

	/** Shrinks the area by [factor] on every axis, symmetrically around [center]. */
	fun contract(factor: Number) = Area(first + factor, second - factor)

	/** Shrinks the area by [x], [y], [z] on their respective axes, symmetrically around [center]. */
	fun contract(x: Number, y: Number, z: Number) = Area(Vec3(x1 + x, y1 + y, z1 + z), Vec3(x2 - x, y2 - y, z2 - z))

	/** Returns the overlapping region between this area and [area]. */
	fun intersect(area: Area) = Area(first max area.first, second min area.second)

	/** Returns the smallest area covering both this area and [area]. */
	fun union(area: Area) = Area(first min area.first, second max area.second)
}

/** Creates an [Area] between two [Vec3] corners. */
operator fun Vec3.rangeTo(other: Vec3) = Area(this, other)

/** Translates the area by [vec3]. Alias for [Area.move]. */
operator fun Area.plus(vec3: Vec3) = move(vec3)

/** Translates the area by `-vec3`. Alias for [Area.move] with a negated vector. */
operator fun Area.minus(vec3: Vec3) = move(-vec3)

/** Creates an empty area at [0,0,0]. */
fun area() = Area(vec3(), vec3())

/** Creates an area with the size [xSize, ySize, zSize] at [0,0,0], both corners relative (`~`). */
fun area(xSize: Number, ySize: Number, zSize: Number) = Area(vec3(), Vec3(pos(xSize), pos(ySize), pos(zSize)))

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
