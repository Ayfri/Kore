package serialization

import arguments.*
import arguments.chatcomponents.scoreComponent
import arguments.chatcomponents.textComponent
import arguments.enums.Gamemode
import arguments.numbers.rangeOrDouble
import arguments.numbers.rangeOrIntStart
import dataPack
import functions.load
import generated.Entities
import generated.type
import setTestPath
import utils.debugEntity

fun selectorTests() = dataPack("serialization_tests") {
	setTestPath()
	load("selector_serialization") {
		val debugEntity = debugEntity {
			this["advancements"] = "@e[advancements={test:foo={bar=true}}]"
			this["gamemode_not"] = "@e[gamemode=!creative]"
			this["simple"] = "@e"
			this["scores"] = "@e[scores={foo=1..}]"
			this["type"] = "@e[type=minecraft:marker]"
			this["type+tags"] = "@e[tag=foo,type=minecraft:marker]"
			this["type+tags+nbt"] = "@e[nbt={foo:\"bar\"},tag=foo,type=minecraft:marker]"
			this["xRotation_renamed"] = "@e[x_rotation=1.5]"
		}

		debugEntity.whenAllTestsValid {
			debug("All selector serialization tests passed !") {
				bold = true
				color = Color.GREEN
			}
		}

		debugEntity.whenAnyTestInvalid {
			debug(scoreComponent(debugEntity.scoreName, debugEntity.selector) {
				bold = true
				color = Color.RED
			} + textComponent(" / ${debugEntity.scoreToValidateAllTests} scores passed") {
				bold = true
				color = Color.RED
			})
		}

		debugEntity.assertAllData {
			this["advancements"] = allEntities {
				advancements {
					Argument.Advancement("foo", "test")() {
						this["bar"] = true
					}
				}
			}.asString()

			this["gamemode_not"] = allEntities {
				gamemode = !Gamemode.CREATIVE
			}.asString()

			this["simple"] = allEntities().asString()

			this["scores"] = allEntities {
				scores {
					score("foo", rangeOrIntStart(1))
				}
			}.asString()

			this["type"] = allEntities { type(Entities.MARKER) }.asString()
			this["type+tags"] = allEntities { type(Entities.MARKER); tag = "foo" }.asString()
			this["type+tags+nbt"] = allEntities {
				type(Entities.MARKER)
				tag = "foo"
				nbt = nbt test@{ this@test["foo"] = "bar" }
			}.asString()

			this["xRotation_renamed"] = allEntities { xRotation = rangeOrDouble(1.5) }.asString()
		}

		debugEntity.remove()
	}
}.generate()
