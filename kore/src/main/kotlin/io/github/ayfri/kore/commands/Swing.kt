package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.Serializable

@Serializable(SwingHand.Companion.SwingHandSerializer::class)
enum class SwingHand {
	MAINHAND,
	OFFHAND;

	companion object {
		data object SwingHandSerializer : LowercaseSerializer<SwingHand>(entries)
	}
}

/** Makes [targets] swing their [hand]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/swing) */
fun Function.swing(targets: EntityArgument, hand: SwingHand) =
	addLine(command("swing", targets, literal(hand.asArg())))
