package io.github.ayfri.kore.pack

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.generated.DataPacks
import kotlinx.serialization.Serializable

/**
* Represents the features that are enabled in the datapack.
*
* JSON format reference: https://minecraft.wiki/w/Experiments#Java_Edition
* @property enabled the features that are enabled
*/
@Serializable
data class Features(
	var enabled: List<DataPacks> = emptyList(),
)

/** Enables the given features. */
fun DataPack.features(vararg enabled: DataPacks) {
	if (features == null) features = Features()
	features!!.enabled += enabled
}
