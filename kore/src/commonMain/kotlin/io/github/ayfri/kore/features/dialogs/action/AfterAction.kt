package io.github.ayfri.kore.features.dialogs.action

import io.github.ayfri.kore.features.dialogs.types.DialogData
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/** An additional operation performed on the dialog after click or submit actions. */
@Serializable(with = AfterAction.Companion.ExitActionSerializer::class)
enum class AfterAction {
	/** Closes the dialog and returns to the previous non-dialog screen (if any). */
	CLOSE,

	/** Does nothing, i.e. keeps the current dialog screen open
	 * (only available if [DialogData.pause] is `false` to avoid locking the game in single-player mode). */
	NONE,

	/** Replace the current dialog with a **`Waiting for Response`** screen. */
	WAIT_FOR_RESPONSE;

	companion object {
		data object ExitActionSerializer : LowercaseSerializer<AfterAction>(entries)
	}
}
