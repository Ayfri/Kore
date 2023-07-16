package commands.execute

import arguments.enums.DataType
import arguments.maths.Vec3
import arguments.types.EntityArgument
import arguments.types.ScoreHolderArgument
import arguments.types.literals.float
import arguments.types.literals.literal
import arguments.types.resources.StorageArgument
import utils.asArg

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
