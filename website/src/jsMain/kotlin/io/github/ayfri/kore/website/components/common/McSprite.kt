package io.github.ayfri.kore.website.components.common

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.VerticalAlign
import com.varabyte.kobweb.compose.css.verticalAlign
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Span

/** Vanilla textures from misode/mcmeta, hotlinked so Mojang assets never land in this repo. */
const val MC_TEXTURES = "https://raw.githubusercontent.com/misode/mcmeta/assets/assets/minecraft/textures"

fun mcTexture(path: String) = "url('$MC_TEXTURES/$path.png')"

/**
 * An inline pixelated vanilla texture, [path] being relative to `textures/` without extension.
 * Drawn as a background showing the top frame, so animated strips like `block/furnace_front_on` stay square.
 *
 * In doc markdown: `![Crafting Table](mc:block/crafting_table_front)`.
 */
@Composable
fun McSprite(path: String, label: String) {
	Span({
		classes(McSpriteStyle.sprite)
		attr("aria-label", label)
		attr("role", "img")
		attr("title", label)
		style { property("background-image", mcTexture(path)) }
	})
}

object McSpriteStyle : StyleSheet() {
	val sprite by style {
		display(DisplayStyle.InlineBlock)
		height(1.5.em)
		marginRight(0.35.em)
		verticalAlign(VerticalAlign.Middle)
		width(1.5.em)
		property("background-position", "top")
		property("background-repeat", "no-repeat")
		property("background-size", "100% auto")
		property("image-rendering", "pixelated")
	}
}
