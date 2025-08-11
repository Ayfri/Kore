package io.github.ayfri.kore.features.testenvironments

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.TestEnvironmentArgument
import io.github.ayfri.kore.features.testenvironments.types.TestEnvironment
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven test environment definition for Minecraft Java Edition.
 *
 * A test environment is a JSON structure used in data packs to configure the test environment
 * for GameTest features. It specifies the world type, seed, and other settings.
 *
 * Docs: https://kore.ayfri.com/docs/test-features
 * Minecraft Wiki: https://minecraft.wiki/w/GameTest
 */
@Serializable
data class TestEnvironmentFeature(
	@Transient
	override var fileName: String = "test_environment",
	val environment: TestEnvironment,
) : Generator("test_environment") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(environment)
}

/**
 * Creates a test environment with the specified configuration.
 *
 * Produces `data/<namespace>/test_environment/<fileName>.json`.
 * Docs: https://kore.ayfri.com/docs/test-features
 *
 * @param fileName The name of the test environment file
 * @param environment The test environment configuration
 * @param init Additional configuration block
 * @return TestEnvironmentArgument representing the created test environment
 */
fun DataPack.testEnvironment(
	fileName: String = "test_environment",
	environment: TestEnvironment,
	init: TestEnvironmentFeature.() -> Unit = {},
): TestEnvironmentArgument {
	val testEnvironment = TestEnvironmentFeature(fileName, environment).apply(init)
	testEnvironments += testEnvironment
	return TestEnvironmentArgument(fileName, testEnvironment.namespace ?: name)
}
