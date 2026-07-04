plugins {
	kotlin("jvm") apply false
	kotlin("multiplatform") apply false
	kotlin("plugin.serialization") apply false
	kotlin("plugin.compose") apply false
	alias(libs.plugins.vanniktech.publish) apply false
}
