package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.rgb
import io.github.ayfri.kore.arguments.components.enchantment
import io.github.ayfri.kore.arguments.components.enchantments
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.Particles
import kotlin.math.PI

fun Function.particleTests() {
	particle(Particles.ASH) assertsIs "particle minecraft:ash"
	particle(Particles.ASH, vec3()) assertsIs "particle minecraft:ash ~ ~ ~"
	particle(Particles.ASH, vec3(), vec3(), 1.0, 2) assertsIs "particle minecraft:ash ~ ~ ~ ~ ~ ~ 1 2"
	particle(
		Particles.ASH,
		vec3(),
		vec3(),
		1.0,
		2,
		ParticleMode.FORCE
	) assertsIs "particle minecraft:ash ~ ~ ~ ~ ~ ~ 1 2 force"

	particle(
		Particles.ASH,
		vec3(),
		vec3(),
		1.0,
		2,
		ParticleMode.NORMAL,
		allEntities()
	) assertsIs "particle minecraft:ash ~ ~ ~ ~ ~ ~ 1 2 normal @e"

	particles {
		block(Blocks.STONE_SLAB(mapOf("half" to "top"))) assertsIs "particle block minecraft:stone_slab[half=top]"

		blockMarker(Blocks.STONE) assertsIs "particle block_marker minecraft:stone"
		fallingDust(Blocks.STONE) assertsIs "particle falling_dust minecraft:stone"

		dust(Color.PURPLE, 2.0) assertsIs "particle dust 0.6666666666666666 0 1 2"
		dust(rgb("#abcdef"), 2.0) assertsIs "particle dust 0.6705882352941176 0.803921568627451 0.9372549019607843 2"

		dustColorTransition(
			Color.BLUE,
			2.0,
			Color.RED
		) assertsIs "particle dust_color_transition 0.3333333333333333 0.3333333333333333 1 2 1 0.3333333333333333 0.3333333333333333"

		item(Items.DIAMOND_SWORD {
			enchantments {
				enchantment(Enchantments.SHARPNESS, 5)
				enchantment(Enchantments.KNOCKBACK, 2)
			}
		}) assertsIs """particle item minecraft:diamond_sword[enchantments={"minecraft:sharpness":5,"minecraft:knockback":2}]"""

		particle(Particles.ASH) assertsIs "particle minecraft:ash"

		sculkCharge(PI / 2) assertsIs "particle sculk_charge 1.5707963267948966"
		shriek(100) assertsIs "particle shriek 100"
		vibration(vec3(1, 2, 3), 10) assertsIs "particle vibration 1 2 3 10"
	}
}
