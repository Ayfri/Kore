package commands

import arguments.*
import functions.Function
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Encoder
import serializers.LowercaseSerializer
import utils.asArg

@Serializable(RenderType.Companion.RenderTypeSerializer::class)
enum class RenderType {
	HEARTS,
	INTEGER;

	companion object {
		val values = values()

		object RenderTypeSerializer : LowercaseSerializer<RenderType>(values)
	}
}

class Objectives(private val fn: Function) {
	fun add(name: String, criteria: String, displayName: ChatComponents? = null) = fn.addLine(
		command(
			"scoreboard", literal("objectives"), literal("add"), literal(name), literal(criteria), displayName?.asJsonArg()
		)
	)

	fun list() = fn.addLine(command("scoreboard", literal("objectives"), literal("list")))
	fun modify(name: String, displayName: ChatComponents) =
		fn.addLine(
			command(
				"scoreboard",
				literal("objectives"),
				literal("modify"),
				literal(name),
				literal("displayname"),
				displayName.asJsonArg()
			)
		)

	fun modify(name: String, renderType: RenderType) =
		fn.addLine(
			command(
				"scoreboard",
				literal("objectives"),
				literal("modify"),
				literal(name),
				literal("rendertype"),
				literal(renderType.asArg())
			)
		)

	fun remove(name: String) =
		fn.addLine(command("scoreboard", literal("objectives"), literal("remove"), literal(name)))

	fun setDisplay(slot: SetDisplaySlot, name: String) =
		fn.addLine(
			command(
				"scoreboard",
				literal("objectives"),
				literal("setdisplay"),
				slot,
				literal(name)
			)
		)
}

class Objective(private val fn: Function, val objective: String) {
	fun player(target: Argument.ScoreHolder) = PlayerObjective(fn, target, objective)

	fun add(criteria: String, displayName: ChatComponents? = null) = create(criteria, displayName)
	fun create(criteria: String, displayName: ChatComponents? = null) = fn.addLine(
		command(
			"scoreboard",
			literal("objectives"),
			literal("add"),
			literal(objective),
			literal(criteria),
			displayName?.asJsonArg()
		)
	)

	fun modify(displayName: ChatComponents) = fn.addLine(
		command(
			"scoreboard",
			literal("objectives"),
			literal("modify"),
			literal(objective),
			literal("displayname"),
			displayName.asJsonArg()
		)
	)

	fun modify(renderType: RenderType) = fn.addLine(
		command(
			"scoreboard",
			literal("objectives"),
			literal("modify"),
			literal(objective),
			literal("rendertype"),
			literal(renderType.asArg())
		)
	)

	fun remove() = fn.addLine(command("scoreboard", literal("players"), literal("remove"), literal(objective)))
	fun setDisplay(slot: SetDisplaySlot) = fn.addLine(
		command(
			"scoreboard",
			literal("objectives"),
			literal("setdisplay"),
			slot,
			literal(objective)
		)
	)

	fun add(target: Argument.ScoreHolder, amount: Int) =
		fn.addLine(command("scoreboard", literal("players"), literal("add"), target, literal(objective), int(amount)))

	fun enable(target: Argument.ScoreHolder) =
		fn.addLine(command("scoreboard", literal("players"), literal("enable"), target, literal(objective)))

	fun list() = fn.addLine(command("scoreboard", literal("players"), literal("list"), literal(objective)))
	fun operation(
		target: Argument.ScoreHolder,
		source: Argument.ScoreHolder,
		sourceObjective: String,
		operation: Operation
	) =
		fn.addLine(
			command(
				"scoreboard",
				literal("players"),
				literal("operation"),
				target,
				literal(objective),
				literal(operation.asArg()),
				source,
				literal(sourceObjective)
			)
		)

	fun remove(target: Argument.ScoreHolder, amount: Int) = fn.addLine(
		command(
			"scoreboard",
			literal("players"),
			literal("remove"),
			target,
			literal(objective),
			int(amount)
		)
	)

	fun reset(target: Argument.ScoreHolder) =
		fn.addLine(command("scoreboard", literal("players"), literal("reset"), target, literal(objective)))

	operator fun get(target: Argument.ScoreHolder) =
		fn.addLine(command("scoreboard", literal("players"), literal("get"), target, literal(objective)))

	operator fun set(target: Argument.ScoreHolder, amount: Double) =
		fn.addLine(command("scoreboard", literal("players"), literal("set"), target, literal(objective), float(amount)))
}

@Serializable(Operation.Companion.OperationSerializer::class)
enum class Operation(val symbol: String) {
	ADD("+="),
	REMOVE("-="),
	SET("="),
	MULTIPLY("*="),
	DIVIDE("/="),
	MODULO("%="),
	SWAP("><"),
	MIN("<"),
	MAX(">");

	companion object {
		val values = values()

		object OperationSerializer : LowercaseSerializer<Operation>(values) {
			override fun serialize(encoder: Encoder, value: Operation) = encoder.encodeString(value.symbol)
		}
	}
}

class Players(private val fn: Function) {
	fun add(target: Argument.ScoreHolder, objective: String, score: Int) =
		fn.addLine(command("scoreboard", literal("players"), literal("add"), target, literal(objective), int(score)))

	fun enable(target: Argument.ScoreHolder, objective: String) =
		fn.addLine(command("scoreboard", literal("players"), literal("enable"), target, literal(objective)))

	fun get(target: Argument.ScoreHolder, objective: String) =
		fn.addLine(command("scoreboard", literal("players"), literal("get"), target, literal(objective)))

	fun list(target: Argument.ScoreHolder? = null) =
		fn.addLine(command("scoreboard", literal("players"), literal("list"), target))

	fun operation(
		target: Argument.ScoreHolder,
		objective: String,
		operation: Operation,
		source: Argument.ScoreHolder,
		sourceObjective: String,
	) = fn.addLine(
		command(
			"scoreboard",
			literal("players"),
			literal("operation"),
			target,
			literal(objective),
			literal(operation.asArg()),
			source,
			literal(sourceObjective)
		)
	)

	fun set(target: Argument.ScoreHolder, objective: String, score: Int) =
		fn.addLine(command("scoreboard", literal("players"), literal("set"), target, literal(objective), int(score)))

	fun remove(target: Argument.ScoreHolder, objective: String, score: Int) = fn.addLine(
		command(
			"scoreboard", literal("players"), literal("remove"), target, literal(objective), int(score)
		)
	)

	fun reset(target: Argument.ScoreHolder, objective: String? = null) =
		fn.addLine(command("scoreboard", literal("players"), literal("reset"), target, literal(objective)))
}

class Player(private val fn: Function, val target: Argument.ScoreHolder) {
	val players = Players(fn)
	fun objective(name: String) = PlayerObjective(fn, target, name)

	fun add(objective: String, score: Int) = players.add(target, objective, score)
	fun enable(objective: String) = players.enable(target, objective)
	fun list() = players.list(target)
	fun operation(objective: String, operation: Operation, source: Argument.ScoreHolder, sourceObjective: String) =
		players.operation(target, objective, operation, source, sourceObjective)

	fun remove(objective: String, score: Int) = players.remove(target, objective, score)
	fun reset(objective: String? = null) = players.reset(target, objective)

	operator fun get(objective: String) = players.get(target, objective)
	operator fun set(objective: String, score: Int) = players.set(target, objective, score)
}

class PlayerObjective(fn: Function, val target: Argument.ScoreHolder, val objective: String) {
	val players = Players(fn)

	fun add(score: Int) = players.add(target, objective, score)
	fun enable() = players.enable(target, objective)
	fun get() = players.get(target, objective)
	fun operation(operation: Operation, source: Argument.ScoreHolder, sourceObjective: String) =
		players.operation(target, objective, operation, source, sourceObjective)

	fun remove(score: Int) = players.remove(target, objective, score)
	fun reset() = players.reset(target, objective)
	fun set(score: Int) = players.set(target, objective, score)

	operator fun plusAssign(score: Int) {
		add(score)
	}

	operator fun minusAssign(score: Int) {
		remove(score)
	}

	operator fun inc() = apply { add(1) }

	operator fun dec() = apply { remove(1) }

	infix fun min(obj: PlayerObjective) {
		operation(Operation.MIN, obj.target, obj.objective)
	}

	infix fun max(obj: PlayerObjective) {
		operation(Operation.MAX, obj.target, obj.objective)
	}
}

class Scoreboard(private val fn: Function) {
	val objectives = Objectives(fn)
	val players = Players(fn)

	fun objectives(block: Objectives.() -> Command) = Objectives(fn).block()

	fun objective(target: Argument.ScoreHolder, objective: String, block: PlayerObjective.() -> Command) =
		PlayerObjective(fn, target, objective).block()

	fun objective(target: Argument.ScoreHolder, objective: String) = PlayerObjective(fn, target, objective)
	fun objective(objective: String) = Objective(fn, objective)
	fun objective(objective: String, block: Objective.() -> Command) = Objective(fn, objective).block()

	fun players(block: Players.() -> Command) = Players(fn).block()
	fun player(target: Argument.ScoreHolder, block: Player.() -> Command) = Player(fn, target).block()
}

val Function.scoreboard get() = Scoreboard(this)
fun Function.scoreboard(block: Scoreboard.() -> Command) = Scoreboard(this).block()
