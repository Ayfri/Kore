package commands

import arguments.DisplaySlot
import arguments.chatcomponents.ChatComponents
import arguments.chatcomponents.PlainTextComponent
import arguments.chatcomponents.textComponent
import arguments.colors.Color
import arguments.types.ScoreHolderArgument
import arguments.types.literals.int
import arguments.types.literals.literal
import functions.Function
import serializers.LowercaseSerializer
import utils.asArg
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Encoder

@Serializable(RenderType.Companion.RenderTypeSerializer::class)
enum class RenderType {
	HEARTS,
	INTEGER;

	companion object {
		data object RenderTypeSerializer : LowercaseSerializer<RenderType>(entries)
	}
}

class Objectives(private val fn: Function) {
	fun add(name: String, criteria: String, displayName: ChatComponents? = null) =
		fn.addLine(
			command(
				"scoreboard",
				literal("objectives"),
				literal("add"),
				literal(name),
				literal(criteria),
				displayName?.asJsonArg()
			)
		)

	fun add(name: String, criteria: String, displayName: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
		add(name, criteria, textComponent(displayName, color, block))

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

	fun modify(name: String, displayName: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
		modify(name, textComponent(displayName, color, block))

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

	fun setDisplay(slot: DisplaySlot, name: String) =
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
	fun player(target: ScoreHolderArgument) = PlayerObjective(fn, target, objective)
	fun player(target: ScoreHolderArgument, block: PlayerObjective.() -> Command) = PlayerObjective(fn, target, objective).block()

	fun add(criteria: String, displayName: ChatComponents? = null) = create(criteria, displayName)
	fun add(criteria: String, displayName: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
		create(criteria, textComponent(displayName, color, block))

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

	fun create(criteria: String, displayName: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
		create(criteria, textComponent(displayName, color, block))

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

	fun modify(displayName: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
		modify(textComponent(displayName, color, block))

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

	fun remove() = fn.addLine(command("scoreboard", literal("objectives"), literal("remove"), literal(objective)))

	fun setDisplay(slot: DisplaySlot) = fn.addLine(
		command(
			"scoreboard",
			literal("objectives"),
			literal("setdisplay"),
			slot,
			literal(objective)
		)
	)

	fun add(target: ScoreHolderArgument, amount: Int) =
		fn.addLine(command("scoreboard", literal("players"), literal("add"), target, literal(objective), int(amount)))

	fun enable(target: ScoreHolderArgument) =
		fn.addLine(command("scoreboard", literal("players"), literal("enable"), target, literal(objective)))

	fun operation(
		target: ScoreHolderArgument,
		operation: Operation,
		source: ScoreHolderArgument,
		sourceObjective: String,
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

	fun remove(target: ScoreHolderArgument, amount: Int) = fn.addLine(
		command(
			"scoreboard",
			literal("players"),
			literal("remove"),
			target,
			literal(objective),
			int(amount)
		)
	)

	fun reset(target: ScoreHolderArgument) =
		fn.addLine(command("scoreboard", literal("players"), literal("reset"), target, literal(objective)))

	operator fun get(target: ScoreHolderArgument) =
		fn.addLine(command("scoreboard", literal("players"), literal("get"), target, literal(objective)))

	operator fun set(target: ScoreHolderArgument, amount: Int) =
		fn.addLine(command("scoreboard", literal("players"), literal("set"), target, literal(objective), int(amount)))
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
		data object OperationSerializer : LowercaseSerializer<Operation>(entries) {
			override fun serialize(encoder: Encoder, value: Operation) = encoder.encodeString(value.symbol)
		}
	}
}

class Players(private val fn: Function) {
	fun add(target: ScoreHolderArgument, objective: String, score: Int) =
		fn.addLine(command("scoreboard", literal("players"), literal("add"), target, literal(objective), int(score)))

	fun enable(target: ScoreHolderArgument, objective: String) =
		fn.addLine(command("scoreboard", literal("players"), literal("enable"), target, literal(objective)))

	fun get(target: ScoreHolderArgument, objective: String) =
		fn.addLine(command("scoreboard", literal("players"), literal("get"), target, literal(objective)))

	fun list(target: ScoreHolderArgument? = null) =
		fn.addLine(command("scoreboard", literal("players"), literal("list"), target))

	fun operation(
		target: ScoreHolderArgument,
		objective: String,
		operation: Operation,
		source: ScoreHolderArgument,
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

	fun remove(target: ScoreHolderArgument, objective: String, score: Int) = fn.addLine(
		command(
			"scoreboard", literal("players"), literal("remove"), target, literal(objective), int(score)
		)
	)

	fun reset(target: ScoreHolderArgument, objective: String? = null) =
		fn.addLine(command("scoreboard", literal("players"), literal("reset"), target, literal(objective)))

	fun set(target: ScoreHolderArgument, objective: String, score: Int) =
		fn.addLine(command("scoreboard", literal("players"), literal("set"), target, literal(objective), int(score)))
}

class Player(private val fn: Function, val target: ScoreHolderArgument) {
	val players = Players(fn)
	fun objective(name: String) = PlayerObjective(fn, target, name)

	fun add(objective: String, score: Int) = players.add(target, objective, score)
	fun enable(objective: String) = players.enable(target, objective)
	fun list() = players.list(target)
	fun operation(objective: String, operation: Operation, source: ScoreHolderArgument, sourceObjective: String) =
		players.operation(target, objective, operation, source, sourceObjective)

	fun remove(objective: String, score: Int) = players.remove(target, objective, score)
	fun reset(objective: String? = null) = players.reset(target, objective)

	operator fun get(objective: String) = players.get(target, objective)
	operator fun set(objective: String, score: Int) = players.set(target, objective, score)
}

class PlayerObjective(fn: Function, val target: ScoreHolderArgument, val objective: String) {
	val players = Players(fn)

	fun add(score: Int) = players.add(target, objective, score)
	fun enable() = players.enable(target, objective)
	fun get() = players.get(target, objective)
	fun operation(operation: Operation, source: ScoreHolderArgument, sourceObjective: String) =
		players.operation(target, objective, operation, source, sourceObjective)

	fun operation(operation: Operation, source: PlayerObjective) =
		players.operation(target, objective, operation, source.target, source.objective)

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

	fun objective(target: ScoreHolderArgument, objective: String, block: PlayerObjective.() -> Command) =
		PlayerObjective(fn, target, objective).block()

	fun objective(target: ScoreHolderArgument, objective: String) = PlayerObjective(fn, target, objective)
	fun objective(objective: String) = Objective(fn, objective)
	fun objective(objective: String, block: Objective.() -> Command) = Objective(fn, objective).block()

	fun players(block: Players.() -> Command) = Players(fn).block()
	fun player(target: ScoreHolderArgument, block: Player.() -> Command) = Player(fn, target).block()
}

val Function.scoreboard get() = Scoreboard(this)
fun Function.scoreboard(block: Scoreboard.() -> Command) = Scoreboard(this).block()
