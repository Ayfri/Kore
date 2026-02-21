package io.github.ayfri.kore

import io.github.ayfri.kore.arguments.actions.OpenUrl
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.RGB
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.text.markdownToTextComponents

fun markdownRendererTests() {
	val bold = markdownToTextComponents("**hello**")
	bold.size assertsIs 1
	bold[0].list[0].bold!! assertsIs true
	bold[0].list[0].text assertsIs "hello"

	val italic = markdownToTextComponents("*world*")
	italic[0].list[0].italic!! assertsIs true
	italic[0].list[0].text assertsIs "world"

	val strike = markdownToTextComponents("~~removed~~")
	strike[0].list[0].strikethrough!! assertsIs true
	strike[0].list[0].text assertsIs "removed"

	val underline = markdownToTextComponents("__underlined__")
	underline[0].list[0].underlined!! assertsIs true
	underline[0].list[0].text assertsIs "underlined"

	val obf = markdownToTextComponents("||hidden||")
	obf[0].list[0].obfuscated!! assertsIs true
	obf[0].list[0].text assertsIs "hidden"

	val code = markdownToTextComponents("use `command` here")
	code[0].list.size assertsIs 3
	code[0].list[0].text assertsIs "use "
	code[0].list[1].text assertsIs "command"
	code[0].list[1].color!! assertsIs Color.GRAY
	code[0].list[2].text assertsIs " here"

	val link = markdownToTextComponents("click [here](https://example.com) now")
	link[0].list.size assertsIs 3
	link[0].list[0].text assertsIs "click "
	link[0].list[1].text assertsIs "here"
	link[0].list[1].color!! assertsIs Color.AQUA
	link[0].list[1].underlined!! assertsIs true
	(link[0].list[1].clickEvent is OpenUrl) assertsIs true
	(link[0].list[1].clickEvent as OpenUrl).url assertsIs "https://example.com"
	link[0].list[2].text assertsIs " now"

	val h1 = markdownToTextComponents("# Title")
	h1[0].list[0].text assertsIs "Title"
	h1[0].list[0].bold!! assertsIs true
	h1[0].list[0].color!! assertsIs Color.GOLD

	val h2 = markdownToTextComponents("## Subtitle")
	h2[0].list[0].color!! assertsIs Color.YELLOW

	val h3 = markdownToTextComponents("### Section")
	h3[0].list[0].color!! assertsIs Color.AQUA

	val ul = markdownToTextComponents("- item one")
	ul[0].list.size assertsIs 2
	ul[0].list[0].text assertsIs "• "
	ul[0].list[1].text assertsIs "item one"

	val ol = markdownToTextComponents("1. first item")
	ol[0].list.size assertsIs 2
	ol[0].list[0].text assertsIs "1. "
	ol[0].list[1].text assertsIs "first item"

	val bq = markdownToTextComponents("> quoted text")
	bq[0].list.size assertsIs 2
	bq[0].list[0].text assertsIs "│ "
	bq[0].list[0].color!! assertsIs Color.GRAY
	bq[0].list[1].text assertsIs "quoted text"

	val hr = markdownToTextComponents("---")
	hr[0].list[0].strikethrough!! assertsIs true
	hr[0].list[0].text.length assertsIs 20

	val colored = markdownToTextComponents("§(#ff0000)red text§() normal")
	colored[0].list.size assertsIs 2
	val colorVal = colored[0].list[0].color as RGB
	colorVal.red assertsIs 255
	colorVal.green assertsIs 0
	colorVal.blue assertsIs 0
	colored[0].list[0].text assertsIs "red text"
	colored[0].list[1].text assertsIs " normal"

	val combined = markdownToTextComponents("**bold and *italic* text**")
	combined[0].list[0].bold!! assertsIs true
	combined[0].list[0].text assertsIs "bold and "
	combined[0].list[1].bold!! assertsIs true
	combined[0].list[1].italic!! assertsIs true
	combined[0].list[1].text assertsIs "italic"
	combined[0].list[2].bold!! assertsIs true
	combined[0].list[2].text assertsIs " text"

	val doc = markdownToTextComponents(
		"""
		# Welcome
		Hello **world**!
		- one
		- two
		---
		""".trimIndent()
	)
	doc.size assertsIs 5

	val custom = markdownToTextComponents("hello") {
		color = Color.RED
		this.bold = true
	}
	custom[0].list[0].color!! assertsIs Color.RED
	custom[0].list[0].bold!! assertsIs true

	val empty = markdownToTextComponents("")
	empty.size assertsIs 1

	val plain = markdownToTextComponents("just text")
	plain[0].list[0].text assertsIs "just text"

	// Complete serialized text component checks
	val boldSerialized = markdownToTextComponents("**hello** world")
	boldSerialized[0].asString() assertsIs """[{type:"text",bold:1b,text:"hello"},{type:"text",text:" world"}]"""

	val headingSerialized = markdownToTextComponents("# Title")
	headingSerialized[0].asString() assertsIs """{type:"text",bold:1b,color:"gold",text:"Title"}"""

	val linkSerialized = markdownToTextComponents("Click [here](https://example.com) for **info**")
	linkSerialized[0].asString() assertsIs """[{type:"text",text:"Click "},{type:"text",click_event:{action:"open_url",url:"https://example.com"},color:"aqua",text:"here",underlined:1b},{type:"text",text:" for "},{type:"text",bold:1b,text:"info"}]"""

	val codeSerialized = markdownToTextComponents("Run `help` now")
	codeSerialized[0].asString() assertsIs """[{type:"text",text:"Run "},{type:"text",color:"gray",text:"help"},{type:"text",text:" now"}]"""

	val hrSerialized = markdownToTextComponents("---")
	hrSerialized[0].asString() assertsIs """{type:"text",color:"gray",strikethrough:1b,text:"────────────────────"}"""

	val quoteSerialized = markdownToTextComponents("> hello")
	quoteSerialized[0].asString() assertsIs """[{type:"text",color:"gray",text:"│ "},{type:"text",text:"hello"}]"""

	val ulSerialized = markdownToTextComponents("- item")
	ulSerialized[0].asString() assertsIs """[{type:"text",text:"• "},{type:"text",text:"item"}]"""

	val olSerialized = markdownToTextComponents("1. first")
	olSerialized[0].asString() assertsIs """[{type:"text",text:"1. "},{type:"text",text:"first"}]"""

}
