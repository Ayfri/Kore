@file:OptIn(ExperimentalComposeWebApi::class)

package io.github.ayfri.kore.website.components.mc

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.components.common.mcTexture
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

private const val MAX_SLICES = 8
private const val TEXELS = 16

/**
 * An item texture with the depth the game gives items, one texel, drawn in CSS 3D with no canvas (so no CORS on the hotlinked texture):
 * the sprite repeated as a few slices across that depth, plus one cross-section per texel column that makes the edges solid seen from the side.
 * It is centered on its node's origin and faces +z, [texelPx] CSS pixels per texture pixel, so its sprite is `16 * texelPx` wide.
 * Semi-transparent texels stack up and look more opaque than they are.
 */
@Composable
fun McItemModel(texture: String, texelPx: Int) {
	Style(McItemModelStyle)

	val size = TEXELS * texelPx
	val slices = texelPx.coerceIn(2, MAX_SLICES)

	Div({ classes(McItemModelStyle.node) }) {
		repeat(slices) { slice ->
			Div({
				classes(McItemModelStyle.slice)
				style {
					backgroundImage(mcTexture(texture))
					height(size.px)
					left((-size / 2.0).px)
					top((-size / 2.0).px)
					transform { translateZ((texelPx * (slice / (slices - 1.0) - 0.5)).px) }
					width(size.px)
				}
			}) {}
		}

		repeat(TEXELS) { column ->
			Div({
				classes(McItemModelStyle.slice)
				style {
					backgroundImage(mcTexture(texture))
					backgroundPosition(BackgroundPosition.of(CSSPosition((-column * texelPx).px, 0.px)))
					backgroundSize(BackgroundSize.of(size.px, size.px))
					height(size.px)
					left((-texelPx / 2.0).px)
					top((-size / 2.0).px)
					transform {
						translateX(((column + 0.5) * texelPx - size / 2.0).px)
						rotateY(90.deg)
					}
					width(texelPx.px)
				}
			}) {}
		}
	}
}

private object McItemModelStyle : StyleSheet() {
	/** A zero-sized point the 3D transforms of its children turn around. */
	val node by style {
		height(0.px)
		left(0.px)
		position(Position.Absolute)
		top(0.px)
		transformStyle(TransformStyle.Preserve3d)
		width(0.px)
	}

	/** Seen from both sides, since the slices of an item are never culled. */
	val slice by style {
		backfaceVisibility(BackfaceVisibility.Visible)
		backgroundRepeat(BackgroundRepeat.NoRepeat)
		backgroundSize(BackgroundSize.of(100.percent, 100.percent))
		imageRendering(ImageRendering.Pixelated)
		position(Position.Absolute)
	}
}
