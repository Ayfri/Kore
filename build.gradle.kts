plugins {
	kotlin("jvm") apply false
	kotlin("multiplatform") apply false
	kotlin("plugin.serialization") apply false
	kotlin("plugin.compose") apply false
	alias(libs.plugins.vanniktech.publish) apply false
}

// Aggregates every KMP module's `allTests` (jvmTest, jsNodeTest, jsBrowserTest) so CI doesn't enumerate modules or platforms.
tasks.register("testAll") {
	group = "verification"
	description = "Runs every module's tests across all Kotlin Multiplatform targets (JVM, JS Node, and JS Browser when enabled)."
	dependsOn(listOf(":bindings", ":helpers", ":kore", ":oop").map { "$it:allTests" })
}
