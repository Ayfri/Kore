package io.github.ayfri.kore.data.particle

import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import io.github.ayfri.kore.utils.encodeToNbtTag
import io.github.ayfri.kore.utils.toSnbt
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound

/**
 * A particle and the options its kind reads, as `spawn_particles`, `explode`, and the ambient and dripstone particles of
 * environment attributes take it. Build one with the factories of this package:
 *
 * ```
 * simpleParticle(Particles.ASH)           // {"type": "minecraft:ash"}
 * dustParticle(Color.RED, scale = 1.5f)   // {"type": "minecraft:dust", "color": 16733525, "scale": 1.5}
 * ```
 *
 * [type] is the first field of every subtype, as vanilla writes it, and the other fields are alphabetical: that is the
 * one place the ordering rule of the codebase gives way to the game's own order.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle_format
 */
@GeneratedSealedSerializer
@Serializable(with = ParticleOptions.Companion.ParticleOptionsSerializer::class)
sealed class ParticleOptions {
	/** The id of the particle, deciding which options are read. */
	abstract var type: ParticleTypeArgument

	companion object {
		data object ParticleOptionsSerializer : NamespacedPolymorphicSerializer<ParticleOptions>(
			particleOptionsSealedSerializer(),
			subtypeOf = ::particleOptionsSubtype,
		)
	}
}

/** The `/particle` form of these options: the namespaced ID, followed by the SNBT of every field but [ParticleOptions.type]. */
internal fun ParticleOptions.asCommandArgument() = literal(
	(encodeToNbtTag(ParticleOptions.serializer(), this) as NbtCompound - "type").let {
		type.asId() + if (it.isEmpty()) "" else NbtCompound(it).toSnbt()
	}
)

/** Mirrors the vanilla-mcdoc `minecraft:particle` dispatch. */
private val subtypeByParticle = mapOf(
	"block" to "block_particle_options",
	"block_crumble" to "block_particle_options",
	"block_marker" to "block_particle_options",
	"dragon_breath" to "dragon_breath_particle_options",
	"dust" to "dust_particle_options",
	"dust_color_transition" to "dust_color_transition_particle_options",
	"dust_pillar" to "block_particle_options",
	"effect" to "effect_particle_options",
	"entity_effect" to "color_particle_options",
	"falling_dust" to "block_particle_options",
	"flash" to "color_particle_options",
	"geyser" to "geyser_particle_options",
	"geyser_base" to "geyser_burst_particle_options",
	"geyser_plume" to "geyser_particle_options",
	"geyser_poof" to "geyser_burst_particle_options",
	"instant_effect" to "effect_particle_options",
	"item" to "item_particle_options",
	"sculk_charge" to "sculk_charge_particle_options",
	"shriek" to "shriek_particle_options",
	"tinted_leaves" to "color_particle_options",
	"trail" to "trail_particle_options",
	"vibration" to "vibration_particle_options",
)

private fun particleOptionsSubtype(type: String) = subtypeByParticle[type] ?: "simple_particle_options"
