// On chope le catalogue 'libs' de façon stricte
val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
	// Le add() avec string bypass le besoin de l'accesseur testImplementation
	add("testImplementation", libs.findLibrary("kotest-assertions-core").get())
	add("testImplementation", libs.findLibrary("kotest-runner-junit5").get())
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()
}