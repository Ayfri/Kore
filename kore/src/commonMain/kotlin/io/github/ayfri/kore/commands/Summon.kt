package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.EntityTypeArgument
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.nbtArg
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder

/**
 * Summons [entity] at [pos] and writes the NBT payload as SNBT.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/summon)
 */
fun Function.summon(entity: EntityTypeArgument, pos: Vec3, nbt: NbtCompound?) = addLine(command("summon", entity, pos, nbt(nbt)))

/**
 * Summons [entity] at [pos] and serializes the builder output as SNBT.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/summon)
 */
fun Function.summon(entity: EntityTypeArgument, pos: Vec3 = vec3(), nbt: (NbtCompoundBuilder.() -> Unit)? = null) =
	addLine(command("summon", entity, pos, nbt?.let { nbtArg(nbt) }))
