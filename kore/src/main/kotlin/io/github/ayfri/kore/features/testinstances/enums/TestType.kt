package io.github.ayfri.kore.features.testinstances.enums

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the execution type for GameTest test instances.
 *
 * The GameTest framework supports two distinct approaches to test execution:
 */
@Serializable(TestType.Companion.TestTypeSerializer::class)
enum class TestType {
	/**
	 * Block-based tests use special test blocks within the structure to control test logic via redstone.
	 *
	 * Available test blocks:
	 * - **Start**: Triggers a redstone pulse when the test begins
	 * - **Log**: Logs a message to the test log when powered by redstone
	 * - **Fail**: Fails the test when powered by redstone
	 * - **Accept**: Completes the test successfully when powered by redstone
	 *
	 * If a race condition occurs, the first activated test block always wins.
	 */
	BLOCK_BASED,

	/**
	 * Function-based tests rely on built-in test functions to determine success or failure.
	 *
	 * These tests are primarily used by Mojang internally and by mod developers through
	 * the GameTest framework interfaces. They allow for programmatic test control with
	 * setup and teardown functions.
	 */
	FUNCTION;

	companion object {
		data object TestTypeSerializer : LowercaseSerializer<TestType>(entries)
	}
}
