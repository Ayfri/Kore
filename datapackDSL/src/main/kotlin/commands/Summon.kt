package commands

import arguments.maths.Vec3
import arguments.maths.vec3
import arguments.types.resources.EntityTypeArgument
import functions.Function
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder
import utils.nbt
import utils.nbtArg

fun Function.summon(entity: EntityTypeArgument, pos: Vec3, nbt: NbtCompound) = addLine(command("summon", entity, pos, nbt(nbt)))
fun Function.summon(entity: EntityTypeArgument, pos: Vec3 = vec3(), nbt: (NbtCompoundBuilder.() -> Unit)? = null) =
	addLine(command("summon", entity, pos, nbt?.let { nbtArg(nbt) }))
