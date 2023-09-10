package io.github.ayfri.kore.helpers.displays

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.numbers.TimeNumber
import io.github.ayfri.kore.arguments.types.literals.randomUUID
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.summon
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.helpers.displays.entities.DisplayEntity
import io.github.ayfri.kore.helpers.displays.maths.Transformation
import io.github.ayfri.kore.utils.set
import net.benwoodworth.knbt.*

class DisplayEntityInterpolable(val entity: DisplayEntity, var lastPosition: Vec3) {
	val selector = randomUUID()
	var isSummoned = false

	context(Function)
	fun interpolateTo(
		duration: Int = entity.interpolationDuration ?: 0,
		start: Int = entity.startInterpolation ?: 0,
		transformation: Transformation,
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
		transformation: Transformation,
	): Command = interpolateTo(duration.inTicks().value.toInt(), start, transformation)

	context(Function)
	fun interpolateTo(
		duration: Int = entity.interpolationDuration ?: 0,
		start: Int = entity.startInterpolation ?: 0,
		block: Transformation.() -> Unit,
	) = interpolateTo(duration, start, Transformation().apply(block))

	context(Function)
	fun interpolateTo(
		duration: TimeNumber,
		start: Int = entity.startInterpolation ?: 0,
		block: Transformation.() -> Unit,
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
