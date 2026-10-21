package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.EnumArgument
import io.github.ayfri.kore.arguments.enums.SwingAnimationType
import io.github.ayfri.kore.arguments.numbers.TimeNumber
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.time
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(SwingHand.Companion.SwingHandSerializer::class)
enum class SwingHand : EnumArgument {
	MAINHAND,
	OFFHAND;

	companion object {
		data object SwingHandSerializer : LowercaseSerializer<SwingHand>(entries)
	}
}

/** Makes the executing entity swing their mainhand. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/swing) */
fun Function.swing() =
	addLine(command("swing"))

/** Makes [targets] swing their mainhand. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/swing) */
fun Function.swing(targets: EntityArgument) =
	addLine(command("swing", targets))

/** Makes [targets] swing their [hand]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/swing) */
fun Function.swing(targets: EntityArgument, hand: SwingHand) =
	addLine(command("swing", targets, hand))

/**
 * Makes [targets] swing their [hand] with the given [animation], for [duration] (at least 1 tick, 6 ticks when omitted).
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/swing)
 *
 * ```
 * swing(self(), SwingHand.MAINHAND, SwingAnimationType.STAB, 10.ticks)  // swing @s mainhand stab 10
 * swing(self(), SwingHand.OFFHAND, SwingAnimationType.WHACK)            // swing @s offhand whack
 * ```
 */
fun Function.swing(targets: EntityArgument, hand: SwingHand, animation: SwingAnimationType, duration: TimeNumber? = null) =
	addLine(command("swing", targets, hand, animation, duration?.let(::time)))
