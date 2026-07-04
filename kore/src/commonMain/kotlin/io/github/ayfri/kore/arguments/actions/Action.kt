package io.github.ayfri.kore.arguments.actions

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/** Marker interface for all action types ([ClickEvent], [DialogAction]). */
@Serializable
sealed interface ActionType

/** Base sealed class for namespaced actions serialized via [NamespacedPolymorphicSerializer]. */
@GeneratedSealedSerializer
@Serializable(with = Action.Companion.ActionSerializer::class)
sealed class Action : ActionType {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object ActionSerializer : NamespacedPolymorphicSerializer<Action>(actionSealedSerializer())
	}
}

/** Mutable container that accumulates a single [ActionType] from a builder DSL. */
sealed class ActionWrapper<out T : ActionType> {
	abstract var action: @UnsafeVariance T?
}
