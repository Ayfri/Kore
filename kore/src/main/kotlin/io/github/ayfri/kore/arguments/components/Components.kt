package io.github.ayfri.kore.arguments.components

import kotlinx.serialization.Serializable

@Serializable(with = ComponentsSerializer::class)
class Components : ComponentsScope()
