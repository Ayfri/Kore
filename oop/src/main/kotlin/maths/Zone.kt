package maths

import arguments.Coordinate
import arguments.numbers.PosNumber

data class Zone(var first: Coordinate, var second: Coordinate = first) {
	constructor(x1: Number, y1: Number, z1: Number, x2: Number, y2: Number, z2: Number) : this(Coordinate(x1, y1, z1), Coordinate(x2, y2, z2))
	constructor(x1: PosNumber, y1: PosNumber, z1: PosNumber, x2: PosNumber, y2: PosNumber, z2: PosNumber) : this(Coordinate(x1, y1, z1), Coordinate(x2, y2, z2))
	
	val x1 get() = first.x
	val y1 get() = first.y
	val z1 get() = first.z
	
	val x2 get() = second.x
	val y2 get() = second.y
	val z2 get() = second.z
	
	val xCenter get() = (x1 + x2) / 2
	val yCenter get() = (y1 + y2) / 2
	val zCenter get() = (z1 + z2) / 2
	
	val center get() = Coordinate(xCenter, yCenter, zCenter)
	
	val xSize get() = x2 - x1
	val ySize get() = y2 - y1
	val zSize get() = z2 - z1
	
	val size get() = Coordinate(xSize, ySize, zSize)
	
	val xRadius get() = xSize / 2
	val yRadius get() = ySize / 2
	val zRadius get() = zSize / 2
	
	val radius get() = Coordinate(xRadius, yRadius, zRadius)
	
	operator fun contains(coordinate: Coordinate) = coordinate.x in x1..x2 && coordinate.y in y1..y2 && coordinate.z in z1..z2
	operator fun contains(zone: Zone) = zone.first in this && zone.second in this
	
	fun move(coordinate: Coordinate) = Zone(first + coordinate, second + coordinate)
	fun expand(coordinate: Coordinate) = Zone(first - coordinate, second + coordinate)
	fun expand(factor: Number) = Zone(first - factor, second + factor)
	fun expand(x: Number, y: Number, z: Number) = Zone(Coordinate(x1 - x, y1 - y, z1 - z), Coordinate(x2 + x, y2 + y, z2 + z))
	fun contract(coordinate: Coordinate) = Zone(first + coordinate, second - coordinate)
	fun contract(factor: Number) = Zone(first + factor, second - factor)
	fun contract(x: Number, y: Number, z: Number) = Zone(Coordinate(x1 + x, y1 + y, z1 + z), Coordinate(x2 - x, y2 - y, z2 - z))
	fun intersect(zone: Zone) = Zone(first max zone.first, second min zone.second)
	fun union(zone: Zone) = Zone(first min zone.first, second max zone.second)
}

operator fun Coordinate.rangeTo(other: Coordinate) = Zone(this, other)
operator fun Zone.plus(coordinate: Coordinate) = move(coordinate)
operator fun Zone.minus(coordinate: Coordinate) = move(-coordinate)
