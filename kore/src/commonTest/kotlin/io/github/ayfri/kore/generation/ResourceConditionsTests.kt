package io.github.ayfri.kore.generation

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.advancements.advancement
import io.github.ayfri.kore.features.predicates.conditions.randomChance
import io.github.ayfri.kore.features.predicates.predicate
import io.github.ayfri.kore.generation.fabric.conditions.ResourceCondition
import io.github.ayfri.kore.generation.fabric.conditions.ResourceConditionsBuilder
import io.github.ayfri.kore.generation.fabric.conditions.fabricLoadConditions
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.builtins.ListSerializer

fun DataPack.resourceConditionsTests() {
	val conditions = ResourceConditionsBuilder().apply {
		allModsLoaded("create", "farmers_delight")
		not(anyModsLoaded("badmod"))
		and {
			tagsPopulated("c:ingots/copper")
			featuresEnabled("minecraft:vanilla")
		}
		or {
			registryContains("minecraft:diamond")
			registryContains("example:custom_block", registry = "minecraft:block")
		}
		tagsPopulated("minecraft:logs", registry = "minecraft:block")
		conditionTrue()
	}.conditions

	jsonEncoder.encodeToString(ListSerializer(ResourceCondition.serializer()), conditions) assertsIs """
		[
			{
				"condition": "fabric:all_mods_loaded",
				"values": [
					"create",
					"farmers_delight"
				]
			},
			{
				"condition": "fabric:not",
				"value": {
					"condition": "fabric:any_mods_loaded",
					"values": [
						"badmod"
					]
				}
			},
			{
				"condition": "fabric:and",
				"values": [
					{
						"condition": "fabric:tags_populated",
						"values": [
							"c:ingots/copper"
						]
					},
					{
						"condition": "fabric:features_enabled",
						"features": [
							"minecraft:vanilla"
						]
					}
				]
			},
			{
				"condition": "fabric:or",
				"values": [
					{
						"condition": "fabric:registry_contains",
						"values": [
							"minecraft:diamond"
						]
					},
					{
						"condition": "fabric:registry_contains",
						"registry": "minecraft:block",
						"values": [
							"example:custom_block"
						]
					}
				]
			},
			{
				"condition": "fabric:tags_populated",
				"registry": "minecraft:block",
				"values": [
					"minecraft:logs"
				]
			},
			{
				"condition": "fabric:true"
			}
		]
	""".trimIndent()

	advancement("needs_create") {
		fabricLoadConditions {
			allModsLoaded("create")
		}
	}

	advancements.last().generateJsonWithLoadConditions(this) assertsIs """
		{
			"fabric:load_conditions": [
				{
					"condition": "fabric:all_mods_loaded",
					"values": [
						"create"
					]
				}
			],
			"criteria": {}
		}
	""".trimIndent()

	predicate("multi_ignored") {
		fabricLoadConditions {
			allModsLoaded("create")
		}
		randomChance(0.5f)
		randomChance(0.25f)
	}

	val multi = predicates.last()
	multi.generateJsonWithLoadConditions(this) assertsIs multi.generateJson(this)
}

class ResourceConditionsTests : FunSpec({
	test("resource conditions") {
		dataPack("resource_conditions") {
			pretty()
			resourceConditionsTests()
		}
	}
})
