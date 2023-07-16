package arguments.maths

import arguments.numbers.PosNumber
import arguments.numbers.pos

typealias Coordinate = Vec3

fun coordinate(x: Number = 0, y: Number = 0, z: Number = 0) = Vec3(x.pos, y.pos, z.pos)
fun coordinate(x: PosNumber, y: PosNumber, z: PosNumber) = Vec3(x, y, z)
fun coordinate(x: PosNumber.Type, y: PosNumber.Type, z: PosNumber.Type) = Vec3(pos(type = x), pos(type = y), pos(type = z))
fun coordinate(type: PosNumber.Type) = Vec3(pos(type = type), pos(type = type), pos(type = type))
