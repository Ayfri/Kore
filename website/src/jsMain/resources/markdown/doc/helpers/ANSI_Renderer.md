---
root: .components.layouts.MarkdownLayout
title: ANSI Text Renderer
nav-title: ANSI Renderer
description: Convert ANSI SGR escape sequences into Minecraft text components with the Kore helpers module.
keywords: minecraft, datapack, kore, helpers, ansi, sgr, escape, text, renderer, color, bold, italic
date-created: 2026-03-03
date-modified: 2026-03-31
routeOverride: /docs/helpers/ansi-renderer
position: 1
---

# ANSI Text Renderer

Converts text
containing [ANSI SGR escape sequences](https://en.wikipedia.org/wiki/ANSI_escape_code#SGR_(Select_Graphic_Rendition)_parameters)
into Minecraft `ChatComponents` for use in [tellraw](https://minecraft.wiki/w/Commands/tellraw), title
commands, or boss bar names. ANSI codes are stripped from the output and mapped to Minecraft text component styles.

## Basic usage

```kotlin
val lines = ansiToTextComponents("\u001B[1;31mBold red\u001B[0m normal text")
// Produces: [{bold:true, color:"red", text:"Bold red"}, {text:" normal text"}]
```

Each input line becomes a `ChatComponents` instance. Consecutive characters sharing the same style are merged into
runs for efficiency.

## With config defaults

```kotlin
val lines = ansiToTextComponents("\u001B[32mGreen\u001B[0m rest") {
	color = Color.WHITE
}
// "Green" gets green from ANSI, "rest" gets white from config fallback
```

## Supported ANSI SGR codes

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
| `\x1B[30m`–`\x1B[37m` | `color`                | Standard foreground      |
| `\x1B[90m`–`\x1B[97m` | `color`                | Bright foreground        |
| `\x1B[38;5;Nm`        | `color` (RGB)          | 256-color palette        |
| `\x1B[38;2;R;G;Bm`    | `color` (RGB)          | 24-bit RGB color         |
| `\x1B[39m`            | `color = null`         | Default foreground color |
| `\x1B[0m`             | reset all              | Reset all attributes     |

ANSI styles override the config defaults (`color`, `bold`, `italic`). When no ANSI style is active, the config
values are used as fallback.

## Configuration

| Property | Default | Description                      |
|----------|---------|----------------------------------|
| `color`  | `null`  | Fallback text color              |
| `bold`   | `null`  | Fallback bold styling            |
| `italic` | `null`  | Fallback italic styling          |
| `font`   | `null`  | Minecraft font resource location |
