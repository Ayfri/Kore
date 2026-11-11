package io.github.ayfri.kore.website.playground.snippets.events

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.entities.entity
import io.github.ayfri.kore.entities.player
import io.github.ayfri.kore.events.onConsumeItem
import io.github.ayfri.kore.events.onDeath
import io.github.ayfri.kore.events.onKill
import io.github.ayfri.kore.events.onRightClick
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.pack.pack

fun playground() = dataPack("event_demo") {
	pack { description = textComponent("Handlers for a stick click, a golden apple, a kill and a boss death") }

	val steve = player("Steve")
	val boss = entity(EntityTypes.ZOMBIE) { tag = "boss" }

	steve.onRightClick(Items.STICK) { say("Whoosh!") }
	steve.onConsumeItem(Items.GOLDEN_APPLE) { say("Golden apple!") }

	steve.onKill { say("Kill counted!") }

	boss.onDeath { say("The boss is down!") }
}
