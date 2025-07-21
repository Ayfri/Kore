package io.github.ayfri.kore.features.testenvironments

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.TestEnvironmentArgument
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.features.testenvironments.enums.Weather as WeatherEnum
import io.github.ayfri.kore.features.testenvironments.types.*
import io.github.ayfri.kore.generated.Gamerules
import io.github.ayfri.kore.utils.camelCase

/**
 * DSL entry point for creating GameTest test environments.
 *
 * Test environments define the preconditions and context for automated test execution
 * in Minecraft's GameTest framework. They group test instances together and ensure
 * consistent environmental conditions across test runs.
 */
fun DataPack.testEnvironments(init: TestEnvironmentsBuilder.() -> Unit) {
	TestEnvironmentsBuilder(this).apply(init)
}

/**
 * Builder reference for external use in GameTest scenarios.
 *
 * Allows creating test environments outside of the main `testEnvironments` block,
 * useful for reusing environments across multiple test instance definitions.
 */
val DataPack.testEnvironmentsBuilder get() = TestEnvironmentsBuilder(this)

/**
 * Builder class for creating GameTest test environments using Kotlin DSL syntax.
 *
 * Provides methods to create different types of test environments that control
 * the execution context for automated tests in the GameTest framework.
 */
class TestEnvironmentsBuilder(private val dataPack: DataPack) {

	/**
	 * Creates a composite environment that combines multiple test environments.
	 *
	 * This allows applying multiple environmental conditions simultaneously,
	 * such as combining weather settings with game rule modifications and time changes.
	 *
	 * @param fileName The name of the combined environment file
	 * @param environments The test environments to combine into one
	 * @return TestEnvironmentArgument representing the composite environment
	 */
	fun allOf(
		fileName: String = "combined_environment",
		vararg environments: TestEnvironmentArgument
	): TestEnvironmentArgument = dataPack.testEnvironment(fileName, AllOf(environments.toList()))

	/**
	 * Creates a function-based test environment with setup and teardown procedures.
	 *
	 * Function environments allow executing specific datapack functions before and after
	 * test execution, enabling complex world state preparation and cleanup procedures.
	 *
	 * @param fileName The name of the function environment file
	 * @param init Configuration block for defining setup and teardown functions
	 * @return TestEnvironmentArgument representing the function environment
	 */
	fun function(
		fileName: String = "function_environment",
		init: FunctionEnvironmentBuilder.() -> Unit
	): TestEnvironmentArgument {
		val builder = FunctionEnvironmentBuilder().apply(init)
		return dataPack.testEnvironment(fileName, builder.build())
	}

	/**
	 * Creates a game rules-based test environment for controlled testing conditions.
	 *
	 * Game rules environments allow setting specific game rules to create predictable
	 * test conditions, such as disabling mob spawning, controlling daylight cycles,
	 * or adjusting game mechanics for consistent test results.
	 *
	 * @param fileName The name of the game rules environment file
	 * @param init Configuration block for setting boolean and integer game rules
	 * @return TestEnvironmentArgument representing the game rules environment
	 */
	fun gameRules(
		fileName: String = "gamerules_environment",
		init: GameRulesBuilder.() -> Unit
	): TestEnvironmentArgument {
		val builder = GameRulesBuilder().apply(init)
		return dataPack.testEnvironment(fileName, builder.build())
	}

	/**
	 * Creates a time-based test environment with a specific time of day.
	 *
	 * Time environments ensure tests run at consistent times, which is crucial
	 * for testing time-dependent mechanics like mob spawning, solar panels,
	 * or daylight sensor behavior.
	 *
	 * @param fileName The name of the time environment file
	 * @param time The time of day in game ticks (0-24000, where 6000 = noon, 18000 = midnight)
	 * @return TestEnvironmentArgument representing the time environment
	 */
	fun timeOfDay(
		fileName: String = "time_environment",
		time: Int
	): TestEnvironmentArgument = dataPack.testEnvironment(fileName, TimeOfDay(time))

	/**
	 * Creates a weather-based test environment with specific weather conditions.
	 *
	 * Weather environments ensure consistent weather conditions during test execution,
	 * important for testing weather-dependent mechanics like lightning rods, crop growth,
	 * or mob behavior variations.
	 *
	 * @param fileName The name of the weather environment file
	 * @param weather The weather condition to maintain during test execution
	 * @return TestEnvironmentArgument representing the weather environment
	 */
	fun weather(
		fileName: String = "weather_environment",
		weather: WeatherEnum
	): TestEnvironmentArgument = dataPack.testEnvironment(fileName, Weather(weather))
}

/**
 * DSL builder for configuring function environments in the GameTest framework.
 *
 * Allows specifying setup and teardown functions that execute before and after
 * test instance runs, enabling complex world state preparation and cleanup.
 */
class FunctionEnvironmentBuilder {
	var setup: FunctionArgument? = null
	var teardown: FunctionArgument? = null

	fun setup(function: FunctionArgument) {
		setup = function
	}

	fun teardown(function: FunctionArgument) {
		teardown = function
	}

	internal fun build() = Function(setup, teardown)
}

/**
 * DSL builder for configuring game rules environments in the GameTest framework.
 *
 * Provides a type-safe way to set boolean and integer game rules for creating
 * controlled testing conditions. Rules are automatically sorted alphabetically
 * for consistent output.
 */
class GameRulesBuilder {
	private val boolRules = mutableMapOf<Gamerules.Boolean, Boolean>()
	private val intRules = mutableMapOf<Gamerules.Int, Int>()

	operator fun set(rule: Gamerules.Boolean, value: Boolean) {
		boolRules[rule] = value
	}

	operator fun set(rule: Gamerules.Int, value: Int) {
		intRules[rule] = value
	}

	internal fun build() = GameRules(
		boolRules = boolRules.map { (rule, value) -> GameRuleEntry(rule.name.camelCase(), value) }.sortedBy { it.rule },
		intRules = intRules.map { (rule, value) -> GameRuleIntEntry(rule.name.camelCase(), value) }.sortedBy { it.rule }
	)
}
