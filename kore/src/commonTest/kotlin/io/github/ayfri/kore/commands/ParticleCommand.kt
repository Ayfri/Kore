package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.argb
import io.github.ayfri.kore.arguments.colors.rgb
import io.github.ayfri.kore.arguments.components.item.enchantment
import io.github.ayfri.kore.arguments.components.item.enchantments
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.particle.ParticleMode
import io.github.ayfri.kore.commands.particle.particle
import io.github.ayfri.kore.data.particle.*
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.Particles
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
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

	particle(Particles.ASH, vec3(), count = 5) assertsIs "particle minecraft:ash ~ ~ ~ 0 0 0 0 5"
	particle(Particles.ASH, vec3(), viewers = allEntities()) assertsIs "particle minecraft:ash ~ ~ ~ 0 0 0 0 0 normal @e"
	shouldThrow<IllegalArgumentException> { particle(Particles.ASH, count = 5) }
	shouldThrow<IllegalArgumentException> { particle(Particles.DUST, vec3()) }
	shouldThrow<IllegalArgumentException> { particle(blockParticle(Blocks.STONE, Particles.FLAME)) }

	particle(simpleParticle(Particles.FLAME), vec3()) assertsIs "particle minecraft:flame ~ ~ ~"

	particle(blockParticle(Blocks.STONE_SLAB(states = mapOf("half" to "top")))) assertsIs "particle minecraft:block{block_state:{id:\"minecraft:stone_slab\",properties:{half:top}}}"
	particle(blockParticle(Blocks.STONE, Particles.BLOCK_CRUMBLE)) assertsIs "particle minecraft:block_crumble{block_state:\"minecraft:stone\"}"
	particle(blockParticle(Blocks.STONE, Particles.BLOCK_MARKER)) assertsIs "particle minecraft:block_marker{block_state:\"minecraft:stone\"}"
	particle(blockParticle(Blocks.SAND, Particles.DUST_PILLAR)) assertsIs "particle minecraft:dust_pillar{block_state:\"minecraft:sand\"}"
	particle(blockParticle(Blocks.STONE, Particles.FALLING_DUST)) assertsIs "particle minecraft:falling_dust{block_state:\"minecraft:stone\"}"

	particle(colorParticle(Color.GREEN)) assertsIs "particle minecraft:entity_effect{color:-11141291}"
	particle(colorParticle(Color.WHITE, Particles.FLASH)) assertsIs "particle minecraft:flash{color:-1}"
	particle(colorParticle(Color.RED, Particles.TINTED_LEAVES)) assertsIs "particle minecraft:tinted_leaves{color:-43691}"
	particle(colorParticle(argb(128, 255, 0, 0))) assertsIs "particle minecraft:entity_effect{color:-2130771968}"

	particle(dragonBreathParticle()) assertsIs "particle minecraft:dragon_breath"
	particle(dragonBreathParticle(0.5f)) assertsIs "particle minecraft:dragon_breath{power:0.5f}"

	particle(dustParticle(Color.PURPLE, 2f)) assertsIs "particle minecraft:dust{color:11141375,scale:2.0f}"
	particle(dustParticle(rgb(0xabcdef))) assertsIs "particle minecraft:dust{color:11259375,scale:1.0f}"
	particle(dustColorTransitionParticle(Color.BLUE, Color.RED, 2f)) assertsIs "particle minecraft:dust_color_transition{from_color:5592575,scale:2.0f,to_color:16733525}"

	particle(effectParticle()) assertsIs "particle minecraft:effect"
	particle(effectParticle(Color.GREEN, 2f, Particles.INSTANT_EFFECT)) assertsIs "particle minecraft:instant_effect{color:5635925,power:2.0f}"

	particle(geyserParticle(3)) assertsIs "particle minecraft:geyser{water_blocks:3}"
	particle(geyserParticle(4, Particles.GEYSER_PLUME)) assertsIs "particle minecraft:geyser_plume{water_blocks:4}"
	particle(geyserBurstParticle(3, 0.5f)) assertsIs "particle minecraft:geyser_base{burst_impulse_base:0.5f,water_blocks:3}"
	particle(geyserBurstParticle(4, 1.5f, Particles.GEYSER_POOF)) assertsIs "particle minecraft:geyser_poof{burst_impulse_base:1.5f,water_blocks:4}"

	particle(itemParticle(Items.DIAMOND_SWORD {
		enchantments {
			enchantment(Enchantments.SHARPNESS, 5)
			enchantment(Enchantments.KNOCKBACK, 2)
		}
	})) assertsIs """particle minecraft:item{item:{id:"minecraft:diamond_sword",components:{enchantments:{"minecraft:sharpness":5,"minecraft:knockback":2}}}}"""

	particle(sculkChargeParticle((PI / 2).toFloat())) assertsIs "particle minecraft:sculk_charge{roll:1.5707964f}"
	particle(shriekParticle(100)) assertsIs "particle minecraft:shriek{delay:100}"

	particle(trailParticle(Color.RED, 10, 1.0, 2.0, 3.0)) assertsIs "particle minecraft:trail{color:16733525,duration:10,target:[1.0d,2.0d,3.0d]}"

	particle(
		vibrationParticle(1, 2, 3, 10),
		vec3(),
		vec3(0, 0, 0),
		0.0,
		1,
		ParticleMode.FORCE
	) assertsIs "particle minecraft:vibration{arrival_in_ticks:10,destination:{pos:[1,2,3],type:block}} ~ ~ ~ 0 0 0 0 1 force"
}

class ParticleCommandTests : FunSpec({
	test("particle") {
		dataPack("unit_tests") {
			load { particleTests() }
		}
	}
})
