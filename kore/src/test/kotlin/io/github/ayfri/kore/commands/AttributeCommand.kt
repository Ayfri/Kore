package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.AttributeModifierArgument
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.assertions.assertsMatches
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Attributes

fun Function.attributeTests() {
	val attribute = Attributes.MAX_HEALTH
	val attributeName = AttributeModifierArgument("my_attribute", datapack.name)

	attributes {
		get(self()) {
			attribute(attribute).get() assertsIs "attribute @s minecraft:max_health get"
			get(attribute, 2.0) assertsIs "attribute @s minecraft:max_health get 2"

			modifiers(attribute) {
				add(
					attributeName,
					1.0,
					AttributeModifierOperation.ADD_VALUE
				) assertsMatches Regex("attribute @s minecraft:max_health modifier add ${attributeName.asString()} 1 add_value")

				add(
					attributeName,
					1.0,
					AttributeModifierOperation.ADD_MULTIPLIED_TOTAL
				) assertsMatches Regex("attribute @s minecraft:max_health modifier add ${attributeName.asString()} 1 add_multiplied_total")

				add(
					attributeName,
					1.0,
					AttributeModifierOperation.ADD_MULTIPLIED_BASE
				) assertsMatches Regex("attribute @s minecraft:max_health modifier add ${attributeName.asString()} 1 add_multiplied_base")

				get(attributeName) assertsMatches Regex("attribute @s minecraft:max_health modifier value get ${attributeName.asString()}")

				remove(attributeName) assertsMatches Regex("attribute @s minecraft:max_health modifier remove ${attributeName.asString()}")
			}
		}

		get(self(), attribute, 1.0) assertsIs "attribute @s minecraft:max_health get 1"

		get(self(), attribute) {
			get() assertsIs "attribute @s minecraft:max_health get"

			base {
				get() assertsIs "attribute @s minecraft:max_health base get"
				set(1.0) assertsIs "attribute @s minecraft:max_health base set 1"
			}

			modifiers {
				get(attributeName) assertsMatches Regex("attribute @s minecraft:max_health modifier value get ${attributeName.asString()}")
			}
		}
	}

	attributes(self()) {
		get(attribute) assertsIs "attribute @s minecraft:max_health get"
		base(attribute) {
			get() assertsIs "attribute @s minecraft:max_health base get"
			set(1.0) assertsIs "attribute @s minecraft:max_health base set 1"
		}

		modifiers(attribute) {
			get(attributeName) assertsMatches Regex("attribute @s minecraft:max_health modifier value get ${attributeName.asString()}")
		}
	}

	attributes.get(self(), attribute) assertsIs "attribute @s minecraft:max_health get"
}
