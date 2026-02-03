package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import io.github.ayfri.kore.utils.snakeCase
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable(with = ComponentMatcher.Companion.ComponentMatcherSerializer::class)
sealed class ComponentMatcher {
	internal open val componentName get() = getComponentName(this::class)

	companion object {
		fun <T : ComponentMatcher> getComponentName(kClass: KClass<out T>) =
			kClass.simpleName!!.removeSuffix("ComponentMatcher").snakeCase()

		data object ComponentMatcherSerializer : NamespacedPolymorphicSerializer<ComponentMatcher>(
			ComponentMatcher::class,
			skipOutputName = true
		)
	}
}
