@file:OptIn(ExperimentalComposeWebApi::class)

package io.github.ayfri.kore.website.components.mc

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.url
import io.github.ayfri.kore.website.utils.perspective
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.dom.Div
import kotlin.math.roundToInt

/**
 * The poses a mannequin takes, in the game's order. [angle] turns the whole body in CSS degrees around the point at height [centerY]
 * (feet at 24), [hitbox] is the entity height in skin pixels, which sets the name tag height, and [bounds] is the space the body takes.
 */
enum class McPlayerPose(val angle: Double, val centerY: Double, val hitbox: Int, val bounds: McPoseBounds) {
	STANDING(0.0, 8.0, 29, McPoseBounds(-9.0, 8.5, -16.5, 16.5)),
	CROUCHING(0.0, 10.0, 24, McPoseBounds(-10.0, 8.0, -10.5, 17.5)),
	SWIMMING(-90.0, 17.0, 10, McPoseBounds(-9.5, 14.5, 0.0, 16.5)),
	FALL_FLYING(-90.0, 17.0, 10, McPoseBounds(-9.5, 11.5, 2.0, 15.0)),
	SLEEPING(90.0, 20.0, 3, McPoseBounds(-10.5, 11.0, 4.0, 19.0)),
}

/** The box a pose takes on screen at the default yaw of 25 degrees, in skin pixels from the center of the scene (x right, y down). */
class McPoseBounds(val left: Double, val right: Double, val top: Double, val bottom: Double)

/** The second layers of a skin, which the game lets players hide one by one. */
enum class McSkinLayer { HAT, JACKET, LEFT_PANTS_LEG, LEFT_SLEEVE, RIGHT_PANTS_LEG, RIGHT_SLEEVE }

/** A skin texture at [url], with 3 pixel wide arms when [slim] and the old 64x32 layout (no second layers but the hat) when [legacy]. */
data class McSkin(val url: String, val slim: Boolean, val legacy: Boolean = false)

/**
 * An item held in the hand, drawn as the flat sprite of the `item/` texture at [texture] (relative to `textures/`, without extension).
 * A [tool] is held like the game holds tools, its blade pointing forward and up from the fist, and is drawn bigger than other items.
 */
data class McHeldItem(val texture: String, val tool: Boolean = false)

private const val FIT_PADDING = 1.0
private const val HAND_Y = 10.5
private const val HOLDING_ARM_DEGREES = 18.0
private const val ITEM_SIZE = 8.0
private const val ITEM_TEXELS = 16
private const val SCENE_HEIGHT = 52
private const val SCENE_WIDTH = 44
private const val SEAM_PX = 1.5
/** The downward tilt of the turntable, shared with [McHitbox] so a hitbox drawn over the model stays aligned. */
internal const val PLAYER_MODEL_TILT_DEGREES = 12.0
private const val TOOL_SIZE = 14.0
private const val TOOL_TILT_DEGREES = 25.0

private class Box(val u: Int, val v: Int, val w: Int, val h: Int, val d: Int)

private class Rect(val u: Int, val v: Int, val w: Int, val h: Int)

private class Vec(val x: Double, val y: Double, val z: Double = 0.0)

/** Where a part pivots and how it turns, in skin pixels and CSS degrees. */
private class Placement(val pivot: Vec, val rotateX: Double = 0.0, val rotateZ: Double = 0.0)

/** The six boxes of the model with their skin layout, in skin pixels. */
private enum class Part(val layer: McSkinLayer) {
	HEAD(McSkinLayer.HAT),
	BODY(McSkinLayer.JACKET),
	RIGHT_ARM(McSkinLayer.RIGHT_SLEEVE),
	LEFT_ARM(McSkinLayer.LEFT_SLEEVE),
	RIGHT_LEG(McSkinLayer.RIGHT_PANTS_LEG),
	LEFT_LEG(McSkinLayer.LEFT_PANTS_LEG);

	val isLeft get() = this == LEFT_ARM || this == LEFT_LEG

	/** The width, height and depth of the box, with its skin position [u], [v]: 3 pixel wide arms when [slim]. */
	fun box(slim: Boolean, u: Int, v: Int) = when (this) {
		HEAD -> Box(u, v, 8, 8, 8)
		BODY -> Box(u, v, 8, 12, 4)
		RIGHT_ARM, LEFT_ARM -> Box(u, v, if (slim) 3 else 4, 12, 4)
		RIGHT_LEG, LEFT_LEG -> Box(u, v, 4, 12, 4)
	}

	/** The box of the first layer, the old 64x32 skins having no left limbs and mirroring the right ones. */
	fun baseUv(legacy: Boolean) = when (this) {
		HEAD -> 0 to 0
		BODY -> 16 to 16
		RIGHT_ARM -> 40 to 16
		LEFT_ARM -> if (legacy) 40 to 16 else 32 to 48
		RIGHT_LEG -> 0 to 16
		LEFT_LEG -> if (legacy) 0 to 16 else 16 to 48
	}

	/** The box of the second layer, drawn [inflate] skin pixels outside the first. */
	fun outerUv() = when (this) {
		HEAD -> 32 to 0
		BODY -> 16 to 32
		RIGHT_ARM -> 40 to 32
		LEFT_ARM -> 48 to 48
		RIGHT_LEG -> 0 to 32
		LEFT_LEG -> 0 to 48
	}

	fun inflate() = if (this == HEAD) 0.5 else 0.25

	/** The center of the box relative to its pivot, which the pose never changes. */
	fun center(slim: Boolean) = when (this) {
		HEAD -> Vec(0.0, -4.0)
		BODY -> Vec(0.0, 6.0)
		RIGHT_ARM -> Vec(if (slim) -0.5 else -1.0, 4.0)
		LEFT_ARM -> Vec(if (slim) 0.5 else 1.0, 4.0)
		RIGHT_LEG, LEFT_LEG -> Vec(0.0, 6.0)
	}

	fun placement(pose: McPlayerPose, slim: Boolean): Placement {
		val crouching = pose == McPlayerPose.CROUCHING
		val side = if (isLeft) 1.0 else -1.0
		return when (this) {
			HEAD -> Placement(
				Vec(0.0, if (crouching) 4.2 else 0.0),
				rotateX = if (pose == McPlayerPose.SWIMMING || pose == McPlayerPose.FALL_FLYING) 45.0 else 0.0,
			)
			BODY -> Placement(Vec(0.0, if (crouching) 3.2 else 0.0), rotateX = if (crouching) -28.65 else 0.0)
			RIGHT_ARM, LEFT_ARM -> Placement(
				Vec(5.0 * side, (if (slim) 2.5 else 2.0) + if (crouching) 3.2 else 0.0),
				rotateX = when (pose) {
					McPlayerPose.CROUCHING -> -22.92
					McPlayerPose.SWIMMING -> 180.0
					else -> 0.0
				},
				rotateZ = -2.9 * side,
			)
			RIGHT_LEG, LEFT_LEG -> Placement(
				if (crouching) Vec(2.0 * side, 12.2, -4.0) else Vec(2.0 * side, 12.0),
				rotateX = if (pose == McPlayerPose.SWIMMING) -17.19 * side else 0.0,
			)
		}
	}
}

/** The faces of a box, placed on its surface with the skin rectangle the game's box layout gives each one. */
private enum class Side {
	BACK, BOTTOM, FRONT, LEFT, RIGHT, TOP;

	fun rect(box: Box) = when (this) {
		BACK -> Rect(box.u + 2 * box.d + box.w, box.v + box.d, box.w, box.h)
		BOTTOM -> Rect(box.u + box.d + box.w, box.v, box.w, box.d)
		FRONT -> Rect(box.u + box.d, box.v + box.d, box.w, box.h)
		LEFT -> Rect(box.u + box.d + box.w, box.v + box.d, box.d, box.h)
		RIGHT -> Rect(box.u, box.v + box.d, box.d, box.h)
		TOP -> Rect(box.u + box.d, box.v, box.w, box.d)
	}

	/** The width and height of the face on a box of [w] x [h] x [d]. */
	fun size(w: Double, h: Double, d: Double) = when (this) {
		BACK, FRONT -> w to h
		BOTTOM, TOP -> w to d
		LEFT, RIGHT -> d to h
	}

	/** Puts a face lying in the box's front plane on this side, turning it to look outward. */
	fun place(transform: TransformBuilder, w: Double, h: Double, d: Double) {
		when (this) {
			BACK -> {
				transform.rotateY(180.deg)
				transform.translateZ((d / 2).px)
			}
			BOTTOM -> {
				transform.rotateX((-90).deg)
				transform.translateZ((h / 2).px)
				transform.rotate(180.deg)
			}
			FRONT -> transform.translateZ((d / 2).px)
			LEFT -> {
				transform.rotateY(90.deg)
				transform.translateZ((w / 2).px)
			}
			RIGHT -> {
				transform.rotateY((-90).deg)
				transform.translateZ((w / 2).px)
			}
			TOP -> {
				transform.rotateX(90.deg)
				transform.translateZ((h / 2).px)
			}
		}
	}
}

/**
 * A player model in CSS 3D, with no canvas or WebGL: boxes of textured faces in skin pixels (x right, y down, z toward the viewer),
 * facing +z with the player's right side at -x. [scale] CSS pixels make one skin pixel, [yaw] turns the model in degrees,
 * and [tagLines] are the name tag lines over the head, top to bottom. A [heldItem] sits in the right hand, or in the left one when [mainHandLeft].
 * A [fit] scene is only as big as the pose's [McPlayerPose.bounds] at yaw 25, with no room for tags, for rows of static poses.
 */
@Composable
fun McPlayerModel(
	skin: McSkin,
	pose: McPlayerPose,
	scale: Int,
	yaw: Double,
	label: String,
	hiddenLayers: Set<McSkinLayer> = emptySet(),
	heldItem: McHeldItem? = null,
	mainHandLeft: Boolean = false,
	tagLines: List<String> = emptyList(),
	fit: Boolean = false,
) {
	val s = scale.toDouble()
	val tagY = 24.0 - pose.hitbox - 8
	val bounds = pose.bounds
	val sceneHeight = if (fit) bounds.bottom - bounds.top + 2 * FIT_PADDING else SCENE_HEIGHT.toDouble()
	val sceneWidth = if (fit) bounds.right - bounds.left + 2 * FIT_PADDING else SCENE_WIDTH.toDouble()

	Div({
		classes(McPlayerModelStyle.scene)
		attr("aria-label", label)
		attr("role", "img")
		style {
			height((sceneHeight * s).px)
			perspective((150 * s).px)
			width((sceneWidth * s).px)
		}
	}) {
		Div({
			classes(McPlayerModelStyle.turntable)
			style {
				transform {
					if (fit) translate((-(bounds.left + bounds.right) / 2 * s).px, (-(bounds.top + bounds.bottom) / 2 * s).px)
					rotateX((-PLAYER_MODEL_TILT_DEGREES).deg)
					rotateY(yaw.deg)
				}
			}
		}) {
			if (tagLines.isNotEmpty()) Div({
				classes(McPlayerModelStyle.tag)
				style {
					transform {
						translate3d(0.px, ((tagY - 8) * s).px, 0.px)
						rotateY((-yaw).deg)
						rotateX(PLAYER_MODEL_TILT_DEGREES.deg)
						translate((-50).percent, (-100).percent)
					}
				}
			}) { tagLines.forEach { McNameTag(it) } }

			Div({
				classes(McPlayerModelStyle.node)
				style {
					transform {
						translate3d(0.px, ((pose.centerY - 8) * s).px, 0.px)
						rotateX(pose.angle.deg)
						translate3d(0.px, (-8 * s).px, 0.px)
					}
				}
			}) {
				val holdingArm = if (mainHandLeft) Part.LEFT_ARM else Part.RIGHT_ARM
				Part.entries.forEach { PartModel(it, skin, pose, s, hiddenLayers, if (it == holdingArm) heldItem else null) }
			}
		}
	}
}

@Composable
private fun PartModel(part: Part, skin: McSkin, pose: McPlayerPose, s: Double, hiddenLayers: Set<McSkinLayer>, heldItem: McHeldItem?) {
	val placement = part.placement(pose, skin.slim)
	val (baseU, baseV) = part.baseUv(skin.legacy)
	val (outerU, outerV) = part.outerUv()
	val mirrored = skin.legacy && part.isLeft && part != Part.HEAD

	Div({
		classes(McPlayerModelStyle.node)
		style {
			transform {
				translate3d((placement.pivot.x * s).px, (placement.pivot.y * s).px, (placement.pivot.z * s).px)
				rotateZ(placement.rotateZ.deg)
				rotateX((placement.rotateX + if (heldItem != null) HOLDING_ARM_DEGREES else 0.0).deg)
			}
		}
	}) {
		BoxModel(part, part.box(skin.slim, baseU, baseV), 0.0, mirrored, skin, s)
		if ((!skin.legacy || part == Part.HEAD) && part.layer !in hiddenLayers) {
			BoxModel(part, part.box(skin.slim, outerU, outerV), part.inflate(), mirrored, skin, s)
		}
		if (heldItem != null) HeldItemModel(heldItem, part.center(skin.slim).x, s)
	}
}

/**
 * The model of [item] standing in the plane of the arm, in the fist at [armX], reaching forward: a tool has its grip (bottom left texels)
 * in the hand, so its center sits forward and up from it, and it is tilted toward the horizontal like the game holds it.
 * The sprite's side is a multiple of 16 CSS pixels, so every texel stays on whole pixels.
 */
@Composable
private fun HeldItemModel(item: McHeldItem, armX: Double, s: Double) {
	val size = if (item.tool) TOOL_SIZE else ITEM_SIZE
	val reach = if (item.tool) size * 5.5 / ITEM_TEXELS else size / 2
	val side = (size * s / ITEM_TEXELS).roundToInt().coerceAtLeast(1) * ITEM_TEXELS

	Div({
		classes(McPlayerModelStyle.node)
		style {
			transform {
				translate3d((armX * s).px, (HAND_Y * s).px, 0.px)
				rotateX((if (item.tool) -TOOL_TILT_DEGREES else 0.0).deg)
				translate3d(0.px, ((if (item.tool) -reach else -1.0) * s).px, (reach * s).px)
				rotateY((-90).deg)
			}
		}
	}) { McItemModel(item.texture, side / ITEM_TEXELS) }
}

/** A box of [skin] pixels, mirrored for the left limbs of legacy skins, so its faces show their back to the viewer and must stay visible. */
@Composable
private fun BoxModel(part: Part, box: Box, inflate: Double, mirrored: Boolean, skin: McSkin, s: Double) {
	val center = part.center(skin.slim)
	val w = (box.w + 2 * inflate) * s
	val h = (box.h + 2 * inflate) * s
	val d = (box.d + 2 * inflate) * s
	val textureHeight = if (skin.legacy) 32 else 64

	Div({
		classes(McPlayerModelStyle.node)
		style {
			transform {
				translate3d((center.x * s).px, (center.y * s).px, (center.z * s).px)
				if (mirrored) scaleX(-1)
			}
		}
	}) {
		Side.entries.forEach { side ->
			val rect = side.rect(box)
			val (faceWidth, faceHeight) = side.size(w, h, d)
			Div({
				classes(McPlayerModelStyle.face)
				style {
					backgroundImage(url(skin.url))
					backgroundPosition(BackgroundPosition.of(CSSPosition((-rect.u * faceWidth / rect.w).px, (-rect.v * faceHeight / rect.h).px)))
					backgroundSize(BackgroundSize.of((64 * faceWidth / rect.w).px, (textureHeight * faceHeight / rect.h).px))
					height(faceHeight.px)
					left((-faceWidth / 2).px)
					top((-faceHeight / 2).px)
					transform {
						side.place(this, w, h, d)
						scale3d(1 + SEAM_PX / faceWidth, 1 + SEAM_PX / faceHeight, 1)
					}
					width(faceWidth.px)
					if (mirrored) backfaceVisibility(BackfaceVisibility.Visible)
				}
			}) {}
		}
	}
}

object McPlayerModelStyle : StyleSheet() {
	/** Faces never show their back, so the model is a solid and its inside is never drawn. */
	val face by style {
		backfaceVisibility(BackfaceVisibility.Hidden)
		backgroundRepeat(BackgroundRepeat.NoRepeat)
		imageRendering(ImageRendering.Pixelated)
		position(Position.Absolute)
	}

	/** A zero-sized point the 3D transforms of its children turn around. */
	val node by style {
		height(0.px)
		left(0.px)
		position(Position.Absolute)
		top(0.px)
		transformStyle(TransformStyle.Preserve3d)
		width(0.px)
	}

	/** Clips the stage only: `overflow` on a 3D ancestor of the faces would flatten them. */
	val scene by style {
		maxWidth(100.percent)
		overflow(Overflow.Clip)
		position(Position.Relative)
		userSelect(UserSelect.None)
	}

	val tag by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		left(0.px)
		pointerEvents(PointerEvents.None)
		position(Position.Absolute)
		top(0.px)
		width(Width.MaxContent)
	}

	val turntable by style {
		height(0.px)
		left(50.percent)
		position(Position.Absolute)
		top(50.percent)
		transformStyle(TransformStyle.Preserve3d)
		width(0.px)
	}
}
