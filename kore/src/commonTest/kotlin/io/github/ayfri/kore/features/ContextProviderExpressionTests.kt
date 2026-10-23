package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.compute
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.loottables.LootPool
import io.github.ayfri.kore.features.predicates.providers.*
import io.github.ayfri.kore.features.predicates.types.EntityTarget
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.arguments.tagged.ContextFloatProviderTagArgument
import io.github.ayfri.kore.generated.arguments.tagged.ContextIntProviderTagArgument
import io.github.ayfri.kore.generated.arguments.types.ContextFloatProviderArgument
import io.github.ayfri.kore.generated.arguments.types.ContextIntProviderArgument
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private const val KILLS = """{"type":"minecraft:score","score":"kills","target":{"type":"minecraft:context","target":"this"}}"""
private const val DEATHS = """{"type":"minecraft:score","score":"deaths","target":{"type":"minecraft:context","target":"this"}}"""
private const val KILLS_SNBT = """{type:"minecraft:score",score:kills,target:{type:"minecraft:context",target:this}}"""

private fun NumberProviderScope.kills() = score("kills", EntityTarget.THIS)

private fun NumberProviderScope.deaths() = score("deaths", EntityTarget.THIS)

private fun NumberProviderScope.killsFloat() = kills().toFloat()

private fun NumberProviderScope.deathsFloat() = deaths().toFloat()

private val speed = ContextFloatProviderArgument("speed", "my_pack")

private fun DataPack.intExpressionTests() {
	fun check(expected: String, block: NumberProviderScope.() -> ContextIntProvider) {
		jsonEncoder.encodeToString(ContextIntProvider.serializer(), object : NumberProviderScope {}.block()) shouldBe expected
	}

	check("""{"type":"minecraft:add","inputs":[$KILLS,7]}""") { kills() + 3 + 4 }
	check("""{"type":"minecraft:add","inputs":[$KILLS,3]}""") { kills() + 1 + 2 }
	check("""{"type":"minecraft:add","inputs":[$KILLS,3]}""") { 1 + kills() + 2 }
	check("""{"type":"minecraft:add","inputs":[$KILLS,$DEATHS,3]}""") { (kills() + 1) + (deaths() + 2) }
	check("""{"type":"minecraft:add","inputs":[$KILLS,1]}""") { 1 + kills() }
	check("""{"type":"minecraft:add","inputs":[$KILLS,5]}""") { kills() + constant(2) + constant(3) }
	check(KILLS) { kills() + 0 }
	check(KILLS) { 0 + kills() }
	check("5") { constant(2) + constant(3) }
	check("0") { constant(2) + -2 }

	check("""{"type":"minecraft:add","inputs":[$KILLS,2147483647,1]}""") { kills() + Int.MAX_VALUE + 1 }
	check("""{"type":"minecraft:add","inputs":[$KILLS,2000000000,2000000000]}""") { kills() + 2_000_000_000 + 2_000_000_000 }
	check("""{"type":"minecraft:add","inputs":[$KILLS,2147483646]}""") { kills() + Int.MAX_VALUE + -1 }
	check(KILLS) { kills() + 2_000_000_000 + -2_000_000_000 }
	check("""{"type":"minecraft:add","inputs":[2147483647,1]}""") { constant(Int.MAX_VALUE) + constant(1) }

	check("""{"type":"minecraft:mul","inputs":[$KILLS,6]}""") { kills() * 2 * 3 }
	check("""{"type":"minecraft:mul","inputs":[$KILLS,3]}""") { 3 * kills() }
	check("""{"type":"minecraft:mul","inputs":[$KILLS,65536,65536]}""") { kills() * 65536 * 65536 }
	check("""{"type":"minecraft:mul","inputs":[$KILLS,0]}""") { kills() * 0 }
	check(KILLS) { kills() * 1 }
	check(KILLS) { 1 * kills() }
	check("6") { constant(2) * constant(3) }
	check("""{"type":"minecraft:mul","inputs":[{"type":"minecraft:add","inputs":[$KILLS,1]},2]}""") { (kills() + 1) * 2 }
	check("""{"type":"minecraft:add","inputs":[{"type":"minecraft:mul","inputs":[$KILLS,2]},1]}""") { kills() * 2 + 1 }

	check("""{"type":"minecraft:sub","left":$KILLS,"right":1}""") { kills() - 1 }
	check("""{"type":"minecraft:sub","left":10,"right":$KILLS}""") { 10 - kills() }
	check("2") { constant(5) - constant(3) }
	check("""{"type":"minecraft:sub","left":-2147483648,"right":1}""") { constant(Int.MIN_VALUE) - constant(1) }

	check("-5") { -constant(5) }
	check("""{"type":"minecraft:negate","input":-2147483648}""") { -constant(Int.MIN_VALUE) }
	check("""{"type":"minecraft:negate","input":$KILLS}""") { -kills() }

	check("""{"type":"minecraft:mod","left":$KILLS,"right":7}""") { kills() % 7 }
	check("""{"type":"minecraft:mod","left":$KILLS,"right":0}""") { kills() % 0 }
	check("""{"type":"minecraft:mod","left":7,"right":0}""") { constant(7) % constant(0) }
	check("""{"type":"minecraft:mod","left":10,"right":$KILLS}""") { 10 % kills() }
	check("""{"type":"minecraft:div","left":6,"right":3}""") { constant(6) / constant(3) }
	check("""{"type":"minecraft:div","left":$KILLS,"right":$DEATHS}""") { kills() / deaths() }
	check("""{"type":"minecraft:div","left":10,"right":$KILLS}""") { 10 / kills() }
	check("""{"type":"minecraft:floor_div","left":7,"right":2}""") { constant(7) floorDiv constant(2) }
	check("""{"type":"minecraft:floor_div","left":$KILLS,"right":3}""") { kills() floorDiv 3 }
	check("""{"type":"minecraft:floor_mod","left":-7,"right":2}""") { constant(-7) floorMod constant(2) }
	check("""{"type":"minecraft:floor_mod","left":$KILLS,"right":3}""") { kills() floorMod 3 }
	check("""{"type":"minecraft:pow","base":2,"exponent":3}""") { constant(2) pow constant(3) }
	check("""{"type":"minecraft:pow","base":$KILLS,"exponent":2}""") { kills() pow 2 }

	val bonuses = ContextIntProviderTagArgument("bonuses", "my_pack")
	val bonusesAdd = """{"type":"minecraft:add","inputs":"#my_pack:bonuses"}"""
	check("""{"type":"minecraft:add","inputs":[$bonusesAdd,1]}""") { add(bonuses) + 1 }
	check("""{"type":"minecraft:add","inputs":[$KILLS,$bonusesAdd]}""") { kills() + add(bonuses) }
	check("""{"type":"minecraft:add","inputs":[$bonusesAdd,$bonusesAdd]}""") { add(bonuses) + add(bonuses) }
	check(bonusesAdd) { add(bonuses) + 0 }
	check("""{"type":"minecraft:mul","inputs":[{"type":"minecraft:mul","inputs":"#my_pack:bonuses"},2]}""") { mul(bonuses) * 2 }
	check("""{"type":"minecraft:add","inputs":[$KILLS,8]}""") { add(kills(), constant(1), constant(2)) + 5 }

	check("""{"type":"minecraft:max","inputs":[{"type":"minecraft:min","inputs":[$KILLS,10]},0]}""") { kills().coerceIn(0, 10) }
	check("""{"type":"minecraft:max","inputs":[$KILLS,1]}""") { kills().coerceAtLeast(1) }
	check("""{"type":"minecraft:min","inputs":[$KILLS,9]}""") { kills().coerceAtMost(9) }
	check("""{"type":"minecraft:max","inputs":[{"type":"minecraft:min","inputs":[$KILLS,3]},3]}""") { kills().coerceIn(3, 3) }
	shouldThrow<IllegalArgumentException> { object : NumberProviderScope {}.run { kills().coerceIn(5, 1) } }
	check("""{"type":"minecraft:min","inputs":[$KILLS,5]}""") { min(kills(), 5) }
	check("""{"type":"minecraft:max","inputs":[$KILLS,5]}""") { max(kills(), 5) }
}

private fun DataPack.floatExpressionTests() {
	fun check(expected: String, block: NumberProviderScope.() -> ContextFloatProvider) {
		jsonEncoder.encodeToString(ContextFloatProvider.serializer(), object : NumberProviderScope {}.block()) shouldBe expected
	}

	val fromKills = """{"type":"minecraft:from_int","input":$KILLS}"""
	val fromDeaths = """{"type":"minecraft:from_int","input":$DEATHS}"""
	val speedJson = "\"my_pack:speed\""

	check(fromKills) { killsFloat() }
	check("""{"type":"minecraft:add","inputs":[$fromKills,$fromDeaths,1.0]}""") { killsFloat() + deathsFloat() + 1f }
	check("""{"type":"minecraft:add","inputs":[$fromKills,$fromDeaths,1.0]}""") { (killsFloat() + deathsFloat()) + 1f }
	check("""{"type":"minecraft:add","inputs":[$fromKills,{"type":"minecraft:add","inputs":[$fromDeaths,1.0]}]}""") { killsFloat() + (deathsFloat() + 1f) }
	check("""{"type":"minecraft:add","inputs":[$fromKills,0.1,0.2]}""") { killsFloat() + 0.1f + 0.2f }
	check("""{"type":"minecraft:add","inputs":[1.5,$fromKills]}""") { 1.5f + killsFloat() }
	check("""{"type":"minecraft:add","inputs":[2.0,3.0]}""") { constant(2f) + constant(3f) }
	check(fromKills) { killsFloat() + 0f }
	check(fromKills) { 0f + killsFloat() }

	check("""{"type":"minecraft:mul","inputs":[$fromKills,$fromDeaths,2.0]}""") { killsFloat() * deathsFloat() * 2f }
	check("""{"type":"minecraft:mul","inputs":[$fromKills,{"type":"minecraft:mul","inputs":[$fromDeaths,2.0]}]}""") { killsFloat() * (deathsFloat() * 2f) }
	check("""{"type":"minecraft:mul","inputs":[$fromKills,0.5,0.5]}""") { killsFloat() * 0.5f * 0.5f }
	check("""{"type":"minecraft:mul","inputs":[1.5,$fromKills]}""") { 1.5f * killsFloat() }
	check(fromKills) { killsFloat() * 1f }
	check(fromKills) { 1f * killsFloat() }
	check("""{"type":"minecraft:mul","inputs":[2.0,3.0]}""") { constant(2f) * constant(3f) }
	check("""{"type":"minecraft:add","inputs":[{"type":"minecraft:mul","inputs":[$fromKills,1.5]},2.0]}""") { killsFloat() * 1.5f + 2f }

	check("""{"type":"minecraft:sub","left":$fromKills,"right":0.5}""") { killsFloat() - 0.5f }
	check("""{"type":"minecraft:sub","left":2.0,"right":$fromKills}""") { 2f - killsFloat() }
	check("""{"type":"minecraft:sub","left":1.0,"right":2.0}""") { constant(1f) - constant(2f) }
	check("""{"type":"minecraft:negate","input":$fromKills}""") { -killsFloat() }
	check("""{"type":"minecraft:negate","input":1.0}""") { -constant(1f) }
	check("""{"type":"minecraft:div","left":$fromKills,"right":2.0}""") { killsFloat() / 2f }
	check("""{"type":"minecraft:div","left":6.0,"right":3.0}""") { constant(6f) / constant(3f) }
	check("""{"type":"minecraft:div","left":1.0,"right":$fromKills}""") { 1f / killsFloat() }
	check("""{"type":"minecraft:mod","left":$fromKills,"right":360.0}""") { killsFloat() % 360f }
	check("""{"type":"minecraft:mod","left":360.0,"right":$fromKills}""") { 360f % killsFloat() }
	check("""{"type":"minecraft:pow","base":$fromKills,"exponent":2.0}""") { killsFloat() pow 2f }
	check("""{"type":"minecraft:pow","base":2.0,"exponent":3.0}""") { constant(2f) pow constant(3f) }

	val speeds = ContextFloatProviderTagArgument("speeds", "my_pack")
	val speedsAdd = """{"type":"minecraft:add","inputs":"#my_pack:speeds"}"""
	check("""{"type":"minecraft:add","inputs":[$speedsAdd,1.0]}""") { add(speeds) + 1f }
	check("""{"type":"minecraft:add","inputs":[$fromKills,$speedsAdd]}""") { killsFloat() + add(speeds) }
	check(speedsAdd) { add(speeds) + 0f }
	check("""{"type":"minecraft:mul","inputs":[{"type":"minecraft:mul","inputs":"#my_pack:speeds"},2.0]}""") { mul(speeds) * 2f }

	check("""{"type":"minecraft:max","inputs":[{"type":"minecraft:min","inputs":[$fromKills,2.0]},0.0]}""") { killsFloat().coerceIn(0f, 2f) }
	check("""{"type":"minecraft:max","inputs":[$fromKills,0.5]}""") { killsFloat().coerceAtLeast(0.5f) }
	check("""{"type":"minecraft:min","inputs":[$fromKills,0.5]}""") { killsFloat().coerceAtMost(0.5f) }
	shouldThrow<IllegalArgumentException> { object : NumberProviderScope {}.run { killsFloat().coerceIn(2f, 1f) } }
	check("""{"type":"minecraft:min","inputs":[$fromKills,5.0]}""") { min(killsFloat(), 5f) }
	check("""{"type":"minecraft:max","inputs":[$fromKills,5.0]}""") { max(killsFloat(), 5f) }

	check("""{"type":"minecraft:add","inputs":[$speedJson,0.5]}""") { reference(speed) + 0.5f }
}

private fun DataPack.floatToIntTests() {
	val speedJson = "\"my_pack:speed\""
	val provider = object : NumberProviderScope {}.run { reference(speed).toInt() + 1 }
	jsonEncoder.encodeToString(ContextIntProvider.serializer(), provider) shouldBe
		"""{"type":"minecraft:add","inputs":[{"type":"minecraft:from_float","input":$speedJson},1]}"""
}

private fun DataPack.computeExpressionTests() {
	val bonus = ContextIntProviderArgument("bonus", "my_pack")

	load {
		compute(self()) { (kills() + 3 + 4) % 7 } assertsIs
			"""compute entity @s integer {type:"minecraft:mod",left:{type:"minecraft:add",inputs:[$KILLS_SNBT,7]},right:7}"""
		compute(self()) { kills() + Int.MAX_VALUE + 1 } assertsIs
			"""compute entity @s integer {type:"minecraft:add",inputs:[$KILLS_SNBT,2147483647,1]}"""
		compute(self()) { kills() + reference(bonus) * 2 } assertsIs
			"""compute entity @s integer {type:"minecraft:add",inputs:[$KILLS_SNBT,{type:"minecraft:mul",inputs:["my_pack:bonus",2]}]}"""
		compute(self()) { kills() * 2 - 1 } assertsIs
			"""compute entity @s integer {type:"minecraft:sub",left:{type:"minecraft:mul",inputs:[$KILLS_SNBT,2]},right:1}"""
		compute(self()) { sqrt(kills().toFloat() * 1.5f + 2f) } assertsIs
			"""compute entity @s float {type:"minecraft:sqrt",input:{type:"minecraft:add",inputs:[{type:"minecraft:mul",inputs:[{type:"minecraft:from_int",input:$KILLS_SNBT},1.5d]},2]}}"""
		compute(10f) { killsFloat() / 4f } assertsIs
			"""compute default float {type:"minecraft:div",left:{type:"minecraft:from_int",input:$KILLS_SNBT},right:4} 10"""
		compute { (killsFloat() * 0.1f).toInt() }.toString() shouldBe
			"""compute default integer {type:"minecraft:from_float",input:{type:"minecraft:mul",inputs:[{type:"minecraft:from_int",input:$KILLS_SNBT},0.1d]}}"""
	}
}

class ContextProviderExpressionTests : FunSpec({
	test("int provider expressions") {
		dataPack("contextProviderExpressions") { intExpressionTests() }
	}

	test("float provider expressions") {
		dataPack("contextProviderExpressions") { floatExpressionTests() }
	}

	test("a float provider converts back to an int in an int expression") {
		dataPack("contextProviderExpressions") { floatToIntTests() }
	}

	test("provider expressions in a compute command") {
		dataPack("contextProviderExpressions") { computeExpressionTests() }
	}

	test("a number leads an expression inside a loot pool") {
		val pool = LootPool(rolls = ConstantContextIntProvider(1))
		with(pool) { rolls = 2 + score("kills", EntityTarget.THIS) }
		pool.rolls shouldBe AddContextIntProvider(listOf(ScoreContextIntProvider(null, "kills", ContextScoreProvider(EntityTarget.THIS)), ConstantContextIntProvider(2)))
	}
})
