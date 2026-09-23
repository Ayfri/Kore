package io.github.ayfri.kore.utils

import io.github.ayfri.kore.arguments.types.resources.ModelArgument

/**
 * The vanilla baby texture of an adult one, reading the path from [ModelArgument.asId] since generated texture enums
 * keep their constant name in `name`.
 *
 * ```kotlin
 * Textures.Entity.Cat.CAT_TABBY.babyTexture() // minecraft:entity/cat/cat_tabby_baby
 * ```
 */
internal fun ModelArgument.babyTexture() = ModelArgument("${asId().substringAfter(':')}_baby", namespace)
