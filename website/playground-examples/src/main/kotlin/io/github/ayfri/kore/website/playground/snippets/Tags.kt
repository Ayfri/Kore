package io.github.ayfri.kore.website.playground.snippets.tags

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.tags.blockTag
import io.github.ayfri.kore.features.tags.itemTag
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.pack.pack

fun playground() = dataPack("arena_tags") {
	pack { description = textComponent("Block and item tags") }

	val arenaFloor = blockTag("arena_floor") {
		add(Blocks.STONE)
		add(Blocks.POLISHED_ANDESITE)
		this += Blocks.SMOOTH_STONE
	}

	itemTag("gems") {
		add(Items.DIAMOND)
		add(Items.EMERALD)
		add("mymod:ruby", required = false)
	}

	function("check_floor") {
		execute {
			asTarget(allPlayers())
			at(self())
			ifCondition {
				block(vec3(0, -1, 0).relative, arenaFloor)
			}
			run {
				say("Standing on the arena floor")
			}
		}
	}
}
