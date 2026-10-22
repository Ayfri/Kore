package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.LandingVars
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.components.common.MC_TEXTURES
import io.github.ayfri.kore.website.components.common.Tab
import io.github.ayfri.kore.website.components.common.Tabs
import io.github.ayfri.kore.website.components.mc.McHeldItem
import io.github.ayfri.kore.website.components.mc.McPlayerModel
import io.github.ayfri.kore.website.components.mc.McPlayerPose
import io.github.ayfri.kore.website.components.mc.McSkin
import io.github.ayfri.kore.website.components.mc.McSkinLayer
import io.github.ayfri.kore.website.utils.highlightCodeIn
import io.github.ayfri.kore.website.utils.monoFont
import kotlinx.browser.window
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.await
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*
import org.khronos.webgl.Int8Array
import org.w3c.dom.Image
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import kotlin.coroutines.resume
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

private const val COMMAND_ID = "mannequin-command"
private const val DESCRIPTION_PLACEHOLDER = "NPC"
private const val DRAG_DEGREES_PER_PIXEL = 0.6
private const val KOTLIN_ID = "mannequin-kotlin"
private const val LOOKUP_DEBOUNCE_MS = 400
private const val NAME_LENGTH = 16
private const val POSES_SCALE = 6
private const val STAGE_SCALE = 8
private const val TEXT_LENGTH = 40
private const val UNKNOWN_NAME = "_"

private val ALEX_SKIN = McSkin("$MC_TEXTURES/entity/player/slim/alex.png", slim = true)
private val STEVE_SKIN = McSkin("$MC_TEXTURES/entity/player/wide/steve.png", slim = false)

/**
 * The browser can't call Mojang, so skins come from these mirrors. For a name nobody owns they answer 200 with one default skin,
 * so [skin] compares the download with the default skin of the mirror, fetched once from [UNKNOWN_NAME].
 */
private class SkinHost(val host: String, val url: (String) -> String) {
	private var unknown: ByteArray? = null

	/** The PNG bytes of the skin of [name], or null when the mirror is unreachable or doesn't know the name. */
	suspend fun skin(name: String): ByteArray? {
		val bytes = download(url(name)) ?: return null
		val default = unknown ?: download(url(UNKNOWN_NAME))?.also { unknown = it }
		return bytes.takeUnless { default != null && it.contentEquals(default) }
	}
}

private val SKIN_HOSTS = listOf(
	SkinHost("minotar.net") { "https://minotar.net/skin/$it" },
	SkinHost("mineskin.eu") { "https://mineskin.eu/download/$it" },
)

private fun Enum<*>.label() = name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() }

private fun String.snbt() = "\"" + replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r") + "\""

private fun String.kotlin() = "\"" + replace("\\", "\\\\").replace("\"", "\\\"").replace("\$", "\\\$").replace("\n", "\\n").replace("\r", "\\r") + "\""

private fun wrapYaw(degrees: Double) = (((degrees + 180) % 360 + 360) % 360 - 180).roundToInt().toDouble()

private enum class Arms { WIDE, SLIM }

private enum class Hand { RIGHT, LEFT }

private enum class Item(val label: String, val held: McHeldItem? = null) {
	NONE("None"),
	EMERALD("Emerald", McHeldItem("item/emerald")),
	IRON_SWORD("Iron sword", McHeldItem("item/iron_sword", tool = true)),
	BOOK("Book", McHeldItem("item/book")),
}

private enum class HiddenLayer { CAPE, HAT, JACKET, LEFT_PANTS_LEG, LEFT_SLEEVE, RIGHT_PANTS_LEG, RIGHT_SLEEVE }

private enum class SkinChoice(val label: String) {
	PLAYER("Player name"),
	STEVE("Steve"),
	ALEX("Alex"),
}

/** The builder's choices. [command] and [kotlin] write them the way Kore's `Mannequin` serializes: fields alphabetical by game name for the command, and in the order of the doc page for Kotlin, where the profile calls come before `pose`. */
private data class MannequinSettings(
	val arms: Arms = Arms.WIDE,
	val customName: String = "",
	val customNameVisible: Boolean = false,
	val description: String = "",
	val hiddenLayers: Set<HiddenLayer> = emptySet(),
	val hideDescription: Boolean = false,
	val immovable: Boolean = false,
	val item: Item = Item.NONE,
	val mainHand: Hand = Hand.RIGHT,
	val name: String = "Ayfri",
	val pose: McPlayerPose = McPlayerPose.STANDING,
	val skin: SkinChoice = SkinChoice.PLAYER,
	val yaw: Double = 25.0,
) {
	private val hidden get() = HiddenLayer.entries.filter { it in hiddenLayers }

	/** The name tag lines over the head: the custom name when always visible, then the description, `NPC` by default. */
	val tagLines
		get() = listOfNotNull(
			customName.takeIf { customNameVisible && it.isNotEmpty() },
			description.ifEmpty { DESCRIPTION_PLACEHOLDER }.takeUnless { hideDescription },
		)

	val previewHiddenLayers get() = hidden.mapNotNull { layer -> McSkinLayer.entries.firstOrNull { it.name == layer.name } }.toSet()

	fun command() = listOfNotNull(
		customName.takeIf { it.isNotEmpty() }?.let { "CustomName:${it.snbt()}" },
		"CustomNameVisible:1b".takeIf { customNameVisible },
		description.takeIf { it.isNotEmpty() }?.let { "description:${it.snbt()}" },
		item.takeIf { it != Item.NONE }?.let { "equipment:{mainhand:{id:\"minecraft:${it.name.lowercase()}\"}}" },
		hidden.takeIf { it.isNotEmpty() }?.joinToString(",", "hidden_layers:[", "]") { "\"${it.name.lowercase()}\"" },
		"hide_description:1b".takeIf { hideDescription },
		"immovable:1b".takeIf { immovable },
		"main_hand:\"left\"".takeIf { mainHand == Hand.LEFT },
		pose.takeIf { it != McPlayerPose.STANDING }?.let { "pose:\"${it.name.lowercase()}\"" },
		when (skin) {
			SkinChoice.ALEX -> "profile:{model:\"slim\",texture:\"minecraft:entity/player/slim/alex\"}"
			SkinChoice.PLAYER -> name.takeIf { it.isNotEmpty() }?.let { "profile:{name:${it.snbt()}}" }
			SkinChoice.STEVE -> "profile:{model:\"wide\",texture:\"minecraft:entity/player/wide/steve\"}"
		},
	).joinToString(",", "summon minecraft:mannequin ~ ~ ~ {", "}")

	fun kotlin(): String {
		val fields = listOfNotNull(
			customName.takeIf { it.isNotEmpty() }?.let { "customName = textComponent(${it.kotlin()})" },
			"customNameVisible = true".takeIf { customNameVisible },
			description.takeIf { it.isNotEmpty() }?.let { "description = textComponent(${it.kotlin()})" },
			item.takeIf { it != Item.NONE }?.let { "equipment { this[EquipmentSlot.MAINHAND] = itemStack(Items.${it.name}) }" },
			hidden.takeIf { it.isNotEmpty() }?.joinToString(", ", "hiddenLayers(", ")") { "MannequinLayer.${it.name}" },
			"hideDescription = true".takeIf { hideDescription },
			"immovable = true".takeIf { immovable },
			"mainHand = MannequinHand.LEFT".takeIf { mainHand == Hand.LEFT },
			when (skin) {
				SkinChoice.ALEX -> "textureProfile(Textures.Entity.Player.Slim.ALEX) { model = MannequinModel.SLIM }"
				SkinChoice.PLAYER -> name.takeIf { it.isNotEmpty() }?.let { "playerProfile(${it.kotlin()})" }
				SkinChoice.STEVE -> "textureProfile(Textures.Entity.Player.Wide.STEVE) { model = MannequinModel.WIDE }"
			},
			pose.takeIf { it != McPlayerPose.STANDING }?.let { "pose = MannequinPose.${it.name}" },
		)
		val body = if (fields.isEmpty()) "mannequin {}" else fields.joinToString("\n\t", "mannequin {\n\t", "\n}")
		return "val npc = $body\n\nsummon(npc.entityType, vec3(), npc.toNbt())"
	}

	/** What the preview shows, for screen readers: the skin, the pose, the hidden layers and the name tag lines. */
	fun ariaLabel(skinName: String) = buildString {
		append("A mannequin wearing $skinName, ${pose.label().lowercase()}")
		if (hiddenLayers.isNotEmpty()) append(", with ${hidden.joinToString(", ") { it.label().lowercase() }} hidden")
		if (tagLines.isNotEmpty()) append(", labelled ${tagLines.joinToString(" over ") { "\"$it\"" }}")
		append(".")
	}
}

/** The outcome of looking up [name]: a [skin] found on [host], or null when no mirror knows the name. */
private class Lookup(val name: String, val skin: McSkin?, val host: String?)

private suspend fun loadImage(url: String): Image? = suspendCancellableCoroutine { continuation ->
	val image = Image()
	image.addEventListener("load", { continuation.resume(image) })
	image.addEventListener("error", { continuation.resume(null) })
	image.src = url
	continuation.invokeOnCancellation { image.src = "" }
}

private suspend fun download(url: String): ByteArray? = try {
	val response = window.fetch(url).await()
	if (response.ok) Int8Array(response.arrayBuffer().await()).unsafeCast<ByteArray>() else null
} catch (error: CancellationException) {
	throw error
} catch (_: Throwable) {
	null
}

/** The skin of [name] from the first mirror that knows it, as a blob URL the caller revokes once the lookup is replaced. */
private suspend fun lookUp(name: String): Lookup {
	for (host in SKIN_HOSTS) {
		val bytes = host.skin(name) ?: continue
		val url = URL.createObjectURL(Blob(arrayOf(bytes), BlobPropertyBag(type = "image/png")))
		val image = try {
			loadImage(url)
		} catch (error: CancellationException) {
			URL.revokeObjectURL(url)
			throw error
		}
		if (image == null) {
			URL.revokeObjectURL(url)
			continue
		}
		return Lookup(name, McSkin(url, slim = false, legacy = image.naturalHeight == 32), host.host)
	}
	return Lookup(name, null, null)
}

private fun isNameChar(char: Char) = char in 'A'..'Z' || char in 'a'..'z' || char in '0'..'9' || char == '_'

/** Builds a `minecraft:mannequin` summon from a few choices, drawn as a 3D preview and written as a `summon` command and as Kore Kotlin. */
@Composable
fun MannequinBuilder() {
	Style(MannequinBuilderStyle)

	var settings by remember { mutableStateOf(MannequinSettings()) }
	var lookup by remember { mutableStateOf<Lookup?>(null) }

	LaunchedEffect(settings.name, settings.skin) {
		val name = settings.name
		if (settings.skin != SkinChoice.PLAYER || name.isEmpty() || lookup?.name == name) return@LaunchedEffect
		delay(LOOKUP_DEBOUNCE_MS.milliseconds)
		val next = lookUp(name)
		lookup?.skin?.url?.let { URL.revokeObjectURL(it) }
		lookup = next
	}
	DisposableEffect(Unit) { onDispose { lookup?.skin?.url?.let { URL.revokeObjectURL(it) } } }

	val found = lookup?.takeIf { settings.skin == SkinChoice.PLAYER && settings.name.isNotEmpty() }?.skin
	val skin = when (settings.skin) {
		SkinChoice.ALEX -> ALEX_SKIN
		SkinChoice.STEVE -> STEVE_SKIN
		SkinChoice.PLAYER -> found?.copy(slim = settings.arms == Arms.SLIM) ?: ALEX_SKIN
	}
	val caption = when {
		settings.skin == SkinChoice.STEVE -> "Texture profile, the wide Steve skin of the game."
		settings.skin == SkinChoice.ALEX -> "Texture profile, the slim Alex skin of the game."
		settings.name.isEmpty() -> "No profile, so the mannequin wears the default slim Alex skin."
		lookup?.name != settings.name -> "Looking up the skin of ${settings.name}..."
		found == null -> "No skin found for this name, showing Alex."
		else -> "Skin of ${settings.name} from ${lookup?.host}. The game does its own lookup, this is only a preview."
	}
	val skinName = when {
		skin == STEVE_SKIN -> "the Steve skin"
		found == null -> "the Alex skin"
		else -> "the skin of ${settings.name}"
	}
	val command = settings.command()
	val kotlin = settings.kotlin()

	Div({ classes(MannequinBuilderStyle.builder) }) {
		Div({ classes(MannequinBuilderStyle.layout) }) {
			Div({
				classes(DocControlsStyle.panel, MannequinBuilderStyle.stage)
				ref { stage ->
					var dragging = false
					var startX = 0
					var startYaw = 0.0
					val onDown: (Event) -> Unit = { event ->
						dragging = true
						startX = event.unsafeCast<MouseEvent>().clientX
						startYaw = settings.yaw
						stage.asDynamic().setPointerCapture(event.asDynamic().pointerId)
					}
					val onMove: (Event) -> Unit = { event ->
						if (dragging) settings = settings.copy(yaw = wrapYaw(startYaw + (event.unsafeCast<MouseEvent>().clientX - startX) * DRAG_DEGREES_PER_PIXEL))
					}
					val onEnd: (Event) -> Unit = { dragging = false }
					stage.addEventListener("pointerdown", onDown)
					stage.addEventListener("pointermove", onMove)
					stage.addEventListener("pointerup", onEnd)
					stage.addEventListener("pointercancel", onEnd)
					onDispose {
						stage.removeEventListener("pointerdown", onDown)
						stage.removeEventListener("pointermove", onMove)
						stage.removeEventListener("pointerup", onEnd)
						stage.removeEventListener("pointercancel", onEnd)
					}
				}
			}) {
				McPlayerModel(
					skin = skin,
					pose = settings.pose,
					scale = STAGE_SCALE,
					yaw = settings.yaw,
					label = settings.ariaLabel(skinName),
					hiddenLayers = settings.previewHiddenLayers,
					heldItem = settings.item.held,
					mainHandLeft = settings.mainHand == Hand.LEFT,
					tagLines = settings.tagLines,
				)
				Div({ classes(MannequinBuilderStyle.caption) }) { Text(caption) }
			}

			Div({ classes(DocControlsStyle.panel, MannequinBuilderStyle.controls) }) {
				Field("Skin") {
					PillGroup("Skin", SkinChoice.entries, settings.skin, SkinChoice::label) { settings = settings.copy(skin = it) }
				}
				if (settings.skin == SkinChoice.PLAYER) {
					TextField("mannequin-name", "Player name", settings.name, "Ayfri", NAME_LENGTH) {
						settings = settings.copy(name = it.filter(::isNameChar).take(NAME_LENGTH))
					}
				}
				if (found != null) Field("Arms in the preview") {
					PillGroup("Arms in the preview", Arms.entries, settings.arms, { it.label() }) { settings = settings.copy(arms = it) }
				}
				Field("Pose") {
					PillGroup("Pose", McPlayerPose.entries, settings.pose, { it.label() }) { settings = settings.copy(pose = it) }
				}
				TextField("mannequin-custom-name", "Custom name", settings.customName, "", TEXT_LENGTH) {
					settings = settings.copy(customName = it)
				}
				Pill("Name always visible", settings.customNameVisible) { settings = settings.copy(customNameVisible = !settings.customNameVisible) }
				TextField("mannequin-description", "Description", settings.description, DESCRIPTION_PLACEHOLDER, TEXT_LENGTH) {
					settings = settings.copy(description = it)
				}
				Pill("Hide description", settings.hideDescription) { settings = settings.copy(hideDescription = !settings.hideDescription) }
				Field("Hidden layers") {
					ToggleGroup("Hidden layers", HiddenLayer.entries, settings.hiddenLayers, { it.label() }) { layer ->
						settings = settings.copy(hiddenLayers = if (layer in settings.hiddenLayers) settings.hiddenLayers - layer else settings.hiddenLayers + layer)
					}
				}
				Field("Main hand") {
					PillGroup("Main hand", Hand.entries, settings.mainHand, { it.label() }) { settings = settings.copy(mainHand = it) }
				}
				Field("Main hand item") {
					PillGroup("Main hand item", Item.entries, settings.item, Item::label) { settings = settings.copy(item = it) }
				}
				Pill("Immovable", settings.immovable) { settings = settings.copy(immovable = !settings.immovable) }
				SliderField("mannequin-yaw", "Rotation", settings.yaw, -180.0, 180.0, 1.0) { settings = settings.copy(yaw = it.coerceIn(-180.0, 180.0)) }
			}
		}

		Tabs(
			listOf(
				Tab("Command") { Div({ id(COMMAND_ID) }) { key(command) { CodeBlock(command, "mcfunction") } } },
				Tab("Kotlin") { Div({ id(KOTLIN_ID) }) { key(kotlin) { CodeBlock(kotlin, "kotlin") } } },
			),
		)
		highlightCodeIn(COMMAND_ID, command)
		highlightCodeIn(KOTLIN_ID, kotlin)
	}
}

/** The five poses of a mannequin side by side, on the Steve skin of the game. */
@Composable
fun MannequinPoses() {
	Style(MannequinBuilderStyle)

	Div({ classes(DocControlsStyle.panel, MannequinBuilderStyle.poses) }) {
		McPlayerPose.entries.forEach { pose ->
			Div({ classes(MannequinBuilderStyle.pose) }) {
				McPlayerModel(STEVE_SKIN, pose, POSES_SCALE, 25.0, "A mannequin ${pose.label().lowercase()}", fit = true)
				Span({ classes(DocControlsStyle.controlLabel) }) { Text(pose.label()) }
			}
		}
	}
}

@Composable
private fun Field(label: String, content: @Composable () -> Unit) {
	Div({ classes(DocControlsStyle.control) }) {
		Span({ classes(DocControlsStyle.controlLabel) }) { Text(label) }
		content()
	}
}

private object MannequinBuilderStyle : StyleSheet() {
	val builder by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)
		margin(1.5.cssRem, 0.px)
		minWidth(0.px)
		width(100.percent)
	}

	val caption by style {
		color(LandingVars.Muted.value())
		fontSize(0.8.cssRem)
		maxWidth((STAGE_SCALE * 44).px)
		textAlign(TextAlign.Center)
	}

	val controls by style {
		display(DisplayStyle.Flex)
		flexBasis(18.cssRem)
		flexDirection(FlexDirection.Column)
		flexGrow(1)
		flexShrink(1)
		gap(0.9.cssRem)
	}

	val layout by style {
		alignItems(AlignItems.FlexStart)
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(1.cssRem)
		minWidth(0.px)
	}

	val pose by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.2.cssRem)
		maxWidth(100.percent)
	}

	val poses by style {
		alignItems(AlignItems.FlexEnd)
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.6.cssRem, 0.8.cssRem)
		justifyContent(JustifyContent.Center)
		margin(1.5.cssRem, 0.px)
		padding(0.75.cssRem)
	}

	val stage by style {
		alignItems(AlignItems.Center)
		cursor(Cursor.Grab)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		flexShrink(1)
		gap(0.6.cssRem)
		touchAction(TouchAction.PanY)
	}
}
