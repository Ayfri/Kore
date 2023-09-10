package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.stringifiedNbt
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtTag
import kotlinx.serialization.Serializable

@Serializable
data class SetNbt(
	override var conditions: PredicateAsList? = null,
	val nbt: String,
) : ItemFunction() {
	constructor(nbt: NbtTag) : this(nbt = stringifiedNbt(nbt))
}

fun ItemModifier.setNbt(nbt: NbtTag, block: SetNbt.() -> Unit = {}) {
	modifiers += SetNbt(nbt).apply(block)
}

fun ItemModifier.setNbt(block: NbtCompoundBuilder.() -> Unit) =
	SetNbt(nbt(block)).also { modifiers += it }
