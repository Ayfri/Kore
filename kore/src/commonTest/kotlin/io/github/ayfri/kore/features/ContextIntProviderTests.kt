package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.predicates.conditions.SurvivesExplosion
import io.github.ayfri.kore.features.predicates.conditions.survivesExplosion
import io.github.ayfri.kore.features.predicates.providers.*
import io.github.ayfri.kore.features.predicates.types.EntityTarget
import io.github.ayfri.kore.generated.ContextIntProviders
import io.github.ayfri.kore.generated.EnvironmentAttributes
import io.github.ayfri.kore.generated.arguments.tagged.ContextIntProviderTagArgument
import io.github.ayfri.kore.generated.arguments.types.ContextFloatProviderArgument
import io.github.ayfri.kore.generated.arguments.types.ContextIntProviderArgument
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

fun DataPack.contextIntProviderTests() {
	fun check(expected: String, block: NumberProviderScope.() -> ContextIntProvider) {
		val provider = object : NumberProviderScope {}.block()
		jsonEncoder.encodeToString(ContextIntProvider.serializer(), provider) assertsIs expected
		roundTrip<ContextIntProvider>(provider)
	}

	val bonus = ContextIntProviderArgument("bonus", "my_pack")
	val kills = """{"type":"minecraft:score","score":"kills","target":{"type":"minecraft:context","target":"this"}}"""

	check("1") { constant(1) }
	check("""{"type":"minecraft:abs","input":-3}""") { abs(constant(-3)) }
	check("""{"type":"minecraft:add","inputs":[1,"my_pack:bonus"]}""") { add(constant(1), reference(bonus)) }
	check("""{"type":"minecraft:add","inputs":1}""") { add(constant(1)) }
	check("""{"type":"minecraft:add","inputs":"#my_pack:bonuses"}""") { add(ContextIntProviderTagArgument("bonuses", "my_pack")) }
	shouldThrow<IllegalArgumentException> { object : NumberProviderScope {}.add(emptyList<ContextIntProvider>()) }
	shouldThrow<IllegalArgumentException> { object : NumberProviderScope {}.avg(emptyList<ContextIntProvider>()) }
	val bonuses = ContextIntProviderReference(ContextIntProviderTagArgument("bonuses", "my_pack"))
	shouldThrow<IllegalArgumentException> { AddContextIntProvider(listOf(ConstantContextIntProvider(1), bonuses)) }
	shouldThrow<IllegalArgumentException> { MulContextIntProvider(listOf(bonuses, ContextIntProviderReference(bonus))) }
	shouldThrow<IllegalArgumentException> { MinContextIntProvider(listOf(bonuses, bonuses)) }
	shouldThrowAny { jsonEncoder.decodeFromString(ContextIntProvider.serializer(), """{"type":"minecraft:max","inputs":[1,"#my_pack:t"]}""") }
	check("""{"type":"minecraft:add","inputs":[1,2]}""") { add(listOf(constant(1), constant(2))) }
	check("""{"type":"minecraft:avg","inputs":[1,{"type":"minecraft:uniform","max":3,"min":1}]}""") { avg(constant(1), uniform(1, 3)) }
	check("""{"type":"minecraft:binomial","n":10,"p":0.5}""") { binomial(10, 0.5f) }
	check("""{"type":"minecraft:binomial","n":"my_pack:bonus","p":"my_pack:chance"}""") {
		binomial(reference(bonus), reference(ContextFloatProviderArgument("chance", "my_pack")))
	}
	check("""{"type":"minecraft:conditional","condition":{"type":"minecraft:survives_explosion"},"on_false":0,"on_true":1}""") {
		conditional(SurvivesExplosion, 1, 0)
	}
	check("""{"type":"minecraft:conditional","condition":{"type":"minecraft:survives_explosion"},"on_true":1}""") {
		conditional(SurvivesExplosion, constant(1))
	}
	check("""{"type":"minecraft:conditional","condition":"my_pack:fast_cooking","on_false":1600,"on_true":800}""") {
		conditional(PredicateArgument("fast_cooking", "my_pack"), 800, 1600)
	}
	check("""{"type":"minecraft:conditional","condition":{"type":"minecraft:survives_explosion"},"on_false":2,"on_true":3}""") {
		conditional(3, 2) { survivesExplosion() }
	}
	check("""{"type":"minecraft:div","left":9,"right":2}""") { div(constant(9), constant(2)) }
	check("""{"type":"minecraft:floor_div","left":-9,"right":2}""") { floorDiv(constant(-9), constant(2)) }
	check("""{"type":"minecraft:floor_mod","left":-9,"right":2}""") { floorMod(constant(-9), constant(2)) }
	check("""{"type":"minecraft:from_float","input":{"type":"minecraft:enchantment_level","amount":5}}""") { fromFloat(enchantmentLevel(5)) }
	check("""{"type":"minecraft:from_float","input":2.5}""") { fromFloat(constant(2.5f)) }
	check("""{"type":"minecraft:environment_attribute","attribute":"minecraft:gameplay/baby_villager_activity"}""") {
		intEnvironmentAttribute(EnvironmentAttributes.Gameplay.BABY_VILLAGER_ACTIVITY)
	}
	check("""{"type":"minecraft:max","inputs":[1,2]}""") { max(constant(1), constant(2)) }
	check("""{"type":"minecraft:max","inputs":"#my_pack:t"}""") { max(ContextIntProviderTagArgument("t", "my_pack")) }
	check("""{"type":"minecraft:min","inputs":[$kills,5]}""") { min(score("kills", EntityTarget.THIS), constant(5)) }
	check("""{"type":"minecraft:mod","left":9,"right":2}""") { mod(constant(9), constant(2)) }
	check("""{"type":"minecraft:mul","inputs":["my_pack:bonus",2]}""") { mul(reference(bonus), constant(2)) }
	check("""{"type":"minecraft:negate","input":4}""") { negate(constant(4)) }
	check(
		"""{"type":"minecraft:number_dispatcher","cases":[{"condition":{"type":"minecraft:survives_explosion"},"value":1}],"default":{"type":"minecraft:weighted_list","distribution":[{"data":1,"weight":30},{"data":0,"weight":70}]}}"""
	) {
		intNumberDispatcher(default = intWeightedList {
			entry(30, 1)
			entry(70, 0)
		}) {
			case(1) { survivesExplosion() }
		}
	}
	check("""{"type":"minecraft:number_dispatcher","cases":[{"condition":"my_pack:is_night","value":2},{"condition":{"type":"minecraft:survives_explosion"},"value":"my_pack:bonus"}]}""") {
		intNumberDispatcher {
			case(PredicateArgument("is_night", "my_pack"), 2)
			case(SurvivesExplosion, reference(bonus))
		}
	}
	check("""{"type":"minecraft:number_dispatcher","cases":[],"default":7}""") { intNumberDispatcher(7) {} }
	check("""{"type":"minecraft:pow","base":2,"exponent":3}""") { pow(constant(2), constant(3)) }
	check(""""my_pack:bonus"""") { reference(bonus) }
	check(""""minecraft:cooking/time_coal"""") { reference(ContextIntProviders.Cooking.TIME_COAL) }
	check(kills) { score("kills", EntityTarget.THIS) }
	check("""{"type":"minecraft:score","score":"kills","target":{"type":"minecraft:fixed","name":"Steve"}}""") { score("kills", "Steve") }
	check("""{"type":"minecraft:score","fallback":4,"score":"kills","target":{"type":"minecraft:fixed","name":"Steve"}}""") {
		score("kills", fixedScore("Steve"), constant(4))
	}
	check("""{"type":"minecraft:score","fallback":0,"score":"kills","target":{"type":"minecraft:context","target":"this"}}""") {
		score("kills", EntityTarget.THIS, 0)
	}
	check("""{"type":"minecraft:score","fallback":4,"score":"kills","target":{"type":"minecraft:fixed","name":"Steve"}}""") {
		score("kills", "Steve", 4)
	}
	check("""{"type":"minecraft:score","fallback":4,"score":"kills","target":{"type":"minecraft:fixed","name":"Steve"}}""") {
		score("kills", fixedScore("Steve"), 4)
	}
	check("""{"type":"minecraft:storage","fallback":0,"path":"a","storage":"my_pack:data"}""") { intStorage("my_pack:data", "a", 0) }
	check("""{"type":"minecraft:storage","path":"player.health","storage":"my_pack:data"}""") { intStorage("my_pack:data", "player.health") }
	check("""{"type":"minecraft:storage","fallback":1,"path":"a","storage":"my_pack:data"}""") { intStorage("my_pack:data", "a", constant(1)) }
	check("""{"type":"minecraft:sub","left":5,"right":2}""") { sub(constant(5), constant(2)) }
	check("""{"type":"minecraft:uniform","max":5,"min":1}""") { uniform(1, 5) }
	check("""{"type":"minecraft:uniform","max":5,"min":"minecraft:cooking/time_coal"}""") {
		uniform(reference(ContextIntProviders.Cooking.TIME_COAL), constant(5))
	}
	check("""{"type":"minecraft:weighted_list","distribution":[{"data":1,"weight":30},{"data":0,"weight":70}]}""") {
		intWeightedList {
			entry(30, 1)
			entry(70, constant(0))
		}
	}

	jsonEncoder.decodeFromString(ContextIntProvider.serializer(), "5") assertsIs ConstantContextIntProvider(5)
	jsonEncoder.decodeFromString(ContextIntProvider.serializer(), """{"type":"minecraft:constant","value":5}""") assertsIs
		ConstantContextIntProvider(5)
	jsonEncoder.decodeFromString(ContextIntProvider.serializer(), "\"my_pack:r\"")
		.shouldBeInstanceOf<ContextIntProviderReference>().id
		.shouldBeInstanceOf<ContextIntProviderArgument>().asString() assertsIs "my_pack:r"
	jsonEncoder.decodeFromString(ContextIntProvider.serializer(), """{"type":"minecraft:add","inputs":"#my_pack:t"}""")
		.shouldBeInstanceOf<AddContextIntProvider>().inputs.single()
		.shouldBeInstanceOf<ContextIntProviderReference>().id
		.shouldBeInstanceOf<ContextIntProviderTagArgument>().asString() assertsIs "#my_pack:t"
	jsonEncoder.decodeFromString(ContextIntProvider.serializer(), """{"type":"minecraft:max","inputs":[1,2]}""")
		.shouldBeInstanceOf<MaxContextIntProvider>().inputs.size shouldBe 2

	shouldThrowAny { jsonEncoder.decodeFromString(ContextIntProvider.serializer(), """{"min":1,"max":2}""") }
	shouldThrowAny { jsonEncoder.decodeFromString(ContextIntProvider.serializer(), """{"type":"minecraft:sum","operands":[1]}""") }
	shouldThrowAny { jsonEncoder.decodeFromString(ContextIntProvider.serializer(), """{"type":"minecraft:ceil","input":1}""") }
}

class ContextIntProviderTests : FunSpec({
	test("context int providers") {
		dataPack("contextIntProviders") {
			contextIntProviderTests()
		}
	}

	test("an int constant is written without a decimal part") {
		dataPack("contextIntProviderFormat") {
			jsonEncoder.encodeToString(ContextIntProvider.serializer(), ConstantContextIntProvider(1)) shouldBe "1"
			jsonEncoder.encodeToString(ContextIntProvider.serializer(), ConstantContextIntProvider(-7)) shouldBe "-7"
		}
	}
})
