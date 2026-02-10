import java.time.Year

data object Project {
	const val GROUP = "io.github.ayfri.kore"
	const val VERSION = "1.40.0"

	// URLS
	const val GITHUB_URL = "github.com/Ayfri/Kore"
	const val WEBSITE_URL = "https://kore.ayfri.com"

	// Developer constants
	const val DEVELOPER_ID = "Ayfri"
	const val DEVELOPER_NAME = "Roy Pierre"
	const val DEVELOPER_EMAIL = "pierre.ayfri@gmail.com"
	const val DEVELOPER_URL = "https://ayfri.com"

	// Meta
	const val DESCRIPTION = "A Kotlin DSL to create Minecraft datapacks."
	val COPYRIGHT_YEAR = Year.now().value.toString()
	const val LICENSE = "MIT"
	const val LICENSE_URL = "https://opensource.org/licenses/MIT"
}
