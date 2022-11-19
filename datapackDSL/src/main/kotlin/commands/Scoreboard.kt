package commands

import arguments.Argument
import arguments.Color
import arguments.int
import arguments.literal
import functions.Function
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Encoder
import serializers.LowercaseSerializer

fun interface SetDisplaySlot {
	fun asString(): String
}

object DisplaySlot {
	val belowName = SetDisplaySlot { "belowName" }
	val list = SetDisplaySlot { "list" }
	val sidebar = SetDisplaySlot { "sidebar" }
	fun sidebarTeam(color: Color) = SetDisplaySlot { "sidebar.team.${color.name.lowercase()}" }
}

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
	fun add(name: String, criteria: String, displayName: String? = null) = fn.addLine(
		command(
			"scoreboard", literal("objective"), literal("add"), literal(name), literal(criteria), literal(displayName)
		)
	)
	
	fun list() = fn.addLine(command("scoreboard", literal("objective"), literal("list")))
	fun modify(name: String, displayName: String? = null) =
		fn.addLine(command("scoreboard", literal("objective"), literal("modify"), literal(name), literal("displayname"), literal(displayName)))
	
	fun modify(name: String, renderType: RenderType) =
		fn.addLine(command("scoreboard", literal("objective"), literal("modify"), literal(name), literal("rendertype"), literal(renderType.asArg())))
	
	fun remove(name: String) = fn.addLine(command("scoreboard", literal("objective"), literal("remove"), literal(name)))
	fun setDisplay(slot: SetDisplaySlot, name: String) = fn.addLine(command("scoreboard", literal("objective"), literal("setdisplay"), literal(slot.asString()), literal(name)))
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
	fun list(target: Argument.ScoreHolder? = null) = fn.addLine(command("scoreboard", literal("players"), literal("list"), target))
	fun get(target: Argument.ScoreHolder, objective: String) = fn.addLine(command("scoreboard", literal("players"), literal("get"), target, literal(objective)))
	fun set(target: Argument.ScoreHolder, objective: String, score: Int) = fn.addLine(command("scoreboard", literal("players"), literal("set"), target, literal(objective), int(score)))
	fun add(target: Argument.ScoreHolder, objective: String, score: Int) = fn.addLine(command("scoreboard", literal("players"), literal("add"), target, literal(objective), int(score)))
	fun remove(target: Argument.ScoreHolder, objective: String, score: Int) = fn.addLine(
		command(
			"scoreboard", literal("players"), literal("remove"), target, literal(objective), int(score)
		)
	)
	
	fun reset(target: Argument.ScoreHolder, objective: String? = null) = fn.addLine(command("scoreboard", literal("players"), literal("reset"), target, literal(objective)))
	fun enable(target: Argument.ScoreHolder, objective: String) = fn.addLine(command("scoreboard", literal("players"), literal("enable"), target, literal(objective)))
	
	fun operation(
		target: Argument.ScoreHolder,
		objective: String,
		operation: Operation,
		source: Argument.ScoreHolder,
		sourceObjective: String,
	) = fn.addLine(command("scoreboard", literal("players"), literal("operation"), target, literal(objective), literal(operation.asArg()), source, literal(sourceObjective)))
}

class Player(fn: Function, val target: Argument.ScoreHolder) {
	val objectives = Objectives(fn)
	val players = Players(fn)
	
	fun list() = players.list(target)
	operator fun get(objective: String) = players.get(target, objective)
	operator fun set(objective: String, score: Int) = players.set(target, objective, score)
	fun add(objective: String, score: Int) = players.add(target, objective, score)
	fun remove(objective: String, score: Int) = players.remove(target, objective, score)
	fun reset(objective: String? = null) = players.reset(target, objective)
	fun enable(objective: String) = players.enable(target, objective)
	fun operation(objective: String, operation: Operation, source: Argument.ScoreHolder, sourceObjective: String) = players.operation(target, objective, operation, source, sourceObjective)
}

class PlayerObjective(fn: Function, val target: Argument.ScoreHolder, val objective: String) {
	val players = Players(fn)
	
	fun get() = players.get(target, objective)
	fun set(score: Int) = players.set(target, objective, score)
	fun add(score: Int) = players.add(target, objective, score)
	fun remove(score: Int) = players.remove(target, objective, score)
	fun reset() = players.reset(target, objective)
	fun enable() = players.enable(target, objective)
	fun operation(operation: Operation, source: Argument.ScoreHolder, sourceObjective: String) = players.operation(target, objective, operation, source, sourceObjective)
	
	operator fun plusAssign(score: Int) {
		add(score)
	}
	
	operator fun minusAssign(score: Int) {
		remove(score)
	}
	
	infix fun min(obj: PlayerObjective) {
		operation(Operation.MIN, obj.target, obj.objective)
	}
	
	infix fun max(obj: PlayerObjective) {
		operation(Operation.MAX, obj.target, obj.objective)
	}
}

class Scoreboard(private val fn: Function) {
	fun objectives(block: Objectives.() -> Unit) = Objectives(fn).block()
	
	fun objective(target: Argument.ScoreHolder, objective: String, block: PlayerObjective.() -> Unit) = PlayerObjective(fn, target, objective).block()
	fun objective(target: Argument.ScoreHolder, objective: String) = PlayerObjective(fn, target, objective)
	
	fun players(block: Players.() -> Unit) = Players(fn).block()
	fun player(target: Argument.ScoreHolder, block: Player.() -> Unit) = Player(fn, target).block()
}

fun Function.scoreboard(block: Scoreboard.() -> Unit) = Scoreboard(this).block()
