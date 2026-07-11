package io.github.ayfri.kore.helpers.displays

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.numbers.TimeNumber
import io.github.ayfri.kore.arguments.types.literals.randomUUID
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.summon
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.entities.display.BlockDisplayEntity
import io.github.ayfri.kore.entities.display.ItemDisplayEntity
import io.github.ayfri.kore.entities.display.TextDisplayEntity
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.helpers.displays.entities.BlockDisplay
import io.github.ayfri.kore.helpers.displays.entities.DisplayEntity
import io.github.ayfri.kore.helpers.displays.entities.ItemDisplay
import io.github.ayfri.kore.helpers.displays.entities.TextDisplay
import io.github.ayfri.kore.helpers.displays.maths.Transformation
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.StringifiedNbt
import net.benwoodworth.knbt.encodeToNbtTag
import net.benwoodworth.knbt.nbtList

data class DisplayEntityInterpolable(val entity: DisplayEntity, var lastPosition: Vec3) {
	val selector = randomUUID()
	var isSummoned = false

	context(fn: Function)
	fun interpolateTo(
		duration: Int = entity.interpolationDuration ?: 0,
		start: Int = entity.startInterpolation ?: 0,
		transformation: Transformation,
	): Command {
		val nbtCompound = nbt {
			if (entity.interpolationDuration != duration) this["interpolation_duration"] = duration
			this["start_interpolation"] = start
			this["transformation"] = StringifiedNbt.encodeToNbtTag(transformation).nbtList
		}

		entity.interpolationDuration = duration
		entity.startInterpolation = start
		entity.transformation = transformation

		return fn.data(selector).merge(nbtCompound)
	}

	context(fn: Function)
	fun interpolateTo(
		duration: TimeNumber,
		start: Int = entity.startInterpolation ?: 0,
		transformation: Transformation,
	): Command = interpolateTo(duration.inTicks().value.toInt(), start, transformation)

	context(fn: Function)
	fun interpolateTo(
		duration: Int = entity.interpolationDuration ?: 0,
		start: Int = entity.startInterpolation ?: 0,
		block: Transformation.() -> Unit,
	) = interpolateTo(duration, start, Transformation().apply(block))

	context(fn: Function)
	fun interpolateTo(
		duration: TimeNumber,
		start: Int = entity.startInterpolation ?: 0,
		block: Transformation.() -> Unit,
	) = interpolateTo(duration, start, Transformation().apply(block))

	context(fn: Function)
	fun summon(position: Vec3 = lastPosition) {
		if (isSummoned) error("Entity already summoned")

		val nbt = entity.toNbt().toMutableMap()
		nbt["UUID"] = selector.toNBTIntArray()
		fn.summon(entity.entityType, position, NbtCompound(nbt))
		lastPosition = position
		isSummoned = true
	}
}

fun DisplayEntity.interpolable(position: Vec3) = DisplayEntityInterpolable(this, position)

/**
 * Returns a typed OOP entity handle for this interpolable.
 *
 * The returned entity targets this specific spawned instance by UUID, giving access to all [Entity] extension functions.
 */
fun DisplayEntityInterpolable.toEntity(): Entity = when (entity) {
	is BlockDisplay -> BlockDisplayEntity(selector)
	is ItemDisplay -> ItemDisplayEntity(selector)
	is TextDisplay -> TextDisplayEntity(selector)
}
