package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Matches slimes and magma cubes by size - renamed from `minecraft:slime` to `minecraft:cube_mob` in 26.2-snapshot-3. */
@Serializable
@SerialName("cube_mob")
data class CubeMobSubPredicate(var size: IntRangeOrIntJson? = null) : EntitySubPredicate()

fun EntityTypeSpecificScope.cubeMob(size: IntRangeOrIntJson? = null, block: CubeMobSubPredicate.() -> Unit = {}) {
	entity.subPredicates += CubeMobSubPredicate(size).apply(block)
}
