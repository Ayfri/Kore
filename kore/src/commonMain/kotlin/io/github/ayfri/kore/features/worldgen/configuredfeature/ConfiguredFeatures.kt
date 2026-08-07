package io.github.ayfri.kore.features.worldgen.configuredfeature

import io.github.ayfri.kore.DataPack

/**
 * Builder scope for declaring configured features via [configuredFeatures].
 *
 * Each `FeatureConfig` type (e.g. [io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Geode])
 * exposes an extension function on this class that creates one [ConfiguredFeature] file, so a
 * configured feature can only ever hold the single config it was declared with.
 */
data class ConfiguredFeatures(val dp: DataPack)
