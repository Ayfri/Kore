package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.arguments.actions.CopyToClipboard
import io.github.ayfri.kore.arguments.actions.OpenUrl
import io.github.ayfri.kore.arguments.actions.RunCommand
import io.github.ayfri.kore.arguments.actions.SuggestCommand
import io.github.ayfri.kore.arguments.chatcomponents.*
import io.github.ayfri.kore.arguments.chatcomponents.hover.HoverAction
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.RGB
import io.github.ayfri.kore.helpers.assertions.assertsIs
import io.github.ayfri.kore.helpers.assertions.assertsThrows
import io.github.ayfri.kore.helpers.text.TagResolver
import io.github.ayfri.kore.helpers.text.miniMessageToTextComponents
import io.kotest.core.spec.style.FunSpec

fun miniMessageRendererTests() {
    // Plain text
    val plain = miniMessageToTextComponents("Hello world")
    plain.list.size assertsIs 1
    plain.list[0].text assertsIs "Hello world"

    // Named colors
    val red = miniMessageToTextComponents("<red>Hello</red> world")
    red.list.size assertsIs 2
    red.list[0].text assertsIs "Hello"
    red.list[0].color!! assertsIs Color.RED
    red.list[1].text assertsIs " world"

    // Hex colors
    val hex = miniMessageToTextComponents("<#ff0000>Red text")
    hex.list.size assertsIs 1
    val hexColor = hex.list[0].color as RGB
    hexColor.red assertsIs 255
    hexColor.green assertsIs 0
    hexColor.blue assertsIs 0

    // Color alias
    val colorAlias = miniMessageToTextComponents("<color:blue>Blue text</color>")
    colorAlias.list.size assertsIs 1
    colorAlias.list[0].color!! assertsIs Color.BLUE

    val cAlias = miniMessageToTextComponents("<c:#00ff00>Green</c>")
    cAlias.list.size assertsIs 1
    val cColor = cAlias.list[0].color as RGB
    cColor.green assertsIs 255

    // Bold
    val bold = miniMessageToTextComponents("<bold>Bold text</bold>")
    bold.list.size assertsIs 1
    bold.list[0].bold!! assertsIs true
    bold.list[0].text assertsIs "Bold text"

    // Bold shorthand
    val boldB = miniMessageToTextComponents("<b>Bold</b>")
    boldB.list[0].bold!! assertsIs true

    // Italic
    val italic = miniMessageToTextComponents("<italic>Italic</italic>")
    italic.list[0].italic!! assertsIs true

    val italicI = miniMessageToTextComponents("<i>Italic</i>")
    italicI.list[0].italic!! assertsIs true

    val italicEm = miniMessageToTextComponents("<em>Italic</em>")
    italicEm.list[0].italic!! assertsIs true

    // Underlined
    val underlined = miniMessageToTextComponents("<underlined>Under</underlined>")
    underlined.list[0].underlined!! assertsIs true

    val underlinedU = miniMessageToTextComponents("<u>Under</u>")
    underlinedU.list[0].underlined!! assertsIs true

    // Strikethrough
    val strike = miniMessageToTextComponents("<strikethrough>Strike</strikethrough>")
    strike.list[0].strikethrough!! assertsIs true

    val strikeSt = miniMessageToTextComponents("<st>Strike</st>")
    strikeSt.list[0].strikethrough!! assertsIs true

    // Obfuscated
    val obf = miniMessageToTextComponents("<obfuscated>Hidden</obfuscated>")
    obf.list[0].obfuscated!! assertsIs true

    val obfShort = miniMessageToTextComponents("<obf>Hidden</obf>")
    obfShort.list[0].obfuscated!! assertsIs true

    // Nested decorations
    val nested = miniMessageToTextComponents("<bold><red>Bold Red</red></bold>")
    nested.list.size assertsIs 1
    nested.list[0].bold!! assertsIs true
    nested.list[0].color!! assertsIs Color.RED
    nested.list[0].text assertsIs "Bold Red"

    // Reset
    val reset = miniMessageToTextComponents("<bold><red>Styled<reset>Normal")
    reset.list.size assertsIs 2
    reset.list[0].bold!! assertsIs true
    reset.list[0].color!! assertsIs Color.RED
    reset.list[1].text assertsIs "Normal"

    // Click events
    val clickUrl = miniMessageToTextComponents("<click:open_url:'https://ayfri.com'>Click me</click>")
    clickUrl.list[0].text assertsIs "Click me"
    (clickUrl.list[0].clickEvent is OpenUrl) assertsIs true
    (clickUrl.list[0].clickEvent as OpenUrl).url assertsIs "https://ayfri.com"

    val clickRun = miniMessageToTextComponents("<click:run_command:/spawn>Spawn</click>")
    (clickRun.list[0].clickEvent is RunCommand) assertsIs true

    val clickSuggest = miniMessageToTextComponents("<click:suggest_command:/msg >Message</click>")
    (clickSuggest.list[0].clickEvent is SuggestCommand) assertsIs true

    val clickCopy = miniMessageToTextComponents("<click:copy_to_clipboard:Copied!>Copy</click>")
    (clickCopy.list[0].clickEvent is CopyToClipboard) assertsIs true

    // Hover events
    val hoverText = miniMessageToTextComponents("<hover:show_text:'Hello hover'>Hover me</hover>")
    hoverText.list[0].text assertsIs "Hover me"
    hoverText.list[0].hoverEvent!!.action assertsIs HoverAction.SHOW_TEXT

    // Font
    val font = miniMessageToTextComponents("<font:minecraft:uniform>Uniform text</font>")
    font.list[0].font!! assertsIs "minecraft:uniform"
    font.list[0].text assertsIs "Uniform text"

    // Insertion
    val insert = miniMessageToTextComponents("<insert:hello>Shift click me</insert>")
    insert.list[0].insertion!! assertsIs "hello"

    // Newline
    val newline = miniMessageToTextComponents("Line1<newline>Line2")
    newline.list.size assertsIs 3
    newline.list[0].text assertsIs "Line1"
    newline.list[1].text assertsIs "\n"
    newline.list[2].text assertsIs "Line2"

    val br = miniMessageToTextComponents("A<br>B")
    br.list.size assertsIs 3
    br.list[1].text assertsIs "\n"

    // Translatable
    val lang = miniMessageToTextComponents("<lang:chat.type.text>")
    lang.list.size assertsIs 1
    (lang.list[0] is TranslatedTextComponent) assertsIs true
    (lang.list[0] as TranslatedTextComponent).translate assertsIs "chat.type.text"

    // Translatable with args
    val langArgs = miniMessageToTextComponents("<lang:chat.type.text:Player:Message>")
    val translatable = langArgs.list[0] as TranslatedTextComponent
    translatable.translate assertsIs "chat.type.text"
    translatable.with!!.size assertsIs 2

    // Keybind
    val key = miniMessageToTextComponents("<key:key.forward>")
    key.list.size assertsIs 1
    (key.list[0] is KeybindComponent) assertsIs true
    (key.list[0] as KeybindComponent).keybind assertsIs "key.forward"

    // Selector
    val selector = miniMessageToTextComponents("<selector:@e[type=pig,limit=1]>")
    selector.list.size assertsIs 1
    (selector.list[0] is EntityComponent) assertsIs true
    (selector.list[0] as EntityComponent).selector assertsIs "@e[type=pig,limit=1]"

    // Score
    val score = miniMessageToTextComponents("<score:player_name:objective_name>")
    score.list.size assertsIs 1
    (score.list[0] is ScoreComponent) assertsIs true
    (score.list[0] as ScoreComponent).score.entity assertsIs "player_name"
    (score.list[0] as ScoreComponent).score.objective assertsIs "objective_name"

    // NBT block
    val nbtBlock = miniMessageToTextComponents("<nbt:block:0,64,0:Items[0].id>")
    nbtBlock.list.size assertsIs 1
    val nbtComp = nbtBlock.list[0] as NbtComponent
    nbtComp.nbt assertsIs "Items[0].id"
    nbtComp.block!! assertsIs "0,64,0"
    nbtComp.source!! assertsIs NbtComponentSource.BLOCK

    // NBT entity
    val nbtEntity = miniMessageToTextComponents("<nbt:entity:@s:Health>")
    val nbtEntComp = nbtEntity.list[0] as NbtComponent
    nbtEntComp.entity!! assertsIs "@s"
    nbtEntComp.source!! assertsIs NbtComponentSource.ENTITY

    // NBT storage
    val nbtStorage = miniMessageToTextComponents("<nbt:storage:my_namespace\\:my_id:my.path>")
    val nbtStoreComp = nbtStorage.list[0] as NbtComponent
    nbtStoreComp.source!! assertsIs NbtComponentSource.STORAGE

    // Escape
    val escape = miniMessageToTextComponents("\\<red>Not red")
    escape.list.size assertsIs 1
    escape.list[0].text assertsIs "<red>Not red"

    // Pre
    val pre = miniMessageToTextComponents("<pre><red>raw tags</red></pre>")
    pre.list.size assertsIs 1
    pre.list[0].text assertsIs "<red>raw tags</red>"

    // Gradient (basic support: applies first color)
    val gradient = miniMessageToTextComponents("<gradient:red:blue>Gradient text</gradient>")
    gradient.list.size assertsIs 1
    gradient.list[0].color!! assertsIs Color.RED

    // Rainbow (basic support: style tag)
    val rainbow = miniMessageToTextComponents("<rainbow>Rainbow text</rainbow>")
    rainbow.list.size assertsIs 1
    rainbow.list[0].text assertsIs "Rainbow text"

    // Tag resolvers
    val resolved = miniMessageToTextComponents("<player_name> joined") {
        tagResolvers = mapOf(
            "player_name" to TagResolver { text("Ayfri") { color = Color.GOLD } }
        )
    }
    resolved.list.size assertsIs 2
    resolved.list[0].text assertsIs "Ayfri"
    resolved.list[0].color!! assertsIs Color.GOLD
    resolved.list[1].text assertsIs " joined"

    // Strict mode - unclosed tags
    assertsThrows("Unclosed tags: bold") {
        miniMessageToTextComponents("<bold>Unclosed") { strict = true }
    }

    // Strict mode - unknown tags
    assertsThrows("Unknown tag: <unknown>") {
        miniMessageToTextComponents("<unknown>text") { strict = true }
    }

    // Strict mode - unmatched closing
    assertsThrows("Closing tag </bold> has no matching opening tag.") {
        miniMessageToTextComponents("</bold>text") { strict = true }
    }

    // Empty input
    val empty = miniMessageToTextComponents("")
    empty.list.size assertsIs 1

    // Config defaults applied
    val configured = miniMessageToTextComponents("styled") {
        this.color = Color.AQUA
        this.bold = true
        this.font = "minecraft:alt"
    }
    configured.list[0].color!! assertsIs Color.AQUA
    configured.list[0].bold!! assertsIs true
    configured.list[0].font!! assertsIs "minecraft:alt"

    // Complex mixed example
    val complex = miniMessageToTextComponents("<bold><red>Hello</red> <underlined>World</underlined></bold>")
    complex.list.size assertsIs 3
    complex.list[0].bold!! assertsIs true
    complex.list[0].color!! assertsIs Color.RED
    complex.list[0].text assertsIs "Hello"
    complex.list[1].bold!! assertsIs true
    complex.list[1].text assertsIs " "
    complex.list[2].bold!! assertsIs true
    complex.list[2].underlined!! assertsIs true
    complex.list[2].text assertsIs "World"

    // Serialized output checks
    val simpleSerialized = miniMessageToTextComponents("<red>Hello")
    simpleSerialized.asString() assertsIs """{type:"text",color:"red",text:"Hello"}"""

    val multiSerialized = miniMessageToTextComponents("<bold>Hello</bold> world")
    multiSerialized.asString() assertsIs """[{type:"text",bold:1b,text:"Hello"},{type:"text",text:" world"}]"""
}

class MiniMessageRendererTests : FunSpec({
    test("mini message renderer") {
        miniMessageRendererTests()
    }
})
