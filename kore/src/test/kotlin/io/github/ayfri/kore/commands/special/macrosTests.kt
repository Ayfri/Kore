package io.github.ayfri.kore.commands.special

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.assertions.assertsThrows
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.functions.*
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set

fun DataPack.testMacros() {
	val functionWithMacros = function("function_with_macros") {
		say("This is a macro: ${macro("my_macro")}") assertsIs "\$say This is a macro: $(my_macro)"
		say(macro("my_macro")) assertsIs "\$say $(my_macro)"
		eval("my_macro", "my_macro") assertsIs "$\$(my_macro) $(my_macro)"
	}

	class MyMacros : Macros() {
		val myMacro by "my_macro"
	}

	val functionWithMacrosAndVerification = function("function_with_macros_2", ::MyMacros) {
		say(macros.myMacro) assertsIs "\$say $(my_macro)"
		addLine("tellraw ${macros.myMacro}") assertsIs "\$tellraw $(my_macro)"

		val fakeCall = macros.myMacro.replace("$", "")
		say("fake macro usage: $fakeCall") assertsIs "say fake macro usage: (my_macro)"
	}

	load {
		function(functionWithMacros, arguments = nbt { this["my_macro"] = "Hello world!" })
		function(functionWithMacrosAndVerification, arguments = nbt { this["my_macro"] = "Hello world!" })

		assertsThrows("Missing arguments 'my_macro' when calling function 'minecraft:function_with_macros_2'") {
			function(functionWithMacrosAndVerification, arguments = nbt { this["my_bad_macro"] = "Hello world!" })
		}
	}
}
