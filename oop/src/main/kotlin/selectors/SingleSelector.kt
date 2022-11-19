package selectors

import arguments.SelectorNbtData
import arguments.SelectorType
import entities.Entity
import entities.toEntity

class SingleSelector<out T : Entity>(val base: SelectorType, val nbtData: SelectorNbtData) {
	init {
		if (!base.isSingle && nbtData.limit != 1) {
			nbtData.limit = 1
		}
	}
	
	inline fun <reified T : Entity> toEntity() = Entity(nbtData).toEntity<T>()
}
