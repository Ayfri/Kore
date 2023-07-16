package features.worldgen.noisesettings.rules

import features.worldgen.noisesettings.NoiseSettings
import kotlinx.serialization.Serializable

@Serializable
data class Sequence(
	var sequence: List<SurfaceRule>,
) : SurfaceRule()

fun NoiseSettings.surfaceRules(block: MutableList<SurfaceRule>.() -> Unit) {
	surfaceRule = Sequence(buildList(block))
}

fun NoiseSettings.surfaceRules(vararg rules: SurfaceRule) {
	surfaceRule = Sequence(rules.toList())
}

fun sequence(block: MutableList<SurfaceRule>.() -> Unit) = Sequence(buildList(block))
fun sequence(vararg rules: SurfaceRule) = Sequence(rules.toList())
