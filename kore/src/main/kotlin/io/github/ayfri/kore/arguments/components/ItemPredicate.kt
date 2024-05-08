package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.unescape
import kotlinx.serialization.Serializable

@Serializable(with = ComponentsSerializer::class)
data class ItemPredicate(
	var itemArgument: ItemArgument? = null,
) : ComponentsRemovables() {

	override fun toString() = (itemArgument?.asString() ?: "") + asNbt().entries
		.joinToString(separator = ",", prefix = "[", postfix = "]") { (key, value) ->
			// The quotes are added by the serializer, we just need to unescape the string.
			if (key in CHAT_COMPONENTS_COMPONENTS_TYPES) {
				val unescaped = value.toString().unescape()
					// we also need a fix for JSON Components as they are serialized as JSON but single quoted.
					.replace(Regex("\"\'\"(.+?)\"\'\"", RegexOption.DOT_MATCHES_ALL), "'\"$1\"'")
					.replace(Regex("\"\'\\{(.+?)\\}\'\"", RegexOption.DOT_MATCHES_ALL), "'{$1}'")
				"$key=$unescaped"
			} else if (value == nbt {}) {
				key
			} else "$key=$value"
		}
}

fun ItemArgument.predicates(block: ItemPredicate.() -> Unit) = ItemPredicate(this).apply(block)
