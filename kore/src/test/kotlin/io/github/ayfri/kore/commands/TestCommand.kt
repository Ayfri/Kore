package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.TestInstanceArgument
import io.github.ayfri.kore.arguments.types.TestSelector
import io.github.ayfri.kore.arguments.types.allTests
import io.github.ayfri.kore.arguments.types.minecraftTests
import io.github.ayfri.kore.arguments.types.testSelector
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Blocks

fun Function.testTests() {
	test {
		clearAll() assertsIs "test clearall"
		clearAll(10) assertsIs "test clearall 10"

		clearThat() assertsIs "test clearthat"
		clearThese() assertsIs "test clearthese"

		val testInstance = TestInstanceArgument("my_test", "my_datapack")
		create(testInstance) assertsIs "test create my_datapack:my_test"
		create(testInstance, 10) assertsIs "test create my_datapack:my_test 10"
		create(testInstance, 10, 5) assertsIs "test create my_datapack:my_test 10 5"
		create(testInstance, 10, 5, 8) assertsIs "test create my_datapack:my_test 10 5 8"

		val selector = testSelector("my_datapack:test_*")
		locate(selector) assertsIs "test locate my_datapack:test_*"

		pos() assertsIs "test pos"
		pos("myvar") assertsIs "test pos myvar"

		resetClosest() assertsIs "test resetclosest"
		resetThat() assertsIs "test resetthat"
		resetThese() assertsIs "test resetthese"

		run(selector) assertsIs "test run my_datapack:test_*"
		run(selector, 3) assertsIs "test run my_datapack:test_* 3"
		run(selector, 3, true) assertsIs "test run my_datapack:test_* 3 true"
		run(selector, 3, true, 4) assertsIs "test run my_datapack:test_* 3 true 4"
		run(selector, 3, true, 4, 2) assertsIs "test run my_datapack:test_* 3 true 4 2"

		runClosest() assertsIs "test runclosest"
		runClosest(2) assertsIs "test runclosest 2"
		runClosest(2, false) assertsIs "test runclosest 2 false"
		runClosest(2, false, 3) assertsIs "test runclosest 2 false 3"
		runClosest(2, false, 3, 5) assertsIs "test runclosest 2 false 3 5"

		runFailed() assertsIs "test runfailed"
		runFailed(1) assertsIs "test runfailed 1"
		runFailed(1, true) assertsIs "test runfailed 1 true"
		runFailed(1, true, 2) assertsIs "test runfailed 1 true 2"
		runFailed(1, true, 2, 3) assertsIs "test runfailed 1 true 2 3"

		runMultiple(selector) assertsIs "test runmultiple my_datapack:test_*"
		runMultiple(selector, 5) assertsIs "test runmultiple my_datapack:test_* 5"

		runThat() assertsIs "test runthat"
		runThat(2) assertsIs "test runthat 2"
		runThat(2, true) assertsIs "test runthat 2 true"

		runThese() assertsIs "test runthese"
		runThese(3) assertsIs "test runthese 3"
		runThese(3, false) assertsIs "test runthese 3 false"

		stop() assertsIs "test stop"

		val test1 = TestInstanceArgument("test1", "my_datapack")
		val test2 = TestInstanceArgument("test2", "my_datapack")
		verify(listOf(test1, test2)) assertsIs "test verify my_datapack:test1 my_datapack:test2"
		verify(test1, test2) assertsIs "test verify my_datapack:test1 my_datapack:test2"
	}

	// Test helper functions
	allTests().asString() assertsIs "*:*"
	minecraftTests().asString() assertsIs "minecraft:*"
	testSelector("custom:*").asString() assertsIs "custom:*"
	testSelector("test_?").asString() assertsIs "minecraft:test_?"
	testSelector(Blocks.STONE) assertsIs "minecraft:stone"
	testSelector(Blocks.STONE, suffix = "*") assertsIs "minecraft:stone*"
}
