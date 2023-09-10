package io.github.ayfri.kore.pack

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.generated.DataPacks
import kotlinx.serialization.Serializable

@Serializable
data class Features(
	var enabled: List<DataPacks> = emptyList(),
)

fun DataPack.features(vararg enabled: DataPacks) {
	features.enabled += enabled
}
