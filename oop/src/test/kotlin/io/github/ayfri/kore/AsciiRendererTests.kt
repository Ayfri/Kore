package io.github.ayfri.kore

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.text.ansiToTextComponents

fun asciiRendererTests() {
	ansiToTextComponents("Hello world")[0].asString() assertsIs """
		"Hello world"
	""".trimIndent()

	ansiToTextComponents("")[0].asString() assertsIs """
		""
	""".trimIndent()

	ansiToTextComponents("\u001B[1mBold text")[0].asString() assertsIs """
		{type:"text",bold:1b,text:"Bold text"}
	""".trimIndent()

	ansiToTextComponents("\u001B[1mBold text\u001B[0m")[0].asString() assertsIs """
		{type:"text",bold:1b,text:"Bold text"}
	""".trimIndent()

	ansiToTextComponents("\u001B[3mItalic text")[0].asString() assertsIs """
		{type:"text",italic:1b,text:"Italic text"}
	""".trimIndent()

	ansiToTextComponents("\u001B[4mUnderlined")[0].asString() assertsIs """
		{type:"text",text:"Underlined",underlined:1b}
	""".trimIndent()

	ansiToTextComponents("\u001B[9mStruck")[0].asString() assertsIs """
		{type:"text",strikethrough:1b,text:"Struck"}
	""".trimIndent()

	ansiToTextComponents("\u001B[8mHidden")[0].asString() assertsIs """
		{type:"text",obfuscated:1b,text:"Hidden"}
	""".trimIndent()

	ansiToTextComponents("\u001B[31mRed text")[0].asString() assertsIs """
		{type:"text",color:"red",text:"Red text"}
	""".trimIndent()

	ansiToTextComponents("\u001B[32mGreen\u001B[0m normal")[0].asString() assertsIs """
		[{type:"text",color:"green",text:"Green"},{type:"text",text:" normal"}]
	""".trimIndent()

	ansiToTextComponents("\u001B[93mBright yellow")[0].asString() assertsIs """
		{type:"text",color:"yellow",text:"Bright yellow"}
	""".trimIndent()

	ansiToTextComponents("\u001B[90mDark gray")[0].asString() assertsIs """
		{type:"text",color:"dark_gray",text:"Dark gray"}
	""".trimIndent()

	ansiToTextComponents("\u001B[1;31mBold red\u001B[0m rest")[0].asString() assertsIs """
		[{type:"text",bold:1b,color:"red",text:"Bold red"},{type:"text",text:" rest"}]
	""".trimIndent()

	ansiToTextComponents("\u001B[4;9mUnder+Strike")[0].asString() assertsIs """
		{type:"text",strikethrough:1b,text:"Under+Strike",underlined:1b}
	""".trimIndent()

	ansiToTextComponents("\u001B[1;3;31mAll styled")[0].asString() assertsIs """
		{type:"text",bold:1b,color:"red",italic:1b,text:"All styled"}
	""".trimIndent()

	ansiToTextComponents("\u001B[1mBold\u001B[22m not bold")[0].asString() assertsIs """
		[{type:"text",bold:1b,text:"Bold"},{type:"text",text:" not bold"}]
	""".trimIndent()

	ansiToTextComponents("\u001B[38;2;128;64;255mRGB text")[0].asString() assertsIs """
		{type:"text",color:"#8040ff",text:"RGB text"}
	""".trimIndent()

	ansiToTextComponents("\u001B[38;5;196mPalette red")[0].asString() assertsIs """
		{type:"text",color:"#ff0000",text:"Palette red"}
	""".trimIndent()

	ansiToTextComponents("\u001B[31mRed\n\u001B[34mBlue").let { lines ->
		lines.size assertsIs 2
		lines[0].asString() assertsIs """{type:"text",color:"red",text:"Red"}"""
		lines[1].asString() assertsIs """{type:"text",color:"blue",text:"Blue"}"""
	}

	ansiToTextComponents("plain") {
		color = Color.GOLD
		bold = true
	}[0].asString() assertsIs """
		{type:"text",bold:1b,color:"gold",text:"plain"}
	""".trimIndent()

	ansiToTextComponents("\u001B[32mGreen\u001B[0m rest") {
		color = Color.WHITE
	}[0].asString() assertsIs """
		[{type:"text",color:"green",text:"Green"},{type:"text",color:"white",text:" rest"}]
	""".trimIndent()

	ansiToTextComponents("\u001B[31mA\u001B[32mB\u001B[34mC")[0].asString() assertsIs """
		[{type:"text",color:"red",text:"A"},{type:"text",color:"green",text:"B"},{type:"text",color:"blue",text:"C"}]
	""".trimIndent()

	ansiToTextComponents("\u001B[38;5;232mDarkest gray")[0].asString() assertsIs """
		{type:"text",color:"#080808",text:"Darkest gray"}
	""".trimIndent()

	ansiToTextComponents("\u001B[38;5;255mLightest gray")[0].asString() assertsIs """
		{type:"text",color:"#eeeeee",text:"Lightest gray"}
	""".trimIndent()

	ansiToTextComponents("\u001B[31mRed\u001B[39m default")[0].asString() assertsIs """
		[{type:"text",color:"red",text:"Red"},{type:"text",text:" default"}]
	""".trimIndent()

	ansiToTextComponents("No escape sequences at all")[0].asString() assertsIs """
		"No escape sequences at all"
	""".trimIndent()

	ansiToTextComponents("\u001B[mReset shorthand")[0].asString() assertsIs """
		"Reset shorthand"
	""".trimIndent()
}
