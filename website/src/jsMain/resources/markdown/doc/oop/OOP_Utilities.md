---
root: .components.layouts.MarkdownLayout
title: OOP Utilities
nav-title: OOP Utilities
description: Events, cooldowns, boss bars, entity effects, raycasts, math engine, VFX particles, ANSI renderer, and Markdown renderer for the Kore OOP module.
keywords: minecraft, datapack, kore, oop, events, cooldown, bossbar, effects, raycast, math, trigonometry, vfx, particles, ansi, markdown, text
date-created: 2026-02-21
date-modified: 2026-02-21
routeOverride: /docs/oop/utilities
---

# OOP Utilities

The OOP module provides high-level utilities that generate advancement-based events, scoreboard-based cooldowns,
boss bar management, entity effect helpers, recursive raycasts, a scoreboard math engine,
a geometric particle VFX engine, an ANSI text renderer, and a Markdown text renderer - all from a clean Kotlin DSL.

All generated resource names are centralized in `OopConstants` so they're easy to find and override.

## Events

Events use advancements to detect player/entity actions and dispatch them to function tags. Multiple handlers can be
registered for the same event - they all fire together.

### Player events

```kotlin
val player = player("Steve")

function("my_events") {
	player.onBlockUse { say("Interacted with a block!") }
	player.onConsumeItem { say("Consumed something!") }
	player.onConsumeItem(Items.GOLDEN_APPLE) { say("Golden apple!") }
	player.onHurtEntity { say("Hit!") }
	player.onInventoryChange { say("Inventory changed!") }
	player.onItemUsedOnBlock { say("Used item on block!") }
	player.onKill { say("Kill!") }
	player.onPlaceBlock { say("Placed a block!") }
	player.onRightClick(Items.STICK) { say("Right click with stick!") }
}
```

Available player events (alphabetical):

| Function              | Trigger                               |
|-----------------------|---------------------------------------|
| `onBlockUse`          | Right-click any block                 |
| `onConsumeItem`       | Consume any item (food, potion, etc.) |
| `onConsumeItem(item)` | Consume a specific item               |
| `onHurtEntity`        | Deal damage to an entity              |
| `onInventoryChange`   | Inventory contents change             |
| `onItemUsedOnBlock`   | Use an item on a block                |
| `onKill`              | Kill an entity                        |
| `onPlaceBlock`        | Place a block                         |
| `onRightClick(item)`  | Right-click while holding an item     |

### Entity events

```kotlin
val zombie = Entity().apply { selector.type = EntityTypes.ZOMBIE }

function("mob_events") {
	zombie.onDeath { say("A zombie died!") }
}
```

The death event uses a loot-table trigger: on death the entity drops a hidden item detected by a tick dispatcher that
runs all death handlers then removes the item.

## Cooldowns

Cooldowns use a scoreboard objective that decrements every tick. When the score reaches 0 the cooldown is ready.

### Registering a cooldown

```kotlin
val cd = registerCooldown("attack_cd", 2.seconds)
```

This generates:

- A **load function** that creates the scoreboard objective.
- A **tick function** that decrements the score for all players with score ≥ 1.

### Using a cooldown

```kotlin
function("combat") {
	with(cd) {
		start(player)               // sets the score to the duration
		ifReady(player) {           // runs only when score == 0, then restarts
			say("Attack ready!")
		}
		reset(player)               // forces score to 0
	}
}
```

## Boss Bars

The OOP module wraps Minecraft boss bars into a simple config + handle pattern.

### Registering a boss bar

```kotlin
val bar = registerBossBar("my_bar", name) {
	color = BossBarColor.RED
	max = 200
	style = BossBarStyle.NOTCHED_10
	value = 50
}
```

A load function is generated that creates the bar and applies all initial settings.

### Manipulating a boss bar

```kotlin
function("boss_fight") {
	bar.apply {
		setValue(100)
		setColor(BossBarColor.BLUE)
		setPlayers(player)
		show()
		hide()
		remove()
	}
}
```

## Entity Effects

Extension functions on `Entity` for giving, clearing, and managing mob effects:

```kotlin
function("buff_player") {
	player.giveEffect(Effects.SPEED, duration = 200, amplifier = 1)
	player.giveInfiniteEffect(Effects.NIGHT_VISION, hideParticles = true)
	player.clearEffect(Effects.SPEED)
	player.clearAllEffects()
}
```

| Function             | Description                           |
|----------------------|---------------------------------------|
| `giveEffect`         | Give a timed effect with optional amp |
| `giveInfiniteEffect` | Give an infinite-duration effect      |
| `clearEffect`        | Remove a specific effect              |
| `clearAllEffects`    | Remove all effects                    |
| `effects { ... }`    | Builder block for multiple operations |

## Raycasts

Raycasts generate a set of recursive functions that step forward from the entity's eyes until they hit a block or reach
max distance.

### DSL builder

```kotlin
val ray = raycast {
	name = "my_ray"
	maxDistance = 50
	step = 0.5
	onHitBlock = { say("Hit!") }
	onMaxDistance = { say("Too far!") }
	onStep = { particle(Particles.FLAME, vec3()) }
}

function("use_ray") {
	with(ray) { cast() }
}
```

## Scoreboard Math Engine

The math module provides trigonometric and algebraic functions using scoreboard operations. All values use fixed-point
arithmetic scaled by **1000** to preserve decimal precision inside integer-only scoreboards.

### Registering the math module

```kotlin
val math = registerMath()
```

This generates a **load function** that creates the `kore_math` objective and sets useful constants (`#2`, `#360`,
`#scale`).

### Trigonometric functions

Sine and cosine use a pre-computed 360-entry lookup table (one entry per degree). The input score holds an angle in
degrees; the output receives `value × 1000`.

```kotlin
function("trig_demo") {
	math.apply {
		cos(player, "angle", "cos_result")
		sin(player, "angle", "sin_result")
	}
}
```

### Square root

An iterative Babylonian / Newton approximation (8 iterations by default):

```kotlin
function("sqrt_demo") {
	math.sqrt(player, "input_val", "sqrt_result")
}
```

### Euclidean distance (squared)

Computes `(x2−x1)² + (y2−y1)² + (z2−z1)²`:

```kotlin
function("distance_demo") {
	math.distanceSquared(player, "x1", "y1", "z1", "x2", "y2", "z2", "dist_sq")
}
```

### Parabolic trajectory

Computes `Y = v0 × t − (g × t²) / 2` for projectile simulation:

```kotlin
function("parabola_demo") {
	math.parabola(player, "time", "#v0", "#gravity", "para_y")
}
```

| Function          | Description                                  |
|-------------------|----------------------------------------------|
| `cos`             | Cosine lookup (degrees → scaled result)      |
| `sin`             | Sine lookup (degrees → scaled result)        |
| `sqrt`            | Integer square root (Newton's method)        |
| `distanceSquared` | Squared 3D Euclidean distance                |
| `parabola`        | Parabolic Y from time, velocity, and gravity |

## Geometric Particle VFX Engine

The VFX engine generates `particle` commands for geometric shapes. Each shape is emitted as a generated function
containing pre-computed positions.

### Drawing shapes

```kotlin
drawCircle("fire_ring", Particles.FLAME, radius = 5.0, points = 16)

drawShape("soul_helix") {
	shape = Shape.HELIX
	particle = Particles.SOUL_FIRE_FLAME
	radius = 2.0
	points = 40
	height = 5.0
	turns = 4
}
```

### Available shapes

| Shape    | Description                                      |
|----------|--------------------------------------------------|
| `CIRCLE` | Flat circle on the XZ plane                      |
| `LINE`   | Straight line along a direction vector           |
| `SPHERE` | Fibonacci-distributed points on a sphere surface |
| `SPIRAL` | Expanding spiral that rises along Y              |
| `HELIX`  | Fixed-radius helix that rises along Y            |

### VfxShape properties

| Property   | Default | Used by                       |
|------------|---------|-------------------------------|
| `particle` | -       | All shapes                    |
| `radius`   | `1.0`   | CIRCLE, SPHERE, SPIRAL, HELIX |
| `points`   | `20`    | All shapes                    |
| `height`   | `3.0`   | SPIRAL, HELIX                 |
| `length`   | `5.0`   | LINE                          |
| `dx/dy/dz` | `1,0,0` | LINE direction                |
| `turns`    | `3`     | SPIRAL, HELIX                 |

## ANSI Text Renderer

Converts text containing ANSI SGR escape sequences into Minecraft `ChatComponents` for use in `tellraw`, title
commands, or boss bar names. ANSI codes are stripped from the output and mapped to Minecraft text component styles.

### Basic usage

```kotlin
val lines = ansiToTextComponents("\u001B[1;31mBold red\u001B[0m normal text")
// Produces: [{bold:true, color:"red", text:"Bold red"}, {text:" normal text"}]
```

Each input line becomes a `ChatComponents` instance. Consecutive characters sharing the same style are merged into
runs for efficiency.

### With config defaults

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
| `\x1B[30m`–`\x1B[37m` | `color`                | Standard foreground      |
| `\x1B[90m`–`\x1B[97m` | `color`                | Bright foreground        |
| `\x1B[38;5;Nm`        | `color` (RGB)          | 256-color palette        |
| `\x1B[38;2;R;G;Bm`    | `color` (RGB)          | 24-bit RGB color         |
| `\x1B[39m`            | `color = null`         | Default foreground color |
| `\x1B[0m`             | reset all              | Reset all attributes     |

ANSI styles override the config defaults (`color`, `bold`, `italic`). When no ANSI style is active, the config
values are used as fallback.

### Configuration

| Property | Default | Description                      |
|----------|---------|----------------------------------|
| `color`  | `null`  | Fallback text color              |
| `bold`   | `null`  | Fallback bold styling            |
| `italic` | `null`  | Fallback italic styling          |
| `font`   | `null`  | Minecraft font resource location |

## Markdown Text Renderer

Converts Markdown-formatted text into Minecraft `ChatComponents` for use in `tellraw`, title commands, or boss bar
names.

### Basic usage

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
| `# Heading` – `######` | Bold + heading color (configurable)  |
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

### Examples

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

### Configuration

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
