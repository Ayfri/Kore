---
root: .components.layouts.MarkdownLayout
title: MiniMessage Renderer
nav-title: MiniMessage Renderer
description: Parse Adventure MiniMessage format strings into Minecraft text components using the Kore helpers module.
keywords: minecraft, datapack, kore, helpers, minimessage, adventure, text, component, renderer, parser, color, decoration, click, hover
date-created: 2026-03-03
date-modified: 2026-03-31
routeOverride: /docs/helpers/minimessage-renderer
---

# MiniMessage Renderer

The `helpers` module includes a MiniMessage renderer that
parses [Adventure MiniMessage](https://docs.advntr.dev/minimessage/format.html)
format strings into Kore `ChatComponents`. This lets you write human-readable styled text that compiles into Minecraft's
JSON text component format.

MiniMessage is part of the [Adventure](https://github.com/KyoriPowered/adventure) library, widely used by Paper,
Velocity,
and other Minecraft server platforms. You can preview MiniMessage text in the
[MiniMessage Web Viewer](https://webui.advntr.dev/) without starting a Minecraft instance.

## Basic Usage

```kotlin
import io.github.ayfri.kore.text.miniMessageToTextComponents

val components = miniMessageToTextComponents("<red>Hello <bold>world</bold>!</red>")
```

The function returns a `ChatComponents` instance that can be used anywhere Kore expects text components.

## Configuration

Pass a configuration lambda to customize defaults:

```kotlin
val components = miniMessageToTextComponents("<bold>Styled</bold> text") {
	color = Color.AQUA      // default color for unstyled text
	bold = true              // default bold state
	italic = false           // default italic state
	font = "minecraft:alt"   // default font
	strict = true            // throw on parse errors
}
```

## Colors

### Named Colors

All vanilla formatting colors are supported:

```
<red>Red text</red>
<blue>Blue text</blue>
<light_purple>Purple text</light_purple>
```

Aliases `<color:name>` and `<c:name>` are also supported:

```
<color:red>Red text</color>
<c:blue>Blue text</c>
```

### Hexadecimal Colors

```
<#ff0000>Red text
<color:#00ff00>Green text</color>
<c:#0000ff>Blue text</c>
```

### Gradients

Gradient tags are recognized and apply the first color in the gradient:

```
<gradient:red:blue>Gradient text</gradient>
```

### Rainbow & Transition

Rainbow and transition tags are recognized as style tags:

```
<rainbow>Rainbow text</rainbow>
<transition:red:blue:0.5>Transition text</transition>
```

## Decorations

| Tag               | Aliases       | Effect        |
|-------------------|---------------|---------------|
| `<bold>`          | `<b>`         | **Bold**      |
| `<italic>`        | `<i>`, `<em>` | *Italic*      |
| `<underlined>`    | `<u>`         | Underlined    |
| `<strikethrough>` | `<st>`        | Strikethrough |
| `<obfuscated>`    | `<obf>`       | Obfuscated    |

All decorations support closing tags to limit their scope:

```
<bold>Bold <italic>and italic</italic> just bold</bold>
```

### Reset

The `<reset>` (or `<r>`) tag clears all active styles and returns to defaults:

```
<bold><red>Styled text<reset>Normal text
```

## Click Events

```
<click:open_url:'https://ayfri.com'>Click to visit</click>
<click:run_command:/spawn>Click to spawn</click>
<click:suggest_command:/msg >Click to message</click>
<click:change_page:2>Next page</click>
<click:copy_to_clipboard:Copied!>Click to copy</click>
```

## Hover Events

```
<hover:show_text:'<red>Tooltip text'>Hover me</hover>
<hover:show_item:minecraft:diamond_sword:1>Hover for item</hover>
<hover:show_entity:minecraft:pig:uuid:'Name'>Hover for entity</hover>
```

The `show_text` value is itself parsed as MiniMessage, so you can use tags inside hover text.

## Fonts

```
<font:minecraft:uniform>Uniform font text</font>
<font:kore:icons>Icon font</font>
```

## Insertion

Shift-clicking the rendered component inserts the specified text into the chat prompt:

```
<insert:hello>Shift-click me</insert>
```

## Newlines

```
Line one<newline>Line two
Line one<br>Line two
```

## Advanced Components

### Translatable (I18n)

```
<lang:chat.type.text>
<translate:chat.type.text:Player:Message>
```

### Keybinds

```
<key:key.forward>
<keybind:key.jump>
```

### Selectors

```
<selector:@e[type=pig,limit=1]>
```

### Scoreboards

```
<score:player_name:objective_name>
```

### NBT Data

```
<nbt:block:0,64,0:Items[0].id>
<nbt:entity:@s:Health>
<nbt:storage:namespace\:id:path.to.data>
```

## Escaping & Preformatted Text

### Backslash Escaping

Prefix a tag with `\` to render it as literal text:

```
\<red>This is not red
```

### Preformatted Blocks

Everything inside `<pre>...</pre>` is treated as raw text:

```
<pre><red>This renders as literal <red> tags</red></pre>
```

## Tag Resolvers

Custom placeholder tags can be resolved dynamically at render time:

```kotlin
val components = miniMessageToTextComponents("<player_name> joined the game") {
	tagResolvers = mapOf(
		"player_name" to TagResolver { text("Ayfri") { color = Color.GOLD } }
	)
}
```

The resolved component inherits the current style context (color, bold, italic, font) unless it defines its own.

## Strict Mode

Enable strict mode to throw a `MiniMessageParseException` on parse errors:

```kotlin
miniMessageToTextComponents("<bold>Unclosed tag") {
	strict = true  // throws MiniMessageParseException("Unclosed tags: bold")
}
```

Strict mode catches:

- Unclosed tags
- Unknown/unrecognized tags
- Closing tags with no matching opening tag

## References

- [MiniMessage Format Specification](https://docs.advntr.dev/minimessage/format.html) - full tag reference from the
  Adventure docs.
- [MiniMessage Web Viewer](https://webui.advntr.dev/) - interactive playground to preview MiniMessage text.
- [Adventure GitHub Repository](https://github.com/KyoriPowered/adventure) - the library that defines the MiniMessage
  format.
- [Minecraft Wiki - Raw JSON Text](https://minecraft.wiki/w/Raw_JSON_text_format) - the underlying component format
  MiniMessage compiles to.
