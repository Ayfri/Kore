package io.github.ayfri.kore.selectors

import io.github.ayfri.kore.arguments.selector.SelectorNbtData
import io.github.ayfri.kore.arguments.selector.SelectorType
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.entities.toEntity

class SingleSelector<out T : Entity>(val base: SelectorType, val nbtData: SelectorNbtData) {
	init {
		if (!base.isSingle && nbtData.limit != 1) {
			nbtData.limit = 1
		}
	}

	inline fun <reified T : Entity> toEntity() = Entity(nbtData).toEntity<T>()
}
