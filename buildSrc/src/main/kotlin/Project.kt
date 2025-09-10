import java.time.Year

object Project {
	const val GROUP = "io.github.ayfri.kore"
	const val URL = "github.com/Ayfri/Kore"
	const val VERSION = "1.30.0"

	// Publishing constants
	const val CENTRAL_PORTAL_URL = "https://central.sonatype.com/api/v1/publisher"
	const val MAVEN_PUBLICATION_NAME = "maven"
	const val LICENSE_NAME = "MIT"
	const val LICENSE_URL = "https://opensource.org/licenses/MIT"
	const val DEVELOPER_ID = "Ayfri"
	const val DEVELOPER_NAME = "Roy Pierre"
	const val DEVELOPER_EMAIL = "pierre.ayfri@gmail.com"
	const val PROJECT_DESCRIPTION = "A Kotlin DSL to create Minecraft datapacks."
	val COPYRIGHT_YEAR = Year.now().value.toString()
}
