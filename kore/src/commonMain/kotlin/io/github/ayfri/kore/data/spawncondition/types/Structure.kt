package io.github.ayfri.kore.data.spawncondition.types

import io.github.ayfri.kore.generated.arguments.worldgen.ConfiguredStructureOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Structure(var structures: InlinableList<ConfiguredStructureOrTagArgument>) : VariantCondition
