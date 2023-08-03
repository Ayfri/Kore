package pack

import DataPack
import generated.DataPacks
import kotlinx.serialization.Serializable

@Serializable
data class Features(
	var enabled: List<DataPacks> = emptyList(),
)

fun DataPack.features(vararg enabled: DataPacks) {
	features.enabled += enabled
}
