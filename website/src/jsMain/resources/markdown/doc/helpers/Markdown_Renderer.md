---
root: .components.layouts.MarkdownLayout
title: Markdown Text Renderer
nav-title: Markdown Renderer
description: Convert Markdown-formatted text into Minecraft text components with the Kore helpers module.
keywords: minecraft, datapack, kore, helpers, markdown, text, renderer, bold, italic, link, heading, list
date-created: 2026-03-03
date-modified: 2026-03-31
routeOverride: /docs/helpers/markdown-renderer
position: 9
---

# Markdown Text Renderer

Converts [Markdown](https://commonmark.org/)-formatted text into Minecraft `ChatComponents` for use in
[tellraw](https://minecraft.wiki/w/Commands/tellraw), title commands, or boss bar names.

## Basic usage

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

## Supported inline syntax

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

## Supported block syntax

| Markdown syntax        | Rendering                            |
|------------------------|--------------------------------------|
| `# Heading` – `######` | Bold + heading color (configurable)  |
| `- item` / `* item`    | Unordered list with bullet prefix    |
| `1. item`              | Ordered list with number prefix      |
| `> quote`              | Blockquote with `│ ` prefix          |
| `---` / `***` / `___`  | Horizontal rule (strikethrough line) |

## Not supported (Minecraft limitations)

The following Markdown features are **not supported** because Minecraft text components cannot represent them:

- Images (`![alt](url)`)
- Tables
- Nested blockquotes
- Multi-line code blocks (fenced ` ``` `)
- HTML tags
- Footnotes
- Task lists (`- [ ]` / `- [x]`)
- Reference-style links (`[text][ref]`)

## Examples

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

## Configuration

| Property           | Default       | Description                                  |
|--------------------|---------------|----------------------------------------------|
| `color`            | `null`        | Default text color                           |
| `bold`             | `null`        | Default bold state                           |
| `italic`           | `null`        | Default italic state                         |
| `font`             | `null`        | Minecraft font resource location             |
| `headingColors`    | level 1–6 map | Color per heading level (gold, yellow, etc.) |
| `codeColor`        | `Color.GRAY`  | Color for inline code spans                  |
| `linkColor`        | `Color.AQUA`  | Color for link text                          |
| `linkUnderline`    | `true`        | Whether links are underlined                 |
| `blockquotePrefix` | `"│ "`        | String prepended to blockquote lines         |
| `bulletChar`       | `"• "`        | Bullet string for unordered list items       |
| `hrChar`           | `"─"`         | Character repeated for horizontal rules      |
| `hrLength`         | `20`          | Number of repetitions for horizontal rules   |
