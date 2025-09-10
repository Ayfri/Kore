package io.github.ayfri.kore.features.dialogs.action.submit

import kotlinx.serialization.Serializable

@Serializable
data class CustomForm(
	var id: String,
) : SubmitMethod()

fun SubmitMethodContainer.customForm(id: String) {
	method = CustomForm(id)
}
