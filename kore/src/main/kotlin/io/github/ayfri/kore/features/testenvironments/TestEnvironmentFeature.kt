package io.github.ayfri.kore.features.testenvironments

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.TestEnvironmentArgument
import io.github.ayfri.kore.features.testenvironments.types.TestEnvironment
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

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
