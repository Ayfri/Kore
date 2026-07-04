package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import io.github.ayfri.kore.utils.snakeCase
import kotlinx.serialization.Serializable

@Serializable
sealed class ComponentMatcher {
	internal open val componentName get() = this::class.simpleName!!.removeSuffix("ComponentMatcher").snakeCase()

	companion object {
		data object ComponentMatcherSerializer : NamespacedPolymorphicSerializer<ComponentMatcher>(
			serializer(),
			skipOutputName = true,
			contentName = { it.substringAfterLast('.').removeSuffix("ComponentMatcher").snakeCase() },
		)
	}
}
