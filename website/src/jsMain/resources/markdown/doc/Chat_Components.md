---
root: .components.layouts.MarkdownLayout
title: Chat Components
nav-title: Chat Components
description: A guide for creating Chat Components in a Minecraft datapack using Kore.
keywords: minecraft, datapack, kore, guide, chat-components
date-created: 2024-09-05
date-modified: 2024-09-05
routeOverride: /docs/chat-components
---

# Chat Components

Chat Components are used to create rich text messages in Minecraft. They can include formatting, interactivity, and nested components. Kore
has functions to create and manipulate Chat Components in a datapack.<br>
Note that they always works by groups named `ChatComponents`, whenever you create a chat component, you actually create a `ChatComponents`,
and you can chain multiple components together using the `+` operator.

Minecraft sometimes does not allow "complex" chat components with data resolving (`score`, `nbt` and `entity` chat components), if you use
them, you'll get an empty text component. Simple chat components are inheriting the `SimpleComponent` interface, and you have a
`containsOnlySimpleComponents` property to check if a `ChatComponents` only contains simple components.

You also have a `containsOnlyText()` function to check if a `ChatComponents` only contains plain text components with no formatting.

### Common Properties

- `text` - The text to display.
- `bold` - Whether the text is bold.
- `clickEvent` - The action to perform when the text is clicked.
- `color` - The color of the text.
- `extra` - Additional components to display after this one (prefer using the `+` operator).
- `font` - The font to use.
- `hoverEvent` - The action to perform when the text is hovered over.
- `insertion` - The text to insert into the chat when the text is shift-clicked.
- `italic` - Whether the text is italic.
- `obfuscated` - Whether the text is obfuscated.
- `strikethrough` - Whether the text is strikethrough.
- `underlined` - Whether the text is underlined.

## PlainTextComponent

The `PlainTextComponent` displays simple text with optional formatting such as color and bold.<br>
To create a `PlainTextComponent`, use the `textComponent` function.

### Example

```kotlin
val plainText = textComponent("Hello, world!") {
	color = Color.RED
	bold = true
}
```

In-game output:<br>
![Simple Hello World in bold red](/doc/chat-components/hello-world.png)

See how to set custom colors in the [Colors](./colors) article.

### Combined Components

Components can be combined using the `+` operator, use the `text` function to create a simple text component and not a `ChatComponents`.

```kotlin
val combinedComponents = textComponent("Hello, ") + text("world!") {
	color = Color.RED
	bold = true
}
```

In-game output:<br>
![Combined Hello World](/doc/chat-components/combined-hello-world.png)
> (only the "world!" part is bold and red)

## EntityComponent

The `EntityComponent` displays the name of an entity selected by a selector. If multiple entities are found, their names are displayed in
the form `Name1, Name2` etc.<br>
The `separator` property can be used to change the separator between the names of the entities.<br>
If no entities are found, the component displays nothing.

### Example

```kotlin
val entityComponent = entityComponent(self())
```

In-game example:<br>
![Hello World of the player](/doc/chat-components/entity.png)

## KeybindComponent

The `KeybindComponent` displays a keybind. The keybind is displayed in the player's keybind settings.

### Example

```kotlin
val keybindComponent = keybindComponent("key.sprint")
```

In-game example:<br>
![Keybind Component Example](/doc/chat-components/keybind.png)

## NbtComponent

The `NbtComponent` displays NBT data from a block, an entity, or a storage. The `interpret` property can be used to interpret the NBT data
as a text component, if the parsing fails, nothing is displayed.<br>
The `nbt` property can be used to specify the path to the NBT data.<br>
If `nbt` points to an array, then it will display all the elements joined in the form `Element1, Element2` etc.<br>
The `separator` property can be used to change the separator between the elements of the array.

### Example

```kotlin
val nbtComponent = nbtComponent("Health", entity = nearestEntity {
	type = EntityType.CREEPER
})
```

In-game output:<br>
![NBT Component Example](/doc/chat-components/nbt-health.png)

## ScoreComponent

The `ScoreComponent` displays the score of an entity for a specific objective. The `name` property can be used to specify the name of the
entity whose score to display, it can be a selector or a literal name (will use the player with that name). It can also be `*` to select the
entity seeing the text component.<br>
The `objective` property can be used to specify the name of the objective to display the score of.<br>
A `value` property can be used to specify a fixed value to display regardless of the score.

### Example

```kotlin
val scoreComponent = scoreComponent("test")
```

In-game output:<br>
![Score Component Example](/doc/chat-components/score.png)

## TranslatedTextComponent

The `TranslatedTextComponent` displays translated text using translation keys. You can also pass arguments to the translation key with the
`with` argument, which should be a list of text components or strings.<br>
A `fallback` property can be used to specify a fallback text if the translation key is not found.

### Example

```kotlin
val translatedTextComponent = translatedTextComponent("chat.type.text", "Ayfri", "Hello !")
```

In-game output:<br>
![Translated Text Component Example](/doc/chat-components/translation.png)

## Hover Event

Hover events display extra information when the text is hovered over, it can be either text, an item, or an entity. Use `showText` to
display text, `showItem` to display an item, and `showEntity` to display an entity.<br>
Note that to show an entity, you have to have its UUID as a string.

### Hover Event Example

```kotlin
val hoverEventComponent = textComponent("Hover over me!") {
	hoverEvent {
		showText("Hello, world!")
	}
}
```

In-game output:<br>
![Hover Event Example](/doc/chat-components/hover.png)

### Hover Item Example

```kotlin
val hoverItemComponent = textComponent("Hover over me!") {
	hoverEvent {
		showItem(Items.DIAMOND_SWORD {
			damage(5)
		})
	}
}
```

In-game output:<br>
![Hover Event with an Item Example](/doc/chat-components/hover-item.png)

## Click Event

Click events perform an action when the text is clicked. The action can be to:

- Change the page of the book if reading a book
- Copy some text to the clipboard
- Open a file
- Open a URL
- Run a command
- Suggest a command (insert the command in the chat but don't run it)

### Click Event Example

```kotlin
val clickEventComponent = textComponent("Click me!") {
	clickEvent {
		runCommand {
			say("Hello, world!")
		}
	}
}
```
