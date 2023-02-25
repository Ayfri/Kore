package arguments.selector

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
internal val json = Json {
	ignoreUnknownKeys = true
	allowStructuredMapKeys = true
	explicitNulls = false
	serializersModule = SerializersModule {
		contextual(Advancements::class, Advancements.Companion.AdvancementsSerializer)
		contextual(Advancement::class, Advancement.Companion.AdvancementSerializer)
	}
}

/**
 * Unescape a string.
 * Traverse the string and replace escaped characters with their unescaped version.
 * If the string was inside a double quote, the returned string will be without the double quotes.
 * @receiver The string to unescape.
 * @return The unescaped string.
 */
private fun String.unescape(): String {
	var result = this
	for (i in 0 until result.length - 2) {
		if (i in result.indices && result[i] == '\\') {
			result = result.replaceRange(i, i + 2, result[i + 1].toString())
		}
	}

	return when {
		result.startsWith('"') && result.endsWith('"') -> result.substring(1, result.length - 1)
		else -> result
	}
}

data class Selector(val base: SelectorType) {
	val nbtData = SelectorNbtData()

	override fun toString(): String {
		val builder = StringBuilder("@")
		builder.append(base.value)
		if (nbtData != SelectorNbtData()) {
			builder.append("[")
			builder.append(json.encodeToJsonElement(nbtData).toString().unescape())
			builder.append("]")
		}

		return builder.toString()
	}
}
