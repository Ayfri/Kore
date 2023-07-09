package features.itemmodifiers.functions

import data.item.BannerPattern
import kotlinx.serialization.Serializable

@Serializable
data class SetBannerPattern(
	val patterns: List<BannerPattern>
) : ItemFunctionSurrogate
