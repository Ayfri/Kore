package arguments.types

import arguments.Argument
import arguments.maths.Vec3

interface ContainerArgument : Argument

val ContainerArgument.literalName
	get() = when (this) {
		is Vec3 -> "block"
		is EntityArgument -> "entity"
		else -> error("Unknown container argument type: $this")
	}
