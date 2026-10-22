package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.components.common.MC_TEXTURES
import io.github.ayfri.kore.website.components.mc.McHitbox
import io.github.ayfri.kore.website.components.mc.McPlayerModel
import io.github.ayfri.kore.website.components.mc.McPlayerPose
import io.github.ayfri.kore.website.components.mc.McSkin
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*

private const val NPC_HITBOX_SCALE = 4
private const val NPC_HITBOX_WIDTH = 0.7
private const val NPC_HITBOX_YAW = 25.0

private val NPC_HITBOX_SKIN = McSkin("$MC_TEXTURES/entity/player/wide/steve.png", slim = false)

/** An NPC in [pose] whose interaction is [height] blocks tall, the pose's mannequin hitbox plus 0.1. */
private class NpcHitboxPose(val pose: McPlayerPose, val height: Double)

private val NPC_HITBOX_POSES = listOf(
	NpcHitboxPose(McPlayerPose.STANDING, 1.9),
	NpcHitboxPose(McPlayerPose.CROUCHING, 1.6),
)

/** The `STANDING` and `CROUCHING` mannequins with the hitbox of their interaction, 0.1 bigger than the mannequin's own. */
@Composable
fun NpcHitboxes() {
	Style(NpcHitboxesStyle)

	Div({ classes(DocControlsStyle.panel, NpcHitboxesStyle.row) }) {
		NPC_HITBOX_POSES.forEach { npc ->
			Div({ classes(NpcHitboxesStyle.pose) }) {
				Div({ classes(NpcHitboxesStyle.stage) }) {
					McPlayerModel(
						NPC_HITBOX_SKIN, npc.pose, NPC_HITBOX_SCALE, NPC_HITBOX_YAW,
						"A ${npc.pose.name.lowercase()} NPC with its ${NPC_HITBOX_WIDTH} x ${npc.height} blocks interaction box",
						tagLines = listOf("Shopkeeper"),
					)
					McHitbox(NPC_HITBOX_WIDTH, npc.height, npc.pose, NPC_HITBOX_SCALE, NPC_HITBOX_YAW)
				}
				Span({ classes(DocControlsStyle.controlLabel) }) { Text("${npc.pose.name}, $NPC_HITBOX_WIDTH x ${npc.height}") }
			}
		}
	}
}

private object NpcHitboxesStyle : StyleSheet() {
	val pose by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.2.cssRem)
		maxWidth(100.percent)
	}

	val row by style {
		alignItems(AlignItems.FlexEnd)
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.6.cssRem, 2.cssRem)
		justifyContent(JustifyContent.Center)
		margin(1.5.cssRem, 0.px)
		padding(0.75.cssRem)
	}

	val stage by style {
		maxWidth(100.percent)
		position(Position.Relative)
	}
}
