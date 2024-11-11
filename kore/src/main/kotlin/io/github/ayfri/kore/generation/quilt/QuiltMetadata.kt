package io.github.ayfri.kore.generation.quilt

import kotlinx.serialization.Serializable
import java.nio.file.Path

@Serializable
data class QuiltMetadata(
	var name: String? = null,
	var description: String? = null,
	var contributors: Map<String, String>? = null,
	var contact: QuiltContact? = null,
	var license: QuiltLicense? = null,
	var icon: Path? = null,
)

fun QuiltMetadata.contact(init: QuiltContact.() -> Unit) {
	contact = QuiltContact().apply(init)
}

fun QuiltMetadata.contributors(init: MutableMap<String, String>.() -> Unit) {
	contributors = buildMap(init)
}

fun QuiltMetadata.contributor(name: String, role: String) {
	contributors = mapOf(name to role)
}

fun QuiltMetadata.license(identifier: String) {
	license = QuiltLicense(identifier)
}

fun QuiltMetadata.license(id: String, name: String, url: String, description: String? = null) {
	if (license !== null && license!!.licenses !== null) {
		license =
			license!!.copy(licenses = license!!.licenses!! + QuiltLicenseObject(id = id, name = name, url = url, description = description))
	}
	license = QuiltLicense(licenses = listOf(QuiltLicenseObject(id = id, name = name, url = url, description = description)))
}

fun QuiltMetadata.licenses(vararg licenses: QuiltLicenseObject) {
	license = QuiltLicense(licenses = licenses.toList())
}
