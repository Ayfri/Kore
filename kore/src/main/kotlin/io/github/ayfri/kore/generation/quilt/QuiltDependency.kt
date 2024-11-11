package io.github.ayfri.kore.generation.quilt

import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.Serializable

@Serializable(with = QuiltDependency.Companion.QuiltDependencySerializer::class)
data class QuiltDependency(
	var id: String,
	var versions: String? = null,
	var reason: String? = null,
	var optional: Boolean? = null,
	var unless: QuiltDependency? = null,
) {
	companion object {
		data object QuiltDependencySerializer : SinglePropertySimplifierSerializer<QuiltDependency, String>(
			kClass = QuiltDependency::class,
			property = QuiltDependency::id,
		)
	}
}
