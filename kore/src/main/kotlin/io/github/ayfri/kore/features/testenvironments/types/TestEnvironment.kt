package io.github.ayfri.kore.features.testenvironments.types

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

/**
 * Base sealed class for all test environment types in Minecraft's GameTest framework.
 *
 * Test environments define the preconditions and context in which automated end-to-end (E2E)
 * tests are executed. They group test instances together and specify the environmental
 * conditions such as weather, time, game rules, or function setup/teardown.
 *
 * This is part of Minecraft's GameTest framework introduced in snapshot 25w03a for automated
 * testing of datapack functionality.
 */
@Serializable(with = TestEnvironment.Companion.TestEnvironmentSerializer::class)
sealed class TestEnvironment {
	companion object {
		data object TestEnvironmentSerializer : NamespacedPolymorphicSerializer<TestEnvironment>(TestEnvironment::class)
	}
}
