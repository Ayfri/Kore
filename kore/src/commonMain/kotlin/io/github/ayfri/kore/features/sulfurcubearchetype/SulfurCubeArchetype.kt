package io.github.ayfri.kore.features.sulfurcubearchetype

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.tagged.ItemTagArgument
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.generated.arguments.types.AttributeArgument
import io.github.ayfri.kore.generated.arguments.types.SulfurCubeArchetypeArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Represents an attribute modifier applied by a sulfur cube archetype.
 *
 * @property amount The amount applied by the modifier.
 * @property attribute The attribute being modified.
 * @property id The identifier of the modifier.
 * @property operation How [amount] is combined with the attribute's base value.
 */
@Serializable
data class SulfurCubeArchetypeAttributeModifier(
	var amount: Double,
	var attribute: AttributeArgument,
	var id: String,
	var operation: AttributeModifierOperation,
)

/**
 * Data-driven sulfur cube archetype definition.
 *
 * Controls the attribute modifiers, buoyancy, explosion fuse, and valid item contents of a sulfur cube.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/sulfur-cube-archetypes
 * Minecraft Wiki: https://minecraft.wiki/w/Sulfur_cube_archetype_definition
 */
@Serializable
data class SulfurCubeArchetype(
	@Transient
	override var fileName: String = "sulfur_cube_archetype",
	var attributeModifiers: MutableList<SulfurCubeArchetypeAttributeModifier>,
	var buoyant: Boolean,
	var explosionFuse: Int? = null,
	var items: ItemTagArgument,
) : Generator("sulfur_cube_archetype") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Creates a sulfur cube archetype using a builder block.
 *
 * Produces `data/<namespace>/sulfur_cube_archetype/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/sulfur-cube-archetypes
 * Minecraft Wiki: https://minecraft.wiki/w/Sulfur_cube_archetype_definition
 */
fun DataPack.sulfurCubeArchetype(
	fileName: String = "sulfur_cube_archetype",
	items: ItemTagArgument,
	buoyant: Boolean = false,
	init: SulfurCubeArchetype.() -> Unit = {},
): SulfurCubeArchetypeArgument {
	val archetype = SulfurCubeArchetype(fileName, mutableListOf(), buoyant, items = items).apply(init)
	sulfurCubeArchetypes += archetype
	return SulfurCubeArchetypeArgument(fileName, archetype.namespace ?: name)
}

fun SulfurCubeArchetype.modifier(
	amount: Double,
	attribute: AttributeArgument,
	id: String,
	operation: AttributeModifierOperation,
) {
	attributeModifiers += SulfurCubeArchetypeAttributeModifier(amount, attribute, id, operation)
}
