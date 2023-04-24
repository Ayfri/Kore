package features.worldgen.intproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("minecraft:constant")
//@JvmInline
/* value */ class ConstantIntProvider(val value: Int) : IntProvider
