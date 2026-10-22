@file:OptIn(ExperimentalComposeWebApi::class)

package io.github.ayfri.kore.website.components.mc

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.utils.inset
import io.github.ayfri.kore.website.utils.perspective
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Style

/** Players are drawn at 15/16 of their model size, so a block is 16 / 0.9375 skin pixels. */
private const val SKIN_PIXELS_PER_BLOCK = 16 / 0.9375


/** A face of the box: its size in skin pixels and how it is moved onto its side. */
private class HitboxFace(val width: Double, val height: Double, val place: TransformBuilder.() -> Unit)

/**
 * The white wireframe box F3+B draws around an entity, [width] x [height] blocks (and as deep as wide), as an overlay of the [McPlayerModel]
 * of an upright [pose] drawn with the same [scale], [yaw] and `fit = false`. The overlay fills its parent, which must hug the model.
 */
@Composable
fun McHitbox(width: Double, height: Double, pose: McPlayerPose, scale: Int, yaw: Double) {
	Style(McHitboxStyle)

	val s = scale.toDouble()
	val w = width * SKIN_PIXELS_PER_BLOCK * s
	val h = height * SKIN_PIXELS_PER_BLOCK * s
	val feet = pose.centerY + 8
	val faces = listOf(
		HitboxFace(w, h) { translateZ((w / 2).px) },
		HitboxFace(w, h) {
			rotateY(180.deg)
			translateZ((w / 2).px)
		},
		HitboxFace(w, h) {
			rotateY(90.deg)
			translateZ((w / 2).px)
		},
		HitboxFace(w, h) {
			rotateY((-90).deg)
			translateZ((w / 2).px)
		},
		HitboxFace(w, w) {
			rotateX(90.deg)
			translateZ((h / 2).px)
		},
		HitboxFace(w, w) {
			rotateX((-90).deg)
			translateZ((h / 2).px)
		},
	)

	Div({
		classes(McHitboxStyle.overlay)
		style { perspective((150 * s).px) }
	}) {
		Div({
			classes(McPlayerModelStyle.turntable)
			style {
				transform {
					rotateX((-PLAYER_MODEL_TILT_DEGREES).deg)
					rotateY(yaw.deg)
				}
			}
		}) {
			Div({
				classes(McPlayerModelStyle.node)
				style { transform { translate3d(0.px, (feet * s - h / 2).px, 0.px) } }
			}) {
				faces.forEach { face ->
					Div({
						classes(McHitboxStyle.face)
						style {
							height(face.height.px)
							left((-face.width / 2).px)
							top((-face.height / 2).px)
							transform { face.place(this) }
							width(face.width.px)
						}
					})
				}
			}
		}
	}
}

private object McHitboxStyle : StyleSheet() {
	val face by style {
		border(2.px, LineStyle.Solid, Color.white)
		boxSizing(BoxSizing.BorderBox)
		position(Position.Absolute)
	}

	val overlay by style {
		pointerEvents(PointerEvents.None)
		position(Position.Absolute)
		inset(0.px)
	}
}
