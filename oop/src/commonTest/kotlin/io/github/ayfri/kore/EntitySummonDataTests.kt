package io.github.ayfri.kore

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.arguments.numbers.ticks
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.entities.summon.*
import io.github.ayfri.kore.teams.team
import io.github.ayfri.kore.utils.set
import io.kotest.core.spec.style.FunSpec

private fun summonData(block: ItemEntitySummonData.() -> Unit) = ItemEntitySummonData().apply(block).toNbt().toString()

class EntitySummonDataTests : FunSpec({
	test("fields write the game keys, short air and fall_distance included") {
		summonData {
			air = 300
			customData { this["quest"] = "intro" }
			customNameVisible = true
			fallDistance = 2.5
			fire = 40
			glowing = true
			hasVisualFire = true
			invulnerable = true
			invulnerableTime = 10
			noGravity = true
			onGround = false
			portalCooldown = 5
			silent = true
			team = "red"
			ticksFrozen = 7
		} assertsIs """{Air:300s,CustomNameVisible:1b,data:{quest:"intro"},fall_distance:2.5d,Fire:40s,Glowing:1b,HasVisualFire:1b,Invulnerable:1b,invulnerable_time:10,NoGravity:1b,OnGround:0b,PortalCooldown:5,Silent:1b,Team:"red",TicksFrozen:7}"""
	}

	test("builders write the same keys") {
		summonData {
			customName("Shop", Color.GOLD)
			frozen(1.seconds)
			invulnerableFor(3.seconds)
			motion(0.0, 0.6, 0.0)
			onFire(5.seconds)
			rotation(90f, 0f)
			tag("shop", "npc")
			tag("extra")
			team(team("red"))
		} assertsIs """{CustomName:{text:"Shop",color:"gold"},Fire:100s,invulnerable_time:60,Motion:[0.0d,0.6d,0.0d],Rotation:[90.0f,0.0f],Tags:["shop","npc","extra"],Team:"red",TicksFrozen:20}"""
	}

	test("fire stops at the largest short") {
		summonData { onFire(100000.ticks) } assertsIs "{Fire:32767s}"
	}
})
