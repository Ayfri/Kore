package selectors

import arguments.SelectorType

val SelectorType.isSingle get() = this == SelectorType.NEAREST_PLAYER || this == SelectorType.RANDOM_PLAYER
