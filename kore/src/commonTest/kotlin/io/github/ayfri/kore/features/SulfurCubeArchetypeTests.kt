package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.sulfurcubearchetype.contactDamage
import io.github.ayfri.kore.features.sulfurcubearchetype.explosion
import io.github.ayfri.kore.features.sulfurcubearchetype.modifier
import io.github.ayfri.kore.features.sulfurcubearchetype.soundSettings
import io.github.ayfri.kore.features.sulfurcubearchetype.sulfurCubeArchetype
import io.github.ayfri.kore.generated.Attributes
import io.github.ayfri.kore.generated.DamageTypes
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.sulfurCubeArchetypeTests() {
	sulfurCubeArchetype(
		"regular",
		items = Tags.Item.SWORDS,
		horizontalKnockbackPower = 0.4f,
		verticalKnockbackPower = 0.2f,
	) {
		buoyant = true

		contactDamage(amount = 3f, damageType = DamageTypes.GENERIC, attributeToSource = true)
		explosion(fuse = 80, power = 3, causesFire = true)

		modifier(
			amount = 4.0,
			attribute = Attributes.MAX_HEALTH,
			id = "sulfur_cube_archetype:regular",
			operation = AttributeModifierOperation.ADD_VALUE,
		)

		soundSettings(
			hitSound = SoundEvents.Entity.SulfurCube.HIT,
			pushSound = SoundEvents.Entity.SulfurCube.PUSH,
			pushSoundCooldown = 5f,
			pushSoundImpulseThreshold = 0.6f,
		)
	}

	sulfurCubeArchetypes.last() assertsIs """
		{
			"attribute_modifiers": [
				{
					"amount": 4.0,
					"attribute": "minecraft:max_health",
					"id": "sulfur_cube_archetype:regular",
					"operation": "add_value"
				}
			],
			"buoyant": true,
			"contact_damage": {
				"amount": 3.0,
				"attribute_to_source": true,
				"damage_type": "minecraft:generic"
			},
			"explosion": {
				"causes_fire": true,
				"fuse": 80,
				"power": 3
			},
			"items": "#minecraft:swords",
			"knockback_modifiers": {
				"horizontal_power": 0.4,
				"vertical_power": 0.2
			},
			"sound_settings": {
				"hit_sound": "minecraft:entity.sulfur_cube.hit",
				"push_sound": "minecraft:entity.sulfur_cube.push",
				"push_sound_cooldown": 5.0,
				"push_sound_impulse_threshold": 0.6
			}
		}
	""".trimIndent()
}

class SulfurCubeArchetypeTests : FunSpec({
	test("sulfur cube archetype") {
		dataPack("sulfurCubeArchetype") {
			pretty()
			sulfurCubeArchetypeTests()
		}
	}
})
