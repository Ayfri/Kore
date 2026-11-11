package io.github.ayfri.kore.website.playground.snippets.displayentities

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.maths.Vec3f
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.commands.summon
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.helpers.displays.blockDisplay
import io.github.ayfri.kore.helpers.displays.blockState
import io.github.ayfri.kore.helpers.displays.entities.BillboardMode
import io.github.ayfri.kore.helpers.displays.maths.transformation
import io.github.ayfri.kore.helpers.displays.maths.translation
import io.github.ayfri.kore.helpers.displays.text
import io.github.ayfri.kore.helpers.displays.textDisplay
import io.github.ayfri.kore.pack.pack

fun playground() = dataPack("holograms") {
	pack { description = textComponent("A floating text and a scaled block display") }

	val sign = textDisplay {
		text("Welcome!", Color.GOLD) {
			bold = true
		}
		billboardMode = BillboardMode.CENTER
	}

	val crystal = blockDisplay {
		blockState(Blocks.AMETHYST_BLOCK)

		transformation {
			scale = Vec3f(2, 2, 2)

			translation {
				y = 1f
			}
		}
	}

	load("spawn_displays") {
		summon(sign.entityType, vec3(0, 66, 0), sign.toNbt())
		summon(crystal.entityType, vec3(0, 64, 0), crystal.toNbt())
	}
}
