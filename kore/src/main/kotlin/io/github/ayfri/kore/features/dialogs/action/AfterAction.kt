package io.github.ayfri.kore.features.dialogs.action

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = AfterAction.Companion.ExitActionSerializer::class)
enum class AfterAction {
	CLOSE,
	NONE,
	WAIT_FOR_RESPONSE;

	companion object {
		data object ExitActionSerializer : LowercaseSerializer<AfterAction>(entries)
	}
}
