package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.colors.RGB
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.timelines.*
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.types.FloatValue
import io.github.ayfri.kore.generated.EnvironmentAttributes

fun DataPack.timelineTests() {
	timeline("test_timeline") {
		periodTicks = 24000

		track(EnvironmentAttributes.Visual.FOG_START_DISTANCE, Linear, EnvironmentAttributeModifier.OVERRIDE) {
			keyframe(0) {
				value(0.0f)
			}

			keyframe(12000) {
				value(100.0f)
			}
		}

		track(EnvironmentAttributes.Visual.FOG_END_DISTANCE) {
			ease = InOutCubic

			keyframe(0) {
				value(50.0f)
			}

			keyframe(6000) {
				value(200.0f)
			}
		}
	}

	timelines.last() assertsIs """
		{
			"period_ticks": 24000,
			"tracks": {
				"minecraft:visual/fog_start_distance": {
					"ease": "linear",
					"keyframes": [
						{
							"ticks": 0,
							"value": 0.0
						},
						{
							"ticks": 12000,
							"value": 100.0
						}
					],
					"modifier": "override"
				},
				"minecraft:visual/fog_end_distance": {
					"ease": "in_out_cubic",
					"keyframes": [
						{
							"ticks": 0,
							"value": 50.0
						},
						{
							"ticks": 6000,
							"value": 200.0
						}
					]
				}
			}
		}
	""".trimIndent()

	timeline("bezier_timeline") {
		periodTicks = 12000

		track(EnvironmentAttributes.Gameplay.SKY_LIGHT_LEVEL) {
			ease = CubicBezier(0.25f, 0.1f, 0.25f, 1.0f)

			keyframe(0) {
				value(0)
			}

			keyframe(6000) {
				value(15)
			}
		}
	}

	timelines.last() assertsIs """
		{
			"period_ticks": 12000,
			"tracks": {
				"minecraft:gameplay/sky_light_level": {
					"ease": {
						"cubic_bezier": [
							0.25,
							0.1,
							0.25,
							1.0
						]
					},
					"keyframes": [
						{
							"ticks": 0,
							"value": 0
						},
						{
							"ticks": 6000,
							"value": 15
						}
					]
				}
			}
		}
	""".trimIndent()

	timeline("constant_timeline") {
		track(EnvironmentAttributes.Gameplay.MONSTERS_BURN) {
			ease = Constant

			keyframe(0) {
				value(true)
			}
		}
	}

	timelines.last() assertsIs """
		{
			"tracks": {
				"minecraft:gameplay/monsters_burn": {
					"ease": "constant",
					"keyframes": [
						{
							"ticks": 0,
							"value": true
						}
					]
				}
			}
		}
	""".trimIndent()

	timeline("typed_timeline") {
		periodTicks = 24000

		track(EnvironmentAttributes.Visual.FOG_COLOR) {
			ease = Linear

			keyframe(0) {
				value(RGB(255, 128, 0))
			}

			keyframe(12000) {
				value(FloatValue(1.0f))
			}
		}
	}

	timelines.last() assertsIs """
		{
			"period_ticks": 24000,
			"tracks": {
				"minecraft:visual/fog_color": {
					"ease": "linear",
					"keyframes": [
						{
							"ticks": 0,
							"value": 16744448
						},
						{
							"ticks": 12000,
							"value": 1.0
						}
					]
				}
			}
		}
	""".trimIndent()
}
