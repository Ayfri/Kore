package io.github.ayfri.kore.commands.execute

import io.github.ayfri.kore.arguments.enums.DataType
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.StorageArgument
import io.github.ayfri.kore.utils.asArg

class ExecuteStore(private val ex: Execute) {
	fun block(
		pos: Vec3,
		path: String,
		type: DataType,
		scale: Double,
	) = listOf(literal("block"), pos, literal(path), literal(type.asArg()), float(scale))

	fun bossBarMax(id: String) = listOf(literal("bossbar"), literal(id), literal("max"))
	fun bossBarValue(id: String) = listOf(literal("bossbar"), literal(id), literal("value"))

	fun entity(target: EntityArgument, path: String, type: DataType, scale: Double) = listOf(
		literal("entity"), ex.targetArg(target),
		literal(path),
		literal(type.asArg()),
		float(scale)
	)

	fun score(target: ScoreHolderArgument, objective: String) = listOf(literal("score"), ex.targetArg(target), literal(objective))

	fun storage(target: StorageArgument, path: String, type: DataType, scale: Double) = listOf(
		literal("storage"), target,
		literal(path),
		literal(type.asArg()),
		float(scale)
	)
}
