package commands

import arguments.colors.Color
import arguments.colors.rgb
import arguments.maths.vec3
import arguments.types.literals.allEntities
import functions.Function
import generated.Blocks
import generated.Items
import generated.Particles
import net.benwoodworth.knbt.addNbtCompound
import utils.assertsIs
import utils.nbtList
import utils.set
import kotlin.math.PI

fun Function.particleTests() {
	particle(Particles.ASH) assertsIs "particle minecraft:ash"
	particle(Particles.ASH, vec3()) assertsIs "particle minecraft:ash ~ ~ ~"
	particle(Particles.ASH, vec3(), vec3(), 1.0, 2) assertsIs "particle minecraft:ash ~ ~ ~ ~ ~ ~ 1 2"
	particle(Particles.ASH, vec3(), vec3(), 1.0, 2, ParticleMode.FORCE) assertsIs "particle minecraft:ash ~ ~ ~ ~ ~ ~ 1 2 force"

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
			this["Enchantments"] = nbtList {
				addNbtCompound {
					this["id"] = "minecraft:sharpness"
					this["lvl"] = 5
				}

				addNbtCompound {
					this["id"] = "minecraft:knockback"
					this["lvl"] = 2
				}
			}
		}) assertsIs "particle item minecraft:diamond_sword{Enchantments:[{id:\"minecraft:sharpness\",lvl:5},{id:\"minecraft:knockback\",lvl:2}]}"

		particle(Particles.ASH) assertsIs "particle minecraft:ash"

		sculkCharge(PI / 2) assertsIs "particle sculk_charge 1.5707963267948966"
		shriek(100) assertsIs "particle shriek 100"
		vibration(vec3(1, 2, 3), 10) assertsIs "particle vibration 1 2 3 10"
	}
}
