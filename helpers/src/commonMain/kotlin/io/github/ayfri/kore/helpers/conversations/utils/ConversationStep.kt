package io.github.ayfri.kore.helpers.conversations.utils

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.commands.execute.ExecuteCondition
import io.github.ayfri.kore.features.dialogs.types.MultiAction
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.helpers.conversations.ChatChoiceFormat
import io.github.ayfri.kore.helpers.conversations.ChoiceStyle

/** One option of a [ConversationStep.Choice]: its [label], the [hover] text shown on it and the [steps] run when it is picked. */
internal class ConversationOption(val label: ChatComponents, val hover: ChatComponents?, val steps: List<ConversationStep>)

/** A step recorded by the `Conversation` builder, compiled into functions when the declaration block returns. */
internal sealed interface ConversationStep {
	/**
	 * A pause on [options] shown in the [style] way, with the chat [format] or the [dialog] tweak, the prompt of the chat
	 * style said by [speaker].
	 */
	class Choice(
		val title: ChatComponents,
		val options: List<ConversationOption>,
		val style: ChoiceStyle,
		val format: ChatChoiceFormat,
		val dialog: (MultiAction.() -> Unit)?,
		val speaker: ChatComponents?,
	) : ConversationStep

	/** Commands run in the current segment, a line, a dialog or a free-form block. */
	class Instant(val block: Function.() -> Unit) : ConversationStep

	/** A pause of [ticks] ticks. */
	class Wait(val ticks: Int) : ConversationStep

	/** A pause until `advance()` or `resume()` runs. */
	data object WaitFor : ConversationStep

	/** A pause until [condition] holds, checked every tick. */
	class WaitUntil(val condition: ExecuteCondition.() -> Unit) : ConversationStep
}

/** Whether [predicate] matches a step of this list or of the options of its choices, at any depth. */
internal fun List<ConversationStep>.anyDeep(predicate: (ConversationStep) -> Boolean): Boolean = any { step ->
	predicate(step) || step is ConversationStep.Choice && step.options.any { it.steps.anyDeep(predicate) }
}
