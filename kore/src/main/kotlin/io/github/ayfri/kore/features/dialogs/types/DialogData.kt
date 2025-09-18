package io.github.ayfri.kore.features.dialogs.types

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.features.dialogs.action.AfterAction
import io.github.ayfri.kore.features.dialogs.body.BodyContainer
import io.github.ayfri.kore.features.dialogs.body.DialogBody
import io.github.ayfri.kore.features.dialogs.control.ControlContainer
import io.github.ayfri.kore.features.dialogs.control.DialogControl
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = DialogData.Companion.DialogDataSerializer::class)
sealed class DialogData {
	/** Screen title, text component. Should be always visible on screen, no matter the specific type. */
	abstract var title: ChatComponents

	/** Name to be used for a button leading to this dialog (for example, on the pause menu).
	 * If not present, title will be used instead. */
	abstract var externalTitle: ChatComponents?

	/** An additional operation performed on the dialog after click or submit actions. Defaults to [AfterAction.CLOSE]. */
	abstract var afterAction: AfterAction?

	/** Optional list of body elements or a single body element. */
	abstract var body: InlinableList<DialogBody>?

	/** Can dialog be dismissed with `Escape` key. Defaults to `true`. */
	abstract var canCloseWithEscape: Boolean?

	/** Optional list of input controls. */
	abstract var inputs: List<DialogControl>

	/** If the dialog screen should pause the game in single-player mode. Defaults to `true`. */
	abstract var pause: Boolean?

	companion object {
		data object DialogDataSerializer : NamespacedPolymorphicSerializer<DialogData>(DialogData::class)
	}
}

/** Sets the [DialogData.body] elements of the dialog, describing content between the title and actions or inputs. */
fun DialogData.bodies(block: BodyContainer.() -> Unit) = apply {
	body = BodyContainer().apply(block).bodies
}

/** Set the [DialogData.externalTitle] of the dialog, which is used as the button text leading to this dialog. */
fun DialogData.externalTitle(text: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	externalTitle = textComponent(text, color, block)
}

/** Sets the [DialogData.inputs] elements of the dialog, describing input controls. */
fun DialogData.inputs(block: ControlContainer.() -> Unit) = apply {
	inputs = ControlContainer().apply(block).controls
}

/** Sets the [DialogData.title] of the dialog, which is visible on the dialog screen. */
fun DialogData.title(text: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	title = textComponent(text, color, block)
}
