package io.github.ayfri.kore.features.chattypes

import kotlinx.serialization.Serializable

@Serializable
data class ChatTypeDecoration(
	var translationKey: String,
	var parameters: MutableList<ChatTypeParameter> = mutableListOf(),
	var style: ChatTypeStyle? = null,
)

fun ChatTypeDecoration.parameters(vararg parameter: ChatTypeParameter) = parameters.addAll(parameter)

fun ChatTypeDecoration.style(block: ChatTypeStyle.() -> Unit) {
	style = ChatTypeStyle().apply(block)
}
