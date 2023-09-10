package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.literals.randomUUID
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.assertions.assertsMatches
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Attributes

fun Function.attributeTests() {
	val attribute = Attributes.GENERIC_MAX_HEALTH

	// language=regexp
	val charSet = "[0-9a-f]"
	// language=regexp
	val uuidRegex = "$charSet{8}-$charSet{4}-$charSet{4}-$charSet{4}-$charSet{12}"

	attributes {
		get(self()) {
			attribute(attribute).get() assertsIs "attribute @s minecraft:generic.max_health get"
			get(attribute, 2.0) assertsIs "attribute @s minecraft:generic.max_health get 2"

			modifiers(attribute) {
				add(
					attribute,
					1.0,
					AttributeModifierOperation.ADD
				) assertsMatches Regex("attribute @s minecraft:generic.max_health modifier add $uuidRegex minecraft:generic.max_health 1 add")

				add(
					attribute,
					1.0,
					AttributeModifierOperation.MULTIPLY
				) assertsMatches Regex("attribute @s minecraft:generic.max_health modifier add $uuidRegex minecraft:generic.max_health 1 multiply")

				add(
					attribute,
					1.0,
					AttributeModifierOperation.MULTIPLY_BASE
				) assertsMatches Regex("attribute @s minecraft:generic.max_health modifier add $uuidRegex minecraft:generic.max_health 1 multiply_base")

				get(randomUUID()) assertsMatches Regex("attribute @s minecraft:generic.max_health modifier value get $uuidRegex")

				remove(randomUUID()) assertsMatches Regex("attribute @s minecraft:generic.max_health modifier remove $uuidRegex")
			}
		}

		get(self(), attribute, 1.0) assertsIs "attribute @s minecraft:generic.max_health get 1"

		get(self(), attribute) {
			get() assertsIs "attribute @s minecraft:generic.max_health get"

			base {
				get() assertsIs "attribute @s minecraft:generic.max_health base get"
				set(1.0) assertsIs "attribute @s minecraft:generic.max_health base set 1"
			}

			modifiers {
				get(randomUUID()) assertsMatches Regex("attribute @s minecraft:generic.max_health modifier value get $uuidRegex")
			}
		}
	}

	attributes(self()) {
		get(attribute) assertsIs "attribute @s minecraft:generic.max_health get"
		base(attribute) {
			get() assertsIs "attribute @s minecraft:generic.max_health base get"
			set(1.0) assertsIs "attribute @s minecraft:generic.max_health base set 1"
		}

		modifiers(attribute) {
			get(randomUUID()) assertsMatches Regex("attribute @s minecraft:generic.max_health modifier value get $uuidRegex")
		}
	}

	attributes.get(self(), attribute) assertsIs "attribute @s minecraft:generic.max_health get"
}
