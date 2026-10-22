package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import io.github.ayfri.kore.utils.defaultContentName
import kotlinx.serialization.Serializable

private const val SUFFIX = "ComponentMatcher"

@GeneratedSealedSerializer
@Serializable
sealed class ComponentMatcher {
	internal open val componentName get() = defaultContentName(this::class.simpleName!!, SUFFIX)

	companion object {
		data object ComponentMatcherSerializer : NamespacedPolymorphicSerializer<ComponentMatcher>(
			componentMatcherSealedSerializer(),
			skipOutputName = true,
			suffix = SUFFIX,
		)
	}
}
