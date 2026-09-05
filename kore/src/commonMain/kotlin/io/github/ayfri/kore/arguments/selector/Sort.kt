package io.github.ayfri.kore.arguments.selector

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/** Sorting options for selector results (maps to the `sort` argument). */
@Serializable(Sort.Companion.SortSerializer::class)
enum class Sort {
    /** Nearest targets first. */
    NEAREST,
    /** Furthest targets first. */
    FURTHEST,
    /** Random order. */
    RANDOM,
    /** Arbitrary order (implementation-defined). */
    ARBITRARY;

    companion object {
        data object SortSerializer : LowercaseSerializer<Sort>(entries)
    }
}
