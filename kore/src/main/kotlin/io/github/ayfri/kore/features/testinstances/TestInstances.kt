package io.github.ayfri.kore.features.testinstances

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.TestEnvironmentArgument
import io.github.ayfri.kore.arguments.types.TestInstanceArgument
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.features.testinstances.enums.TestRotation
import io.github.ayfri.kore.features.testinstances.enums.TestType
import io.github.ayfri.kore.features.testenvironments.types.Function
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureArgument

/**
 * DSL entry point for creating test instances.
 */
fun DataPack.testInstances(init: TestInstancesBuilder.() -> Unit) {
	TestInstancesBuilder(this).apply(init)
}

/**
 * Builder reference for external use.
 */
val DataPack.testInstancesBuilder get() = TestInstancesBuilder(this)

/**
 * Builder class for creating test instances using DSL syntax.
 */
class TestInstancesBuilder(private val dataPack: DataPack) {

	/**
	 * Creates a test instance using DSL configuration.
	 *
	 * @param fileName The name of the test instance file
	 * @param init Configuration block for the test instance
	 * @return TestInstanceArgument representing the created test instance
	 */
	fun testInstance(
		fileName: String = "test_instance",
		init: TestInstanceDSLBuilder.() -> Unit
	): TestInstanceArgument {
		val builder = TestInstanceDSLBuilder().apply(init)
		val testInstance = TestInstanceFeature(fileName, builder.build())
		dataPack.testInstances += testInstance
		return TestInstanceArgument(fileName, testInstance.namespace ?: dataPack.name)
	}
}

/**
 * DSL builder for configuring test instance properties.
 */
class TestInstanceDSLBuilder {
	var environment: TestEnvironmentArgument? = null
	var function: Function? = null
	var manualOnly: Boolean? = null
	var maxAttempts: Int? = null
	var maxTicks: Int = 100
	var required: Boolean? = null
	var requiredSuccesses: Int? = null
	var rotation: TestRotation? = null
	var setupTicks: Int? = null
	var skyAccess: Boolean? = null
	var structure: StructureArgument? = null
	var type: TestType = TestType.BLOCK_BASED

	fun blockBased() {
		type = TestType.BLOCK_BASED
	}

	fun clockwise90() = rotation(TestRotation.CLOCKWISE_90)

	fun counterclockwise90() = rotation(TestRotation.COUNTERCLOCKWISE_90)

	fun environment(env: TestEnvironmentArgument) {
		environment = env
	}

	fun function(init: FunctionDSLBuilder.() -> Unit) {
		val builder = FunctionDSLBuilder().apply(init)
		function = builder.build()
	}

	fun functionBased() {
		type = TestType.FUNCTION
	}

	fun noRotation() = rotation(TestRotation.NONE)

	fun rotate180() = rotation(TestRotation.ROT_180)

	fun rotation(rot: TestRotation) {
		rotation = rot
	}

	fun structure(struct: StructureArgument) {
		structure = struct
	}

	fun type(testType: TestType) {
		type = testType
	}

	internal fun build(): TestInstance {
		requireNotNull(environment) { "Environment must be specified" }
		requireNotNull(structure) { "Structure must be specified" }

		return TestInstance(
			environment = environment!!,
			function = function,
			manualOnly = manualOnly,
			maxAttempts = maxAttempts,
			maxTicks = maxTicks,
			required = required,
			requiredSuccesses = requiredSuccesses,
			rotation = rotation,
			setupTicks = setupTicks,
			skyAccess = skyAccess,
			structure = structure!!,
			type = type
		)
	}
}

/**
 * DSL builder for configuring function environments in test instances.
 */
class FunctionDSLBuilder {
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
