package io.github.ayfri.kore.features.testinstances

import io.github.ayfri.kore.features.testinstances.enums.TestRotation
import io.github.ayfri.kore.features.testinstances.enums.TestType
import io.github.ayfri.kore.generated.arguments.types.TestEnvironmentArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureArgument
import kotlinx.serialization.Serializable

/**
 * Represents a Minecraft GameTest test instance configuration.
 *
 * Test instances define automated end-to-end (E2E) tests for datapack functionality.
 * They contain a structure, parameters for execution, and optional code to specify test behavior.
 * Each test instance belongs to a test environment that provides the execution context.
 *
 * Test instances can be either:
 * - **Block-based**: Use test blocks (Start, Log, Fail, Accept) inside structures to control logic via redstone
 * - **Function-based**: Rely on a Java method reference to control test logic programmatically
 *
 * Introduced in Minecraft snapshot 25w03a as part of the GameTest framework overhaul.
 *
 * @param environment The test environment that provides execution context and preconditions
 * @param function Optional fully qualified Java method reference for function-based tests (e.g., "com.example.MyMod::myTest")
 * @param manualOnly Whether this test should only be run when manually triggered
 * @param maxAttempts Maximum number of attempts before the test fails (for retry logic)
 * @param maxTicks Maximum number of game ticks the test can run before timing out
 * @param required Whether this test is required to pass for the test suite to succeed
 * @param requiredSuccesses Number of successful runs required out of maxAttempts
 * @param rotation Optional rotation to apply to the test structure (0°, 90°, 180°, 270°)
 * @param setupTicks Number of ticks to wait before starting the actual test execution
 * @param skyAccess Whether the test requires access to the sky (affects structure placement)
 * @param structure The structure template containing all blocks and entities needed for the test
 * @param type The test execution type (BLOCK_BASED or FUNCTION)
 */
@Serializable
data class TestInstance(
	val environment: TestEnvironmentArgument,
	val function: String? = null,
	val manualOnly: Boolean? = null,
	val maxAttempts: Int? = null,
	val maxTicks: Int,
	val required: Boolean? = null,
	val requiredSuccesses: Int? = null,
	val rotation: TestRotation? = null,
	val setupTicks: Int? = null,
	val skyAccess: Boolean? = null,
	val structure: StructureArgument,
	val type: TestType,
)
