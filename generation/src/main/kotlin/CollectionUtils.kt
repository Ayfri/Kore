fun Collection<String>.removeMinecraftPrefix() = map { it.substringAfter("minecraft:") }
