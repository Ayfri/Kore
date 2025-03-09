package io.github.ayfri.kore.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SealedSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind

@OptIn(SealedSerializationApi::class)
data class MixedListSerialDescriptor(
	private val elementTypes: List<KSerializer<*>>,
) : SerialDescriptor {
	override val elementsCount = elementTypes.size

	override val kind = StructureKind.LIST

	override val serialName = "MixedListType"

	override fun getElementAnnotations(index: Int): List<Annotation> {
		require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices" }
		return emptyList()
	}

	override fun getElementDescriptor(index: Int): SerialDescriptor {
		require(index in 0..<elementsCount) { "Invalid index $index for $serialName" }
		return elementTypes[index].descriptor
	}

	override fun getElementIndex(name: String) =
		name.toIntOrNull() ?: throw IllegalArgumentException("$name is not a valid list index")

	override fun getElementName(index: Int) = index.toString()

	override fun isElementOptional(index: Int): Boolean {
		require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices" }
		return false
	}
}

fun mixedListSerialDescriptor(vararg elementTypes: KSerializer<*>) = MixedListSerialDescriptor(elementTypes.toList())
