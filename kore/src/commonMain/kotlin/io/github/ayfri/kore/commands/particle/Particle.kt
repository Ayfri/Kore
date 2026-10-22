package io.github.ayfri.kore.commands.particle

import io.github.ayfri.kore.arguments.EnumArgument
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.command
import io.github.ayfri.kore.data.particle.ParticleOptions
import io.github.ayfri.kore.data.particle.asCommandArgument
import io.github.ayfri.kore.data.particle.simpleParticle
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(ParticleMode.Companion.ParticleModeSerializer::class)
enum class ParticleMode : EnumArgument {
	NORMAL,
	FORCE;

	companion object {
		data object ParticleModeSerializer : LowercaseSerializer<ParticleMode>(entries)
	}
}

/**
 * Spawns [particle] with its options, at [pos].
 *
 * [delta], [speed], [count], [mode] and [viewers] need a [pos], and the game reads [delta], [speed] and [count] together, so setting
 * any of the five fills the others with `0 0 0`, `0` and `0`. A [mode] is required before [viewers] and defaults to
 * [ParticleMode.NORMAL] then.
 *
 * ```
 * particle(dustParticle(Color.RED), vec3(), count = 5) // particle minecraft:dust{color:16733525,scale:1.0f} ~ ~ ~ 0 0 0 0 5
 * particle(simpleParticle(Particles.ASH))              // particle minecraft:ash
 * particle(simpleParticle(Particles.ASH), count = 5)   // throws, a position is missing
 * ```
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/particle)
 */
fun Function.particle(
	particle: ParticleOptions,
	pos: Vec3? = null,
	delta: Vec3? = null,
	speed: Double? = null,
	count: Int? = null,
	mode: ParticleMode? = null,
	viewers: EntityArgument? = null,
): Command {
	val motion = delta != null || speed != null || count != null || mode != null || viewers != null
	require(pos != null || !motion) { "The particle command needs a position before delta, speed, count, mode or viewers." }

	return addLine(
		command(
			"particle",
			particle.asCommandArgument(),
			pos,
			if (motion) delta ?: vec3(0, 0, 0) else null,
			if (motion) float(speed ?: 0.0) else null,
			if (motion) int(count ?: 0) else null,
			mode ?: if (viewers != null) ParticleMode.NORMAL else null,
			viewers
		)
	)
}

/**
 * Spawns the option-less particle [particle], at [pos]. The other parameters work as in the [ParticleOptions] overload.
 *
 * ```
 * particle(Particles.FLAME, vec3()) // particle minecraft:flame ~ ~ ~
 * ```
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/particle)
 */
fun Function.particle(
	particle: ParticleTypeArgument,
	pos: Vec3? = null,
	delta: Vec3? = null,
	speed: Double? = null,
	count: Int? = null,
	mode: ParticleMode? = null,
	viewers: EntityArgument? = null,
) = particle(simpleParticle(particle), pos, delta, speed, count, mode, viewers)
