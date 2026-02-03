package io.github.ayfri.kore.features.testinstances

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.testenvironments.types.Function
import io.github.ayfri.kore.features.testinstances.enums.TestRotation
import io.github.ayfri.kore.features.testinstances.enums.TestType
import io.github.ayfri.kore.generated.arguments.types.TestEnvironmentArgument
import io.github.ayfri.kore.generated.arguments.types.TestInstanceArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class TestInstanceFeature(
	@Transient
	override var fileName: String = "test_instance",
	val testInstance: TestInstance,
) : Generator("test_instance") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(testInstance)
}

/**
 * Creates a GameTest test instance with the specified parameters.
 *
 * Test instances are part of Minecraft's GameTest framework (introduced in snapshot 25w03a)
 * for automated end-to-end testing of datapack functionality. Each test instance defines
 * a complete test scenario with its structure, execution parameters, and environmental context.
 *
 * The generated JSON file will be placed in the `test_instance` registry of the datapack,
 * making it available for execution via the `/test` command or automated test runs.
 *
 * Produces `data/<namespace>/test_instance/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/advanced/test-features
 * Minecraft Wiki: https://minecraft.wiki/w/GameTest
 *
 * @param fileName The name of the test instance file (becomes the test identifier)
 * @param environment The test environment that provides execution context and preconditions
 * @param maxTicks Maximum number of game ticks before the test times out
 * @param structure The structure template containing all blocks and entities needed for the test
 * @param type The test execution type (BLOCK_BASED for redstone logic, FUNCTION for programmatic control)
 * @param function Optional function configuration for function-based tests (setup/teardown functions)
 * @param manualOnly Whether the test should only run when manually triggered (not in automated runs)
 * @param maxAttempts Maximum number of test attempts for retry logic
 * @param required Whether this test is required to pass for the test suite to succeed
 * @param requiredSuccesses Number of successful runs needed out of maxAttempts
 * @param rotation Optional rotation for the test structure (useful for testing directional functionality)
 * @param setupTicks Number of ticks to wait before starting test execution (for world state preparation)
 * @param skyAccess Whether the test requires sky access (affects structure placement validation)
 * @param init Additional configuration block for the test instance feature
 * @return TestInstanceArgument representing the created test instance for referencing in other contexts
 */
fun DataPack.testInstance(
	fileName: String = "test_instance",
	environment: TestEnvironmentArgument,
	maxTicks: Int,
	structure: StructureArgument,
	type: TestType,
	function: Function? = null,
	manualOnly: Boolean? = null,
	maxAttempts: Int? = null,
	required: Boolean? = null,
	requiredSuccesses: Int? = null,
	rotation: TestRotation? = null,
	setupTicks: Int? = null,
	skyAccess: Boolean? = null,
	init: TestInstanceFeature.() -> Unit = {},
): TestInstanceArgument {
	val testInstance = TestInstanceFeature(
		fileName,
		TestInstance(
			environment = environment,
			function = function,
			manualOnly = manualOnly,
			maxAttempts = maxAttempts,
			maxTicks = maxTicks,
			required = required,
			requiredSuccesses = requiredSuccesses,
			rotation = rotation,
			setupTicks = setupTicks,
			skyAccess = skyAccess,
			structure = structure,
			type = type
		)
	).apply(init)
	testInstances += testInstance
	return TestInstanceArgument(fileName, testInstance.namespace ?: name)
}
