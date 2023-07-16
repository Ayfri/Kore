package helpers.displays

import arguments.maths.Vec3
import arguments.numbers.TimeNumber
import arguments.types.literals.randomUUID
import commands.Command
import commands.data
import commands.summon
import functions.Function
import helpers.displays.entities.DisplayEntity
import helpers.displays.maths.Transformation
import net.benwoodworth.knbt.*
import utils.set

class DisplayEntityInterpolable(val entity: DisplayEntity, var lastPosition: Vec3) {
	val selector = randomUUID()
	var isSummoned = false

	context(Function)
	fun interpolateTo(
		duration: Int = entity.interpolationDuration ?: 0,
		start: Int = entity.startInterpolation ?: 0,
		transformation: Transformation
	): Command {
		val nbtCompound = buildNbtCompound {
			if (entity.interpolationDuration != duration) this["interpolation_duration"] = duration
			this["start_interpolation"] = start
			this["transformation"] = StringifiedNbt.encodeToNbtTag(transformation).nbtList
		}

		entity.interpolationDuration = duration
		entity.startInterpolation = start
		entity.transformation = transformation

		return data(selector).merge(nbtCompound)
	}

	context(Function)
	fun interpolateTo(
		duration: TimeNumber,
		start: Int = entity.startInterpolation ?: 0,
		transformation: Transformation
	): Command = interpolateTo(duration.inTicks().value.toInt(), start, transformation)

	context(Function)
	fun interpolateTo(
		duration: Int = entity.interpolationDuration ?: 0,
		start: Int = entity.startInterpolation ?: 0,
		block: Transformation.() -> Unit
	) = interpolateTo(duration, start, Transformation().apply(block))


	context(Function)
	fun interpolateTo(
		duration: TimeNumber,
		start: Int = entity.startInterpolation ?: 0,
		block: Transformation.() -> Unit
	) = interpolateTo(duration, start, Transformation().apply(block))

	context(Function)
	fun summon(position: Vec3 = lastPosition) {
		if (isSummoned) error("Entity already summoned")

		val nbt = entity.toNbt().toMutableMap()
		nbt["UUID"] = selector.toNBTIntArray()
		summon(entity.entityType, position, NbtCompound(nbt))
		lastPosition = position
		isSummoned = true
	}
}

context(Function)
fun DisplayEntity.interpolable(position: Vec3) = DisplayEntityInterpolable(this, position)
