package io.github.ayfri.kore.features.predicates.sub.entityspecific

import io.github.ayfri.kore.features.predicates.sub.Entity
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = LlamaVariants.Companion.LlamaVariantSerializer::class)
enum class LlamaVariants {
	CREAMY,
	WHITE,
	BROWN,
	GRAY;

	companion object {
		data object LlamaVariantSerializer : LowercaseSerializer<LlamaVariants>(entries)
	}
}

@Serializable
data class Llama(var variant: LlamaVariants? = null) : EntityTypeSpecific()

fun Entity.llamaTypeSpecific(variant: LlamaVariants? = null, block: Llama.() -> Unit = {}) = apply {
	typeSpecific = Llama(variant).apply(block)
}
