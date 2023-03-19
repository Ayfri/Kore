package commands.execute

import arguments.Argument
import arguments.Vec3
import arguments.enums.DataType
import arguments.float
import arguments.literal
import commands.asArg

class ExecuteStore(private val ex: Execute) {
	fun block(
		pos: Vec3,
		path: String,
		type: DataType,
		scale: Double,
	) = listOf(literal("block"), pos, literal(path), literal(type.asArg()), float(scale))

	fun bossBarMax(id: String) = listOf(literal("bossbar"), literal(id), literal("max"))
	fun bossBarValue(id: String) = listOf(literal("bossbar"), literal(id), literal("value"))

	fun entity(target: Argument.Entity, path: String, type: DataType, scale: Double) = listOf(
		literal("entity"), ex.targetArg(target),
		literal(path),
		literal(type.asArg()),
		float(scale)
	)

	fun score(target: Argument.ScoreHolder, objective: String) = listOf(literal("score"), ex.targetArg(target), literal(objective))

	fun storage(target: Argument.Storage, path: String, type: DataType, scale: Double) = listOf(
		literal("storage"), target,
		literal(path),
		literal(type.asArg()),
		float(scale)
	)
}
