package commands.special

import DataPack
import assertions.assertsIs
import assertions.assertsThrows
import commands.function
import commands.say
import functions.*
import utils.nbt
import utils.set

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
