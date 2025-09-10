package io.github.ayfri.kore.features.dialogs.action.submit

import kotlinx.serialization.Serializable

@Serializable
data class CustomTemplate(
	var template: String,
	var id: String,
) : SubmitMethod()

fun SubmitMethodContainer.customTemplate(template: String, id: String) {
	method = CustomTemplate(template, id)
}
