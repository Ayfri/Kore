package io.github.ayfri.kore.helpers.conversations

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.DataPackStateKey
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.numbers.TimeNumber
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.rangeStart
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.commands.dialogShow
import io.github.ayfri.kore.commands.execute.ExecuteCondition
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.returnRun
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.features.dialogs.types.MultiAction
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.generated.arguments.types.DialogArgument
import io.github.ayfri.kore.helpers.HelpersConstants
import io.github.ayfri.kore.helpers.conversations.utils.CompiledConversation
import io.github.ayfri.kore.helpers.conversations.utils.ConversationCompiler
import io.github.ayfri.kore.helpers.conversations.utils.ConversationOption
import io.github.ayfri.kore.helpers.conversations.utils.ConversationStep
import io.github.ayfri.kore.helpers.conversations.utils.saidBy

private val declaredConversations = DataPackStateKey<MutableSet<String>>("helpers.conversations")

/**
 * A scripted exchange played to one player: lines in the chat, timed or click-driven pauses, dialog choices and free-form
 * commands, whose progress is stored on the player so any number of players run it at the same time.
 *
 * Declared with `dataPack.conversation(name) { }`, the steps added in the block are compiled into functions when it returns.
 * [start] and [advance] run as the player the conversation is played to, `@s`.
 *
 * ```kotlin
 * val greet = conversation("greet") {
 * 	speaker = textComponent("Bob")
 * 	line("Hello!")
 * 	wait(2.seconds)
 * 	line("Bye!")
 * }
 *
 * function("talk") { greet.start() }  // "<Bob> Hello!", then "<Bob> Bye!" 2 seconds later, for the player running `talk`
 * ```
 *
 * Running a conversation as a non-player isn't possible because its timers run on the advancements of the player.
 *
 * Docs: https://kore.ayfri.com/docs/helpers/conversations
 *
 * @property dataPack The data pack declaring the conversation.
 * @property name The name of the conversation, lowercase letters, digits and underscores, unique in the pack.
 */
@ConversationDsl
class Conversation internal constructor(val dataPack: DataPack, val name: String, parent: Conversation? = null) {
	private val root: Conversation = parent?.root ?: this

	internal val steps = mutableListOf<ConversationStep>()

	internal var usesChoice = false
	internal var usesWait = false

	private var compiled: CompiledConversation? = null

	/**
	 * How the [choice]s declared after this are written in the chat when their style is [ChoiceStyle.CHAT], a choice overrides it
	 * with [ConversationChoice.chat].
	 *
	 * ```kotlin
	 * chatFormat = chatFormat.copy(separator = null)  // each option on its own line
	 * ```
	 */
	var chatFormat = ChatChoiceFormat()

	/**
	 * The `trigger` objective the options of a [choice] set, `<namespace>.conversation.<name>.choice`.
	 * It is only created when the conversation has a choice.
	 */
	val choiceObjective = HelpersConstants.conversationChoiceObjective(dataPack.name, name)

	/** The style of the [choice]s declared after this one that don't give their own, a dialog by default. */
	var choiceStyle = ChoiceStyle.DIALOG

	/** The score of each player: the id of the segment that runs next, unset while the conversation is idle, `<namespace>.conversation.<name>`. */
	val objective = HelpersConstants.conversationObjective(dataPack.name, name)

	/**
	 * Who the following [line]s are said by, shown as `<Bob> Hello!`. Without a speaker a line is the bare text.
	 * A speaker set inside an option applies to the lines after it in that option only.
	 *
	 * ```kotlin
	 * speaker = textComponent("Bob")
	 * line("Hi")  // tellraw @s {type:translatable,translate:"chat.type.text",with:[{type:text,text:Bob},{type:text,text:Hi}]}
	 * ```
	 */
	var speaker: ChatComponents? = null

	/** The countdown of the timed pauses in ticks, `<namespace>.conversation.<name>.wait`. It is only created when the conversation has a [wait] or a [waitUntil]. */
	val waitObjective = HelpersConstants.conversationWaitObjective(dataPack.name, name)

	init {
		require(parent != null || NAME.matches(name)) { "Conversation name '$name' must be lowercase letters, digits and underscores." }
	}

	/**
	 * Pauses on one [ConversationChoice.option] per button or clickable text, until the player picks one. The conversation goes
	 * on with the steps after the choice once the steps of the picked option ran. A dialog closed with Escape, or chat options
	 * scrolled away, keep the choice pending: [advance] or [resume] shows it again.
	 *
	 * [style] is [choiceStyle] unless given. Both styles set the same `trigger` objective, `trigger <choiceObjective> set <n>`,
	 * which any player can use.
	 *
	 * ```kotlin
	 * choice("Take the quest?") {
	 * 	option("Accept", Color.GREEN) { line("Good luck.") }
	 * 	option("Decline", Color.RED) { line("Maybe later.") }
	 * }
	 * choice("Will you help?", ChoiceStyle.CHAT) { option("Yes") { line("Thanks.") } }  // [Yes] in the chat, clickable
	 * ```
	 */
	fun choice(title: ChatComponents, style: ChoiceStyle? = null, block: ConversationChoice.() -> Unit) {
		val choice = ConversationChoice(this, style ?: choiceStyle).apply(block)
		require(choice.options.isNotEmpty()) { "A choice of conversation '$name' needs at least one option." }
		steps += ConversationStep.Choice(title, choice.options, choice.style, choice.format, choice.dialogTweak, speaker)
	}

	/** Pauses on a choice titled [title], see the [ChatComponents] overload. */
	fun choice(title: String, style: ChoiceStyle? = null, block: ConversationChoice.() -> Unit) =
		choice(textComponent(title), style, block)

	/** Says [text] to the player in the chat, as [speaker] when one is set, see the [String] overload. */
	fun line(text: ChatComponents) {
		val speaker = speaker
		steps += ConversationStep.Instant { tellraw(self(), text.saidBy(speaker)) }
	}

	/**
	 * Says [text] to the player in the chat, as [speaker] when one is set.
	 *
	 * ```kotlin
	 * line("Hello!")           // tellraw @s "Hello!", without a speaker
	 * line("Run!", Color.RED)  // the text is red, the speaker's name keeps its own color
	 * ```
	 */
	fun line(text: String, color: Color? = null) = line(textComponent(text, color))

	/** Shows [dialog] to the player, the conversation goes on right away, see [choice] to wait for a button. */
	fun show(dialog: DialogArgument) {
		steps += ConversationStep.Instant { dialogShow(self(), dialog) }
	}

	/**
	 * Runs [block] as the player, any commands.
	 *
	 * ```kotlin
	 * step { give(self(), Items.BREAD, 3) }  // give @s minecraft:bread 3
	 * ```
	 */
	fun step(block: @ConversationDsl Function.() -> Unit) {
		steps += ConversationStep.Instant(block)
	}

	/**
	 * Pauses for [duration], then goes on with the next step.
	 * The countdown loses one point per tick of the player, so it stops while the player is logged out. The next step runs on
	 * the countdown's last point, N ticks after the previous step, or N - 1 when the advancement fires again in the very tick
	 * that set it, depending on where the previous step ran in the game tick.
	 * The duration is truncated to whole ticks and must be at least one tick.
	 *
	 * ```kotlin
	 * wait(2.seconds)    // sets the countdown to 40
	 * wait(0.5.seconds)  // sets the countdown to 10
	 * wait(0.ticks)      // throws, nothing can run in zero ticks
	 * ```
	 */
	fun wait(duration: TimeNumber) {
		val ticks = duration.wholeTicks()
		require(ticks >= 1) { "A conversation wait lasts at least one tick, got $ticks." }
		steps += ConversationStep.Wait(ticks)
	}

	/**
	 * Pauses until [advance] or [resume] runs for the player, typically from a click on an NPC.
	 * Nothing runs every tick while waiting.
	 *
	 * ```kotlin
	 * line("Come back with 10 diamonds.")
	 * waitFor()
	 * line("Thank you!")  // when the player clicks the NPC again
	 * ```
	 */
	fun waitFor() {
		steps += ConversationStep.WaitFor
	}

	/**
	 * Pauses until [condition] holds for the player, checked every tick while waiting.
	 *
	 * ```kotlin
	 * waitUntil { score(self(), "my_pack.mined") greaterThanOrEqualTo 10 }  // execute unless score @s my_pack.mined matches 10.. run ...
	 * ```
	 */
	fun waitUntil(condition: @ConversationDsl ExecuteCondition.() -> Unit) {
		steps += ConversationStep.WaitUntil(condition)
	}

	/**
	 * Continues the conversation of the player: starts it when idle, continues a [waitFor], shows a pending [choice] again,
	 * and does nothing during a timed [wait] or [waitUntil], so a player clicking an NPC twice can't skip a pause.
	 *
	 * ```kotlin
	 * onRightClick { quest.advance() }  // function my_pack:generated_scopes/conversation_quest_advance
	 * ```
	 */
	context(fn: Function)
	fun advance() = fn.function(advanceFunction())

	/**
	 * Cancels the conversation of the player: resets the scores, so it is idle again. A pending timer stops, and a choice
	 * dialog already open stays on screen but its buttons no longer do anything.
	 *
	 * ```kotlin
	 * cancel()  // scoreboard players reset @s my_pack.conversation.quest
	 * ```
	 */
	context(fn: Function)
	fun cancel() {
		fn.scoreboard.players.reset(self(), objective)
		if (root.usesWait) fn.scoreboard.players.reset(self(), waitObjective)
		if (root.usesChoice) fn.scoreboard.players.reset(self(), choiceObjective)
	}

	/**
	 * Continues a [waitFor] or shows a pending [choice] again for the player, and does nothing in any other state.
	 * [advance] does the same and starts the conversation when it is idle.
	 *
	 * ```kotlin
	 * resume()  // function my_pack:generated_scopes/conversation_quest_resume
	 * ```
	 */
	context(fn: Function)
	fun resume() = fn.function(resumeFunction())

	/**
	 * Starts the conversation for the player unless it is already running.
	 *
	 * ```kotlin
	 * start()  // execute unless score @s my_pack.conversation.quest matches 1.. run function my_pack:generated_scopes/conversation_quest_1
	 * ```
	 */
	context(fn: Function)
	fun start() = fn.execute {
		unlessCondition { score(self(), objective, rangeOrInt(rangeStart(1))) }
		run(root.compiled().entry)
	}

	internal fun branch() = Conversation(dataPack, name, this).also {
		it.chatFormat = chatFormat.copy()
		it.choiceStyle = choiceStyle
		it.speaker = speaker
	}

	internal fun register() {
		compiled = ConversationCompiler(this).compile()
	}

	private fun advanceFunction(): FunctionArgument {
		val compiled = root.compiled()
		val resume = compiled.resume.takeIf { it.isNotEmpty() }?.let { resumeFunction() }

		return dataPack.generatedFunction(HelpersConstants.conversationAdvanceFunctionName(name)) {
			execute {
				unlessCondition { score(self(), objective, rangeOrInt(rangeStart(1))) }
				run { returnRun(compiled.entry) }
			}

			resume?.let { function(it) }
		}
	}

	private fun compiled() = checkNotNull(compiled) { "Conversation '$name' is used before its declaration block returned." }

	private fun resumeFunction(): FunctionArgument {
		val resume = root.compiled().resume
		check(resume.isNotEmpty()) { "Conversation '$name' has no waitFor or choice to resume." }

		return dataPack.generatedFunction(HelpersConstants.conversationResumeFunctionName(name)) {
			resume.forEach { (id, function) ->
				execute {
					ifCondition { score(self(), objective, rangeOrInt(id)) }
					run { returnRun(function) }
				}
			}
		}
	}

	private companion object {
		val NAME = Regex("[a-z0-9_]+")
	}
}

/**
 * The options of a [Conversation.choice], each shown as a button of the dialog or a clickable text of the chat, and the
 * appearance of both.
 *
 * ```kotlin
 * choice("Take the quest?") {
 * 	option("Accept") { line("Good luck.") }
 * 	option("Decline") { line("Maybe later.") }
 * 	dialog { columns = 1 }  // one button per row
 * }
 * ```
 */
@ConversationDsl
class ConversationChoice internal constructor(private val conversation: Conversation, internal val style: ChoiceStyle) {
	internal var dialogTweak: (MultiAction.() -> Unit)? = null
	internal var format = conversation.chatFormat.copy()
	internal val options = mutableListOf<ConversationOption>()

	/**
	 * Overrides the [Conversation.chatFormat] for this choice, when its style is [ChoiceStyle.CHAT].
	 *
	 * ```kotlin
	 * chat { separator = null }  // each option on its own line
	 * ```
	 */
	fun chat(block: ChatChoiceFormat.() -> Unit) {
		format.apply(block)
	}

	/**
	 * Tweaks the dialog generated for this choice, when its style is [ChoiceStyle.DIALOG], after its title and one button per
	 * option are set: `body`, `columns`, `canCloseWithEscape`, `pause`, each `actions` width...
	 *
	 * ```kotlin
	 * dialog { columns = 1 }  // one button per row
	 * ```
	 */
	fun dialog(block: MultiAction.() -> Unit) {
		dialogTweak = block
	}

	/**
	 * Adds an option labelled [label], with a [hover] text, and the steps run when a player picks it, which can be any step of a
	 * conversation, even another [Conversation.choice]. The conversation then goes on with the steps after the choice.
	 * The label is what the button or the clickable text shows, see [ChatChoiceFormat.decorate] for how the chat wraps it.
	 *
	 * ```kotlin
	 * option(textComponent("Accept", Color.GREEN), textComponent("Take the pickaxe")) { line("Good luck.") }
	 * ```
	 */
	fun option(label: ChatComponents, hover: ChatComponents? = null, block: Conversation.() -> Unit) {
		options += ConversationOption(label, hover, conversation.branch().apply(block).steps)
	}

	/**
	 * Adds an option labelled [label] in [color], with a [hover] text, see the [ChatComponents] overload.
	 *
	 * ```kotlin
	 * option("Accept", Color.GREEN, "Take the pickaxe") { step { give(self(), Items.IRON_PICKAXE) } }
	 * ```
	 */
	fun option(label: String, color: Color? = null, hover: String? = null, block: Conversation.() -> Unit) =
		option(textComponent(label, color), hover?.let(::textComponent), block)
}

/**
 * Declares the conversation [name] in this pack, its steps added in [block] and compiled into functions when it returns.
 *
 * ```kotlin
 * val greet = conversation("greet") {
 * 	line("Hello!")
 * 	wait(2.seconds)
 * 	line("Bye!")
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/helpers/conversations
 *
 * @param name The name of the conversation, lowercase letters, digits and underscores, unique in the pack.
 * @param block The builder block, where the steps are declared in order.
 * @return The declared [Conversation].
 */
fun DataPack.conversation(name: String, block: Conversation.() -> Unit): Conversation {
	val conversation = Conversation(this, name)
	require(state(declaredConversations) { mutableSetOf() }.add(name)) { "Conversation '$name' is already declared in this pack." }
	return conversation.apply(block).also(Conversation::register)
}
