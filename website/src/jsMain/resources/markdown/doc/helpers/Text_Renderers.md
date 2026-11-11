---
root: .components.layouts.MarkdownLayout
title: Minecraft Text from MiniMessage, Markdown and ANSI
nav-title: Text Renderers
description: Turn MiniMessage, Markdown or ANSI text into Minecraft text components with Kore's helpers module. Colors, decorations, click and hover events, links and fonts.
keywords: minimessage, minimessage viewer, minimessage renderer, minimessage parser, adventure text, minecraft text components, minimessage generator, mini message format, kore minimessage, minimessage to chat component, minecraft, datapack, kore, helpers, markdown, ansi, sgr, escape, text, renderer, color, bold, italic, link, heading, list
date-created: 2026-03-03
date-modified: 2026-11-11
routeOverride: /docs/helpers/text-renderers
---

# Text Renderers

The `helpers` module includes three text renderers. Each one turns a formatted source string into Kore `ChatComponents`, the
Minecraft text component format used by [tellraw](https://minecraft.wiki/w/Commands/tellraw), title commands and boss bar
names. This lets you write human-readable styled text that compiles into Minecraft's JSON text component format.

| Renderer                                                    | Function                      | Input syntax                                                                                                                                | Typical source of the text                                                              |
|-------------------------------------------------------------|-------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------|
| [MiniMessage](/docs/helpers/text-renderers#minimessage)     | `miniMessageToTextComponents` | [Adventure MiniMessage](https://docs.advntr.dev/minimessage/format.html) tags such as `<red>` or `<click:run_command:/spawn>`               | Text written for Paper, Velocity and other server platforms, previewable in a web viewer |
| [Markdown](/docs/helpers/text-renderers#markdown)           | `markdownToTextComponents`    | [Markdown](https://commonmark.org/) with inline styles, headings, lists, quotes and links                                                   | Markdown-formatted text, with Minecraft extensions for underline, obfuscation and color |
| [ANSI](/docs/helpers/text-renderers#ansi)                   | `ansiToTextComponents`        | [ANSI SGR escape sequences](https://en.wikipedia.org/wiki/ANSI_escape_code#SGR_(Select_Graphic_Rendition)_parameters) such as `\u001B[1;31m` | Text already carrying ANSI escape codes                                                 |

> [!FOOTPRINT]
> Nothing at runtime: no objective, storage, entity, forceloaded chunk, tick or load function. The renderers run while Kore builds the pack and return `ChatComponents`.

## Shared configuration

Each renderer takes an optional configuration lambda. These four properties exist on all three:

| Property | Default | Description                                                                  |
|----------|---------|------------------------------------------------------------------------------|
| `color`  | `null`  | Default text color, a fallback when the source text sets none                |
| `bold`   | `null`  | Default bold state                                                           |
| `italic` | `null`  | Default italic state                                                         |
| `font`   | `null`  | Minecraft font resource location                                             |

The properties specific to one renderer are listed in its section: `strict` and `tagResolvers` for MiniMessage, the heading,
code, link, blockquote, bullet and rule options for Markdown.

## MiniMessage

The MiniMessage renderer parses [Adventure MiniMessage](https://docs.advntr.dev/minimessage/format.html)
format strings into Kore `ChatComponents`.

MiniMessage is part of the [Adventure](https://github.com/KyoriPowered/adventure) library, widely used by Paper,
Velocity,
and other Minecraft server platforms. You can preview MiniMessage text in the
[MiniMessage Web Viewer](https://webui.advntr.dev/) without starting a Minecraft instance.

### Using MiniMessage

```kotlin
import io.github.ayfri.kore.text.miniMessageToTextComponents

val components = miniMessageToTextComponents("<red>Hello <bold>world</bold>!</red>")
```

The function returns a `ChatComponents` instance that can be used anywhere Kore expects text components.

### MiniMessage configuration

Pass a configuration lambda to customize defaults. On top of the [shared properties](/docs/helpers/text-renderers#shared-configuration), MiniMessage has
`strict` and `tagResolvers`:

```kotlin
val components = miniMessageToTextComponents("<bold>Styled</bold> text") {
	color = Color.AQUA      // default color for unstyled text
	bold = true              // default bold state
	italic = false           // default italic state
	font = "minecraft:alt"   // default font
	strict = true            // throw on parse errors
}
```

### Colors

#### Named Colors

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

#### Hexadecimal Colors

```
<#ff0000>Red text
<color:#00ff00>Green text</color>
<c:#0000ff>Blue text</c>
```

#### Gradients

Gradient tags are recognized and apply the first color in the gradient:

```
<gradient:red:blue>Gradient text</gradient>
```

#### Rainbow & Transition

Rainbow and transition tags are recognized as style tags:

```
<rainbow>Rainbow text</rainbow>
<transition:red:blue:0.5>Transition text</transition>
```

### Decorations

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

#### Reset

The `<reset>` (or `<r>`) tag clears all active styles and returns to defaults:

```
<bold><red>Styled text<reset>Normal text
```

### Click Events

```
<click:open_url:'https://ayfri.com'>Click to visit</click>
<click:run_command:/spawn>Click to spawn</click>
<click:suggest_command:/msg >Click to message</click>
<click:change_page:2>Next page</click>
<click:copy_to_clipboard:Copied!>Click to copy</click>
```

### Hover Events

```
<hover:show_text:'<red>Tooltip text'>Hover me</hover>
<hover:show_item:minecraft:diamond_sword:1>Hover for item</hover>
<hover:show_entity:minecraft:pig:uuid:'Name'>Hover for entity</hover>
```

The `show_text` value is itself parsed as MiniMessage, so you can use tags inside hover text.

### Fonts

```
<font:minecraft:uniform>Uniform font text</font>
<font:kore:icons>Icon font</font>
```

### Insertion

Shift-clicking the rendered component inserts the specified text into the chat prompt:

```
<insert:hello>Shift-click me</insert>
```

### Newlines

```
Line one<newline>Line two
Line one<br>Line two
```

### Advanced Components

#### Translatable (I18n)

```
<lang:chat.type.text>
<translate:chat.type.text:Player:Message>
```

#### Keybinds

```
<key:key.forward>
<keybind:key.jump>
```

#### Selectors

```
<selector:@e[type=pig,limit=1]>
```

#### Scoreboards

```
<score:player_name:objective_name>
```

#### NBT Data

```
<nbt:block:0,64,0:Items[0].id>
<nbt:entity:@s:Health>
<nbt:storage:namespace\:id:path.to.data>
```

### Escaping & Preformatted Text

#### Backslash Escaping

Prefix a tag with `\` to render it as literal text:

```
\<red>This is not red
```

#### Preformatted Blocks

Everything inside `<pre>...</pre>` is treated as raw text:

```
<pre><red>This renders as literal <red> tags</red></pre>
```

### Tag Resolvers

Custom placeholder tags can be resolved dynamically at render time:

```kotlin
val components = miniMessageToTextComponents("<player_name> joined the game") {
	tagResolvers = mapOf(
		"player_name" to TagResolver { text("Ayfri") { color = Color.GOLD } }
	)
}
```

The resolved component inherits the current style context (color, bold, italic, font) unless it defines its own.

### Strict Mode

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

## Markdown

The Markdown renderer converts [Markdown](https://commonmark.org/)-formatted text into Minecraft `ChatComponents`.

### Using Markdown

```kotlin
val lines = markdownToTextComponents(
	"""
# Welcome
Hello **world**! Click [here](https://example.com).
- item one
- item two
""".trimIndent()
)
```

Each input line becomes a `ChatComponents` instance. Inline styles are parsed and mapped to Minecraft text component
properties.

### Supported inline syntax

| Markdown syntax     | Minecraft property     | Description                      |
|---------------------|------------------------|----------------------------------|
| `**text**`          | `bold = true`          | Bold                             |
| `*text*` / `_text_` | `italic = true`        | Italic                           |
| `~~text~~`          | `strikethrough = true` | Strikethrough                    |
| `__text__`          | `underlined = true`    | Underline (Minecraft extension)  |
| `\|\|text\|\|`      | `obfuscated = true`    | Obfuscated (Minecraft extension) |
| `` `code` ``        | `color` (codeColor)    | Inline code span                 |
| `[label](url)`      | `clickEvent = OpenUrl` | Clickable link                   |
| `§(#rrggbb)text§()` | `color` (RGB)          | Custom colored span              |

### Supported block syntax

| Markdown syntax        | Rendering                            |
|------------------------|--------------------------------------|
| `# Heading` - `######` | Bold + heading color (configurable)  |
| `- item` / `* item`    | Unordered list with bullet prefix    |
| `1. item`              | Ordered list with number prefix      |
| `> quote`              | Blockquote with `│ ` prefix          |
| `---` / `***` / `___`  | Horizontal rule (strikethrough line) |

### Not supported (Minecraft limitations)

The following Markdown features are **not supported** because Minecraft text components cannot represent them:

- Images (`![alt](url)`)
- Tables
- Nested blockquotes
- Multi-line code blocks (fenced ` ``` `)
- HTML tags
- Footnotes
- Task lists (`- [ ]` / `- [x]`)
- Reference-style links (`[text][ref]`)

### Markdown examples

```kotlin
// Bold + italic combined
val styled = markdownToTextComponents("**bold and *italic* text**")

// Link with custom color
val link = markdownToTextComponents("Visit [Kore](https://kore.ayfri.com)") {
	linkColor = Color.GREEN
}

// Custom color syntax
val colored = markdownToTextComponents("§(#ff0000)red text§() normal text")

// Heading with inline styles
val heading = markdownToTextComponents("# Welcome to **Kore**")
```

{{{ .components.mc.MarkdownRendererMockup }}}

### Markdown configuration

On top of the [shared properties](/docs/helpers/text-renderers#shared-configuration), Markdown has:

| Property           | Default       | Description                                  |
|--------------------|---------------|----------------------------------------------|
| `headingColors`    | level 1-6 map | Color per heading level (gold, yellow, etc.) |
| `codeColor`        | `Color.GRAY`  | Color for inline code spans                  |
| `linkColor`        | `Color.AQUA`  | Color for link text                          |
| `linkUnderline`    | `true`        | Whether links are underlined                 |
| `blockquotePrefix` | `"│ "`        | String prepended to blockquote lines         |
| `bulletChar`       | `"• "`        | Bullet string for unordered list items       |
| `hrChar`           | `"─"`         | Character repeated for horizontal rules      |
| `hrLength`         | `20`          | Number of repetitions for horizontal rules   |

## ANSI

The ANSI renderer converts text
containing [ANSI SGR escape sequences](https://en.wikipedia.org/wiki/ANSI_escape_code#SGR_(Select_Graphic_Rendition)_parameters)
into Minecraft `ChatComponents`. ANSI codes are stripped from the output and mapped to Minecraft text component styles.

### Using ANSI

```kotlin
val lines = ansiToTextComponents("\u001B[1;31mBold red\u001B[0m normal text")
// Produces: [{bold:true, color:"red", text:"Bold red"}, {text:" normal text"}]
```

Each input line becomes a `ChatComponents` instance. Consecutive characters sharing the same style are merged into
runs for efficiency.

### ANSI with config defaults

```kotlin
val lines = ansiToTextComponents("\u001B[32mGreen\u001B[0m rest") {
	color = Color.WHITE
}
// "Green" gets green from ANSI, "rest" gets white from config fallback
```

### Supported ANSI SGR codes

| ANSI code             | Minecraft property     | Description              |
|-----------------------|------------------------|--------------------------|
| `\x1B[1m`             | `bold = true`          | Bold on                  |
| `\x1B[3m`             | `italic = true`        | Italic on                |
| `\x1B[4m`             | `underlined = true`    | Underline on             |
| `\x1B[8m`             | `obfuscated = true`    | Obfuscated on            |
| `\x1B[9m`             | `strikethrough = true` | Strikethrough on         |
| `\x1B[22m`            | `bold = null`          | Bold off                 |
| `\x1B[23m`            | `italic = null`        | Italic off               |
| `\x1B[24m`            | `underlined = null`    | Underline off            |
| `\x1B[28m`            | `obfuscated = null`    | Obfuscated off           |
| `\x1B[29m`            | `strikethrough = null` | Strikethrough off        |
| `\x1B[30m`-`\x1B[37m` | `color`                | Standard foreground      |
| `\x1B[90m`-`\x1B[97m` | `color`                | Bright foreground        |
| `\x1B[38;5;Nm`        | `color` (RGB)          | 256-color palette        |
| `\x1B[38;2;R;G;Bm`    | `color` (RGB)          | 24-bit RGB color         |
| `\x1B[39m`            | `color = null`         | Default foreground color |
| `\x1B[0m`             | reset all              | Reset all attributes     |

ANSI styles override the [shared configuration](/docs/helpers/text-renderers#shared-configuration) defaults (`color`, `bold`, `italic`). When no ANSI
style is active, the config values are used as fallback. ANSI has no property of its own beyond the shared ones.

## See also

- [MiniMessage Format Specification](https://docs.advntr.dev/minimessage/format.html) - full tag reference from the
  Adventure docs.
- [MiniMessage Web Viewer](https://webui.advntr.dev/) - interactive playground to preview MiniMessage text.
- [Adventure GitHub Repository](https://github.com/KyoriPowered/adventure) - the library that defines the MiniMessage
  format.
- [Minecraft Wiki - Raw JSON Text](https://minecraft.wiki/w/Raw_JSON_text_format) - the underlying component format
  MiniMessage compiles to.
- [Chat Components](/docs/concepts/chat-components) - The component format all three renderers produce.
