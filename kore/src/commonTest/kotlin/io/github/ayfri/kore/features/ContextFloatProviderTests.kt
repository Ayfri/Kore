package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.predicates.conditions.SurvivesExplosion
import io.github.ayfri.kore.features.predicates.conditions.survivesExplosion
import io.github.ayfri.kore.features.predicates.providers.*
import io.github.ayfri.kore.generated.ContextFloatProviders
import io.github.ayfri.kore.generated.EnvironmentAttributes
import io.github.ayfri.kore.generated.arguments.tagged.ContextFloatProviderTagArgument
import io.github.ayfri.kore.generated.arguments.types.ContextFloatProviderArgument
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

fun DataPack.contextFloatProviderTests() {
	fun check(expected: String, block: NumberProviderScope.() -> ContextFloatProvider) {
		val provider = object : NumberProviderScope {}.block()
		jsonEncoder.encodeToString(ContextFloatProvider.serializer(), provider) assertsIs expected
		roundTrip<ContextFloatProvider>(provider)
	}

	val speed = ContextFloatProviderArgument("speed", "my_pack")

	check("1.0") { constant(1f) }
	check("""{"type":"minecraft:abs","input":-1.5}""") { abs(constant(-1.5f)) }
	check("""{"type":"minecraft:add","inputs":[1.0,"my_pack:speed"]}""") { add(constant(1f), reference(speed)) }
	check("""{"type":"minecraft:add","inputs":1.0}""") { add(constant(1f)) }
	check("""{"type":"minecraft:add","inputs":"#my_pack:speeds"}""") { add(ContextFloatProviderTagArgument("speeds", "my_pack")) }
	shouldThrow<IllegalArgumentException> { object : NumberProviderScope {}.add(emptyList<ContextFloatProvider>()) }
	shouldThrow<IllegalArgumentException> { object : NumberProviderScope {}.avg(emptyList<ContextFloatProvider>()) }
	check("""{"type":"minecraft:avg","inputs":[1.0,{"type":"minecraft:uniform","max":3.0,"min":1.0}]}""") { avg(constant(1f), uniform(1f, 3f)) }
	check("""{"type":"minecraft:ceil","input":1.2}""") { ceil(constant(1.2f)) }
	check("""{"type":"minecraft:conditional","condition":{"type":"minecraft:survives_explosion"},"on_false":2.0,"on_true":5.0}""") {
		conditional(SurvivesExplosion, 5f, 2f)
	}
	check("""{"type":"minecraft:conditional","condition":{"type":"minecraft:survives_explosion"},"on_true":{"type":"minecraft:uniform","max":3.0,"min":1.0}}""") {
		conditional(SurvivesExplosion, uniform(1f, 3f))
	}
	check("""{"type":"minecraft:conditional","condition":"my_pack:fast_cooking","on_false":1600.0,"on_true":800.0}""") {
		conditional(PredicateArgument("fast_cooking", "my_pack"), 800f, 1600f)
	}
	check("""{"type":"minecraft:conditional","condition":{"type":"minecraft:survives_explosion"},"on_false":2.0,"on_true":3.0}""") {
		conditional(3f, 2f) { survivesExplosion() }
	}
	check("""{"type":"minecraft:cos","input":0.5}""") { cos(constant(0.5f)) }
	check("""{"type":"minecraft:div","left":9.0,"right":2.0}""") { div(constant(9f), constant(2f)) }
	check("""{"type":"minecraft:enchantment_level","amount":5}""") { enchantmentLevel(5) }
	check("""{"type":"minecraft:environment_attribute","attribute":"minecraft:gameplay/baby_villager_activity"}""") {
		floatEnvironmentAttribute(EnvironmentAttributes.Gameplay.BABY_VILLAGER_ACTIVITY)
	}
	check("""{"type":"minecraft:floor","input":1.7}""") { floor(constant(1.7f)) }
	check("""{"type":"minecraft:from_int","input":3}""") { fromInt(constant(3)) }
	check("""{"type":"minecraft:length","inputs":[3.0,4.0]}""") { length(constant(3f), constant(4f)) }
	check("""{"type":"minecraft:length","inputs":"#my_pack:speeds"}""") { length(ContextFloatProviderTagArgument("speeds", "my_pack")) }
	check("""{"type":"minecraft:max","inputs":[1.0,2.0]}""") { max(constant(1f), constant(2f)) }
	check("""{"type":"minecraft:min","inputs":[1.0,"my_pack:speed"]}""") { min(constant(1f), reference(speed)) }
	check("""{"type":"minecraft:mod","left":9.0,"right":2.0}""") { mod(constant(9f), constant(2f)) }
	check("""{"type":"minecraft:mul","inputs":["my_pack:speed",2.0]}""") { mul(reference(speed), constant(2f)) }
	check("""{"type":"minecraft:negate","input":4.0}""") { negate(constant(4f)) }
	check(
		"""{"type":"minecraft:number_dispatcher","cases":[{"condition":{"type":"minecraft:survives_explosion"},"value":1.0}],"default":{"type":"minecraft:weighted_list","distribution":[{"data":1.0,"weight":30},{"data":0.0,"weight":70}]}}"""
	) {
		floatNumberDispatcher(default = floatWeightedList {
			entry(30, 1f)
			entry(70, 0f)
		}) {
			case(1f) { survivesExplosion() }
		}
	}
	check("""{"type":"minecraft:number_dispatcher","cases":[{"condition":"my_pack:is_night","value":2.0},{"condition":{"type":"minecraft:survives_explosion"},"value":"my_pack:speed"}]}""") {
		floatNumberDispatcher {
			case(PredicateArgument("is_night", "my_pack"), 2f)
			case(SurvivesExplosion, reference(speed))
		}
	}
	check("""{"type":"minecraft:number_dispatcher","cases":[],"default":7.0}""") { floatNumberDispatcher(7f) {} }
	check("""{"type":"minecraft:pow","base":2.0,"exponent":3.0}""") { pow(constant(2f), constant(3f)) }
	check(""""my_pack:speed"""") { reference(speed) }
	check(""""minecraft:cooking/speed_default"""") { reference(ContextFloatProviders.Cooking.SPEED_DEFAULT) }
	check("""{"type":"minecraft:round","input":1.5}""") { round(constant(1.5f)) }
	check("""{"type":"minecraft:sin","input":0.5}""") { sin(constant(0.5f)) }
	check("""{"type":"minecraft:sqrt","input":4.0}""") { sqrt(constant(4f)) }
	check("""{"type":"minecraft:storage","path":"player.health","storage":"my_pack:data"}""") { floatStorage("my_pack:data", "player.health") }
	check("""{"type":"minecraft:storage","fallback":1.5,"path":"a","storage":"my_pack:data"}""") { floatStorage("my_pack:data", "a", constant(1.5f)) }
	check("""{"type":"minecraft:sub","left":5.0,"right":2.0}""") { sub(constant(5f), constant(2f)) }
	check("""{"type":"minecraft:truncate","input":-1.5}""") { truncate(constant(-1.5f)) }
	check("""{"type":"minecraft:uniform","max":5.0,"min":1.0}""") { uniform(1f, 5f) }
	check("""{"type":"minecraft:uniform","max":5.0,"min":"minecraft:cooking/speed_default"}""") {
		uniform(reference(ContextFloatProviders.Cooking.SPEED_DEFAULT), constant(5f))
	}
	check("""{"type":"minecraft:weighted_list","distribution":[{"data":1.0,"weight":30},{"data":0.0,"weight":70}]}""") {
		floatWeightedList {
			entry(30, 1f)
			entry(70, constant(0f))
		}
	}

	jsonEncoder.decodeFromString(ContextFloatProvider.serializer(), "5.0") assertsIs ConstantContextFloatProvider(5f)
	jsonEncoder.decodeFromString(ContextFloatProvider.serializer(), "5") assertsIs ConstantContextFloatProvider(5f)
	jsonEncoder.decodeFromString(ContextFloatProvider.serializer(), """{"type":"minecraft:constant","value":5.0}""") assertsIs
		ConstantContextFloatProvider(5f)
	jsonEncoder.decodeFromString(ContextFloatProvider.serializer(), "\"my_pack:r\"")
		.shouldBeInstanceOf<ContextFloatProviderReference>().id
		.shouldBeInstanceOf<ContextFloatProviderArgument>().asString() assertsIs "my_pack:r"
	jsonEncoder.decodeFromString(ContextFloatProvider.serializer(), """{"type":"minecraft:length","inputs":"#my_pack:t"}""")
		.shouldBeInstanceOf<LengthContextFloatProvider>().inputs.single()
		.shouldBeInstanceOf<ContextFloatProviderReference>().id
		.shouldBeInstanceOf<ContextFloatProviderTagArgument>().asString() assertsIs "#my_pack:t"

	shouldThrowAny { jsonEncoder.decodeFromString(ContextFloatProvider.serializer(), """{"min":1,"max":2}""") }
	shouldThrowAny { jsonEncoder.decodeFromString(ContextFloatProvider.serializer(), """{"type":"minecraft:binomial","n":1,"p":0.5}""") }
	shouldThrowAny { jsonEncoder.decodeFromString(ContextFloatProvider.serializer(), """{"type":"minecraft:score","score":"a","target":{"type":"minecraft:fixed","name":"b"}}""") }
}

class ContextFloatProviderTests : FunSpec({
	test("context float providers") {
		dataPack("contextFloatProviders") {
			contextFloatProviderTests()
		}
	}

	test("a float constant keeps its decimal part") {
		dataPack("contextFloatProviderFormat") {
			fun encode(value: Float) = jsonEncoder.encodeToString(ContextFloatProvider.serializer(), ConstantContextFloatProvider(value))

			encode(1f) shouldBe "1.0"
			encode(0.1f) shouldBe "0.1"
			encode(0.5f) shouldBe "0.5"
			encode(-2f) shouldBe "-2.0"
			jsonEncoder.encodeToString(ContextIntProvider.serializer(), ConstantContextIntProvider(1)) shouldBe "1"
		}
	}
})
