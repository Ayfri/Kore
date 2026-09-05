package io.github.ayfri.kore.features.itemmodifiers.formulas

import kotlinx.serialization.Serializable

/** `apply_bonus` formula specific to ore drops. Mirrors vanilla `ore_drops`. */
@Serializable
data object OreDrops : Formula
