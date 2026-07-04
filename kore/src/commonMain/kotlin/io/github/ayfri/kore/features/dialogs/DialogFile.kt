package io.github.ayfri.kore.features.dialogs

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.dialogs.types.DialogData
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven dialog definition.
 *
 * Dialogs are simple modal windows that can display information and receive player input.
 *
 * JSON format reference: https://minecraft.wiki/w/Dialog
 */
@Serializable
data class Dialog(
	@Transient
	override var fileName: String = "dialog",
	var data: DialogData,
) : Generator("dialog") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(data)
}

/** The builder for [Dialog]s. **/
val DataPack.dialogBuilder get() = DialogContainer(this)

/**
 * Declare [Dialog] files in this [DataPack].

 * Produces `data/<namespace>/dialog/<fileName>.json`.

 * Docs: [https://minecraft.wiki/w/Dialog](https://minecraft.wiki/w/Dialog)
 */
fun DataPack.dialogs(block: DialogContainer.() -> Unit) = DialogContainer(this).apply(block)
