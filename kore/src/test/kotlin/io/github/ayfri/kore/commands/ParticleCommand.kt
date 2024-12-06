package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.rgb
import io.github.ayfri.kore.arguments.components.types.enchantment
import io.github.ayfri.kore.arguments.components.types.enchantments
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.particle.ParticleMode
import io.github.ayfri.kore.commands.particle.particle
import io.github.ayfri.kore.commands.particle.particles
import io.github.ayfri.kore.commands.particle.types.*
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
		// il faut fix la serialization, le naming, à voir pour refaire encore le sérialiseur, aussi les properties sont dans le Name
		block(Blocks.STONE_SLAB(states = mapOf("half" to "top"))) assertsIs "particle block{block_state:{Name:\"minecraft:stone_slab\",Properties:{half:\"top\"}}}"

		blockCrumble(Blocks.STONE) assertsIs "particle block_crumble{block_state:{Name:\"minecraft:stone\"}}"
		blockCrumble(Blocks.STONE_SLAB(states = mapOf("half" to "top"))) assertsIs "particle block_crumble{block_state:{Name:\"minecraft:stone_slab\",Properties:{half:\"top\"}}}"

		blockMarker(Blocks.STONE) assertsIs "particle block_marker{block_state:{Name:\"minecraft:stone\"}}"
		fallingDust(Blocks.STONE) assertsIs "particle falling_dust{block_state:{Name:\"minecraft:stone\"}}"

		// encode color as decimal
		dust(Color.PURPLE, 2.0) assertsIs "particle dust{color:11141375,scale:2.0d}"
		dust(rgb(0xabcdef), 2.0) assertsIs "particle dust{color:11259375,scale:2.0d}"

		dustColorTransition(
			Color.BLUE,
			2.0,
			Color.RED
		) assertsIs "particle dust_color_transition{from_color:5592575,to_color:16733525,scale:2.0d}"

		entityEffect(Color.GREEN) assertsIs "particle entity_effect{color:[0.3333333333333333d,1.0d,0.3333333333333333d]}"

		item(Items.DIAMOND_SWORD {
			enchantments {
				enchantment(Enchantments.SHARPNESS, 5)
				enchantment(Enchantments.KNOCKBACK, 2)
			}
		}) assertsIs """particle item{item:{id:"minecraft:diamond_sword",components:{enchantments:{"minecraft:sharpness":5,"minecraft:knockback":2}}}}"""

		particle(Particles.ASH) assertsIs "particle minecraft:ash"

		sculkCharge(PI / 2) assertsIs "particle sculk_charge 1.5707963267948966"
		shriek(100) assertsIs "particle shriek 100"

		trail(
			Color.RED,
			Triple(1, 2, 3)
		) assertsIs "particle trail{color:16733525,target:[1,2,3]}"
		trail(
			Color.BLUE,
			Triple(0, 0, 0)
		) assertsIs "particle trail{color:5592575,target:[0,0,0]}"

		vibration(vec3(1, 2, 3), 10) assertsIs "particle vibration 1 2 3 10"
	}
}
