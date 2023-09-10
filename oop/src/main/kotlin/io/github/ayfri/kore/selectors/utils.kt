package io.github.ayfri.kore.selectors

import io.github.ayfri.kore.arguments.selector.SelectorType

val SelectorType.isSingle get() = this == SelectorType.NEAREST_PLAYER || this == SelectorType.RANDOM_PLAYER
