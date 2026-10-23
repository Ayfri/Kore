package io.github.ayfri.kore.features.predicates.providers.utils

import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument
import io.github.ayfri.kore.features.predicates.providers.ContextFloatProviderReference
import io.github.ayfri.kore.features.predicates.providers.ContextIntProviderReference

private val Any?.isTagReference
	get() = this is ContextIntProviderReference && id is TaggedResourceLocationArgument ||
		this is ContextFloatProviderReference && id is TaggedResourceLocationArgument

/** The game reads `inputs` as a holder set, so a `#tag` is valid as the whole field and never next to other inputs. */
internal fun List<*>.requireTagOnlyAlone() = require(size == 1 || none { it.isTagReference }) {
	"A tag can only be the whole list, not one of its elements."
}
