package data.block

data class BlockStatePropertiesBuilder(val properties: MutableMap<String, String> = mutableMapOf()) {
	operator fun contains(key: String) = key in properties

	operator fun get(key: String) = properties[key]

	operator fun set(key: String, value: String) {
		properties[key] = value
	}

	operator fun set(key: String, value: Number) {
		properties[key] = value.toString()
	}

	operator fun set(key: String, value: Boolean) {
		properties[key] = value.toString()
	}

	fun clear() = properties.clear()
	fun remove(key: String) = properties.remove(key)

	fun putAll(vararg properties: Pair<String, String>) = this.properties.putAll(properties)
	fun putAll(vararg properties: Pair<String, Number>) = this.properties.putAll(properties.map { it.first to it.second.toString() })
	fun putAll(vararg properties: Pair<String, Boolean>) = this.properties.putAll(properties.map { it.first to it.second.toString() })

	fun putAll(properties: Map<String, String>) = this.properties.putAll(properties)
	fun putAll(properties: Map<String, Number>) = this.properties.putAll(properties.map { it.key to it.value.toString() })
	fun putAll(properties: Map<String, Boolean>) = this.properties.putAll(properties.map { it.key to it.value.toString() })

	fun putAll(properties: BlockStatePropertiesBuilder) = this.properties.putAll(properties.properties)
}
