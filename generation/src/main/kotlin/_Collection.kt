fun Collection<String>.removeSuffix(suffix: String) = map { it.substringBeforeLast(suffix) }
fun Collection<String>.removePrefix(prefix: String) = map { it.substringAfter(prefix) }

fun Collection<String>.removeMinecraftPrefix() = removePrefix("minecraft:")
fun Collection<String>.removeJSONSuffix() = removeSuffix(".json")
