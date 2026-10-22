package io.github.ayfri.kore.helpers.conversations.utils

import io.github.ayfri.kore.arguments.actions.RunCommand
import io.github.ayfri.kore.arguments.actions.runCommand
import io.github.ayfri.kore.arguments.chatcomponents.clickEvent
import io.github.ayfri.kore.arguments.chatcomponents.hover.showText
import io.github.ayfri.kore.arguments.chatcomponents.hoverEvent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.rangeStart
import io.github.ayfri.kore.arguments.scores.ScoreboardCriteria
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.commands.advancement
import io.github.ayfri.kore.commands.dialogShow
import io.github.ayfri.kore.commands.execute.ExecuteCondition
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.returnFail
import io.github.ayfri.kore.commands.returnRun
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.features.advancements.advancement
import io.github.ayfri.kore.features.advancements.criteria
import io.github.ayfri.kore.features.advancements.rewards
import io.github.ayfri.kore.features.advancements.triggers.tick
import io.github.ayfri.kore.features.dialogs.Dialog
import io.github.ayfri.kore.features.dialogs.action.DialogLabelledAction
import io.github.ayfri.kore.features.dialogs.types.MultiAction
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.arguments.types.AdvancementArgument
import io.github.ayfri.kore.generated.arguments.types.DialogArgument
import io.github.ayfri.kore.helpers.HelpersConstants
import io.github.ayfri.kore.helpers.conversations.ChoiceStyle
import io.github.ayfri.kore.helpers.conversations.Conversation

/** What the public API of a compiled conversation needs: the first segment and the functions `resume()` runs, by pending segment id. */
internal class CompiledConversation(val entry: FunctionArgument, val resume: List<Pair<Int, FunctionArgument>>)

/**
 * Compiles the steps of a [Conversation] into functions.
 *
 * Instant steps are grouped into segments split at each pause. A segment takes the next id when it is visited, so ids follow the
 * order of the steps, and is generated after the segments it continues into, which must exist to be called. While a pause is
 * pending, the score of the conversation holds the id of the segment that runs once it ends.
 *
 * Options are numbered before any generation, in source order across the whole conversation with the options of a choice together, which is
 * the value of the trigger objective.
 */
internal class ConversationCompiler(private val conversation: Conversation) {
	private val dataPack = conversation.dataPack
	private val name = conversation.name
	private val objective = conversation.objective
	private val waitObjective = conversation.waitObjective
	private val choiceObjective = conversation.choiceObjective
	private val tickAdvancement = AdvancementArgument(HelpersConstants.conversationAdvancementName(name), dataPack.name)

	private val choices = mutableListOf<Pair<Int, List<Pair<Int, FunctionArgument>>>>()
	private val dispatches = mutableListOf<Pair<Int, FunctionArgument>>()
	private val resumes = mutableListOf<Pair<Int, FunctionArgument>>()
	private val optionNumbers = mutableMapOf<ConversationOption, Int>()
	private var nextId = 1

	private class Segment(val id: Int, val function: FunctionArgument)

	fun compile(): CompiledConversation {
		val steps = conversation.steps
		val usesChoice = steps.anyDeep { it is ConversationStep.Choice }
		val usesWait = steps.anyDeep { it is ConversationStep.Wait || it is ConversationStep.WaitUntil }
		conversation.usesChoice = usesChoice
		conversation.usesWait = usesWait
		numberOptions(steps)

		val entry = compileSegment(steps, 0, null, null).function

		if (usesChoice || usesWait) generateTick(usesWait)
		dataPack.load(HelpersConstants.conversationLoadFunctionName(name)) {
			scoreboard.objectives.add(objective, ScoreboardCriteria.DUMMY)
			if (usesWait) scoreboard.objectives.add(waitObjective, ScoreboardCriteria.DUMMY)
			if (usesChoice) scoreboard.objectives.add(choiceObjective, ScoreboardCriteria.TRIGGER)
		}

		return CompiledConversation(entry, resumes.sortedBy { it.first })
	}

	/** Numbers the options of a choice together when it is reached, then those of the choices nested in them, so that no number repeats. */
	private fun numberOptions(steps: List<ConversationStep>) {
		for (step in steps) {
			if (step !is ConversationStep.Choice) continue
			step.options.forEach { optionNumbers[it] = optionNumbers.size + 1 }
			step.options.forEach { numberOptions(it.steps) }
		}
	}

	private fun compileSegment(
		steps: List<ConversationStep>,
		from: Int,
		continuation: FunctionArgument?,
		prefix: (Function.() -> Unit)?,
	): Segment {
		val id = nextId++
		val end = (from until steps.size).firstOrNull { steps[it] !is ConversationStep.Instant } ?: steps.size
		val instants = steps.subList(from, end).map { (it as ConversationStep.Instant).block }
		val pause = steps.getOrNull(end)
		val next = pause?.let {
			val guard = (it as? ConversationStep.WaitUntil)?.let { waitUntil -> guardOf(waitUntil) }
			compileSegment(steps, end + 1, continuation, guard)
		}
		val ending = endingOf(pause, next, continuation)

		val function = dataPack.generatedFunction(HelpersConstants.conversationSegmentFunctionName(name, id)) {
			prefix?.invoke(this)
			instants.forEach { it() }
			ending()
		}

		return Segment(id, function)
	}

	private fun endingOf(pause: ConversationStep?, next: Segment?, continuation: FunctionArgument?): Function.() -> Unit {
		if (pause == null || next == null) return {
			if (continuation == null) reset() else function(continuation)
		}

		return when (pause) {
			is ConversationStep.Choice -> choiceEnding(pause, next)
			is ConversationStep.Instant -> error("Instant steps are grouped into segments.")
			is ConversationStep.Wait -> timedEnding(next, pause.ticks)
			is ConversationStep.WaitFor -> waitForEnding(next)
			is ConversationStep.WaitUntil -> timedEnding(next, 1)
		}
	}

	private fun timedEnding(next: Segment, ticks: Int): Function.() -> Unit {
		dispatches += next.id to next.function
		return { pauseTimed(next.id, ticks) }
	}

	private fun waitForEnding(next: Segment): Function.() -> Unit {
		resumes += next.id to next.function
		return { scoreboard.players.set(self(), objective, next.id) }
	}

	private fun choiceEnding(choice: ConversationStep.Choice, next: Segment): Function.() -> Unit {
		val resetChoice: Function.() -> Unit = { scoreboard.players.reset(self(), choiceObjective) }
		val entries = choice.options.map { option ->
			optionNumbers.getValue(option) to compileSegment(option.steps, 0, next.function, resetChoice).function
		}
		val present = when (choice.style) {
			ChoiceStyle.CHAT -> chatPresentation(choice)
			ChoiceStyle.DIALOG -> dialogPresentation(choice, next.id)
		}

		val show: Function.() -> Unit = {
			scoreboard.players.enable(self(), choiceObjective)
			scoreboard.players.set(self(), choiceObjective, 0)
			present()
		}

		choices += next.id to entries
		resumes += next.id to dataPack.generatedFunction(HelpersConstants.conversationShowFunctionName(name, next.id), block = show)

		return {
			scoreboard.players.set(self(), objective, next.id)
			show()
			revokeAdvancement()
		}
	}

	private fun chatPresentation(choice: ConversationStep.Choice): Function.() -> Unit {
		val format = choice.format
		val separator = format.separator
		val options = choice.options.map { option ->
			textComponent {
				clickEvent { runCommand(triggerCommand(optionNumbers.getValue(option))) }
				option.hover?.let { hover -> hoverEvent { showText { list += hover.list } } }
				extra = format.decorate(option.label)
			}
		}

		return {
			if (format.prompt) tellraw(self(), choice.title.saidBy(choice.speaker))
			if (separator == null) options.forEach { tellraw(self(), it) }
			else tellraw(self(), options.reduce { line, option -> line + separator + option })
		}
	}

	private fun dialogPresentation(choice: ConversationStep.Choice, id: Int): Function.() -> Unit {
		val dialog = DialogArgument(HelpersConstants.conversationChoiceDialogName(name, id), dataPack.name)
		val multiAction = MultiAction(
			title = choice.title,
			actions = choice.options.map { option ->
				DialogLabelledAction(RunCommand(triggerCommand(optionNumbers.getValue(option))), option.label, option.hover)
			},
		)

		dataPack.dialogs += Dialog(dialog.name, multiAction.apply { choice.dialog?.invoke(this) })

		return { dialogShow(self(), dialog) }
	}

	private fun triggerCommand(optionValue: Int) = "trigger $choiceObjective set $optionValue"

	private fun generateTick(usesWait: Boolean) {
		val dispatch = dispatches.sortedBy { it.first }.takeIf { it.isNotEmpty() }?.let { entries ->
			dataPack.generatedFunction(HelpersConstants.conversationDispatchFunctionName(name)) {
				entries.forEach { (id, function) ->
					execute {
						ifCondition { pending(id) }
						run { returnRun(function) }
					}
				}
			}
		}

		val reenable = choices.takeIf { it.isNotEmpty() }?.let {
			dataPack.generatedFunction(HelpersConstants.conversationReenableFunctionName(name)) {
				scoreboard.players.enable(self(), choiceObjective)
				scoreboard.players.set(self(), choiceObjective, 0)
			}
		}

		val tickFunction = dataPack.generatedFunction(HelpersConstants.conversationTickFunctionName(name)) {
			execute {
				unlessCondition { score(self(), objective, rangeOrInt(rangeStart(1))) }
				run { returnFail() }
			}

			choices.sortedBy { it.first }.forEach { (id, entries) ->
				entries.forEach { (value, entry) ->
					execute {
						ifCondition { pending(id) }
						ifCondition { score(self(), choiceObjective, rangeOrInt(value)) }
						run { returnRun(entry) }
					}
				}

				execute {
					ifCondition { pending(id) }
					unlessCondition { score(self(), choiceObjective, rangeOrInt(0)) }
					run(requireNotNull(reenable))
				}

				execute {
					ifCondition { pending(id) }
					run { returnRun { revokeAdvancement() } }
				}
			}

			if (usesWait) {
				scoreboard.players.remove(self(), waitObjective, 1)
				execute {
					ifCondition { score(self(), waitObjective, rangeOrInt(rangeStart(1))) }
					run { returnRun { revokeAdvancement() } }
				}
			}

			dispatch?.let { function(it) }
		}

		dataPack.advancement(HelpersConstants.conversationAdvancementName(name)) {
			criteria { tick(TICK_CRITERION) }
			rewards { function = tickFunction }
		}
	}

	private fun guardOf(waitUntil: ConversationStep.WaitUntil): Function.() -> Unit = {
		execute {
			unlessCondition(waitUntil.condition)
			run { returnRun { revokeAdvancement() } }
		}
	}

	private fun ExecuteCondition.pending(id: Int) = score(self(), objective, rangeOrInt(id))

	private fun Function.pauseTimed(id: Int, ticks: Int) {
		scoreboard.players.set(self(), objective, id)
		scoreboard.players.set(self(), waitObjective, ticks)
		revokeAdvancement()
	}

	private fun Function.reset() {
		scoreboard.players.reset(self(), objective)
		if (conversation.usesWait) scoreboard.players.reset(self(), waitObjective)
		if (conversation.usesChoice) scoreboard.players.reset(self(), choiceObjective)
	}

	private fun Function.revokeAdvancement() = advancement(self()) { revoke(tickAdvancement) }

	private companion object {
		const val TICK_CRITERION = "tick"
	}
}
