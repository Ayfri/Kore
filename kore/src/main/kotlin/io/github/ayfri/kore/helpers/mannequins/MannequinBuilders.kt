package io.github.ayfri.kore.helpers.mannequins

import io.github.ayfri.kore.arguments.components.item.PlayerProfile
import io.github.ayfri.kore.arguments.components.item.TextureProfile
import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.arguments.types.resources.model
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
fun Mannequin.playerProfile(name: String? = null, id: UUIDArgument? = null, block: PlayerProfile.() -> Unit = {}) {
	profile = PlayerProfile(name = name, id = id).apply(block)
}

/**
 * Sets the profile of the mannequin to a player profile.
 *
 * @param name The name of the player.
 * @param id The UUID of the player.
 * @param block The builder block for the profile.
 */
fun Mannequin.playerProfile(name: String? = null, id: UUID, block: PlayerProfile.() -> Unit = {}) =
	playerProfile(name, UUIDArgument(id), block)

/**
 * Sets the profile of the mannequin to a texture-based profile.
 *
 * @param texture The main texture.
 * @param block The builder block for the profile.
 */
fun Mannequin.textureProfile(texture: String, block: TextureProfile.() -> Unit = {}) {
	profile = TextureProfile(texture = model(texture)).apply(block)
}
