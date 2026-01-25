package io.github.ayfri.kore.helpers.mannequins

import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import java.util.*

/**
 * DSL builder for a [Mannequin].
 *
 * @param block The builder block.
 * @return The created [Mannequin].
 */
fun mannequin(block: Mannequin.() -> Unit) = Mannequin().apply(block)

/**
 * Sets the hidden layers of the mannequin.
 *
 * @param layers The layers to hide.
 */
fun Mannequin.hiddenLayers(vararg layers: MannequinLayer) {
	hiddenLayers = layers.toList()
}

/**
 * Sets the profile of the mannequin to a player profile.
 *
 * @param name The name of the player.
 * @param id The UUID of the player.
 * @param block The builder block for the profile.
 */
fun Mannequin.playerProfile(name: String? = null, id: UUIDArgument? = null, block: PlayerMannequinProfile.() -> Unit = {}) {
	profile = PlayerMannequinProfile(id = id, name = name).apply(block)
}

/**
 * Sets the profile of the mannequin to a player profile.
 *
 * @param name The name of the player.
 * @param id The UUID of the player.
 * @param block The builder block for the profile.
 */
fun Mannequin.playerProfile(name: String? = null, id: UUID, block: PlayerMannequinProfile.() -> Unit = {}) =
	playerProfile(name, UUIDArgument(id), block)

/**
 * Sets the profile of the mannequin to a texture-based profile.
 *
 * @param texture The main texture.
 * @param block The builder block for the profile.
 */
fun Mannequin.textureProfile(texture: String, block: TextureMannequinProfile.() -> Unit = {}) {
	profile = TextureMannequinProfile(texture = texture).apply(block)
}
