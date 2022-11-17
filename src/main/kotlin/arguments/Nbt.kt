package arguments

import functions.Function
import kotlinx.serialization.encodeToString
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.StringifiedNbt
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.put
import net.benwoodworth.knbt.putNbtCompound

fun Function.nbt(block: NbtCompoundBuilder.() -> Unit) = buildNbtCompound(block)
fun NbtCompoundBuilder.nbt(name: String, block: NbtCompoundBuilder.() -> Unit) = putNbtCompound(name, block)
fun NbtCompoundBuilder.json(name: String, block: NbtCompoundBuilder.() -> Unit) = put(name, StringifiedNbt.encodeToString(buildNbtCompound(block)))

fun Function.nbt(nbt: NbtTag) = literal(StringifiedNbt.encodeToString(nbt))
