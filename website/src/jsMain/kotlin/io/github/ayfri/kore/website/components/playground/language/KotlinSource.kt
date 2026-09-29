package io.github.ayfri.kore.website.components.playground.language

enum class TokenType {
	COMMENT,
	IDENTIFIER,
	NUMBER,
	STRING,
	SYMBOL,
}

class Token(val type: TokenType, val text: String, val start: Int, val end: Int, val line: Int)

/** One step of a chain like `Items.DIAMOND_SWORD.predicate { }`: a name, `this`, or a literal typed as [literal]. */
data class Segment(val name: String, val call: Boolean, val literal: String? = null)

/**
 * A `{` that changes what `this` is: the trailing lambda of a call, `function("x") { }` or `Items.STONE { }`, or the body
 * of an extension function. A call's `argument` is the first argument of `with(x) { }`.
 */
sealed interface Block {
	data class Call(val name: String, val receiver: List<Segment>?, val argument: List<Segment>?) : Block
	data class Body(val receiverType: String) : Block
}

/** The call whose parentheses hold the caret, the argument it is on, and that argument's name when written `name = `. */
class CallSite(val name: String, val receiver: List<Segment>?, val argument: Int, val named: String?)

class Import(val path: String, val alias: String?, val line: Int) {
	val name get() = alias ?: path.substringAfterLast('.')
	val star get() = path.endsWith(".*")
}

/** A name the buffer declares: its declared type as written, or the token ending its initializer's chain. */
class Local(val name: String, val kind: LocalKind, val type: String?, val initializer: Int?, val token: Int)

enum class LocalKind {
	CLASS,
	FUNCTION,
	VALUE,
}

/** Where completion was asked. */
sealed interface Site {
	/** In a comment, a string, or where a new name is being declared. */
	data object None : Site

	/** After `import a.b.`: [path] is `a.b`. */
	data class ImportPath(val path: String) : Site

	/** After `expr.`. */
	data class Member(val receiver: List<Segment>) : Site

	/** After `expr ` on the same line, where an infix call goes: `"lives" lessThan…`. */
	data class Infix(val receiver: List<Segment>) : Site

	/** After `:` or `fun`, where a type goes. */
	data object Type : Site

	data object Scope : Site
}

private val CONTROL_KEYWORDS = setOf("catch", "for", "if", "when", "while")
private val DECLARATION_KEYWORDS = setOf("class", "interface", "object", "package", "typealias", "val", "var")
private val EXPRESSION_KEYWORDS = setOf("false", "null", "super", "this", "true")
private val TRANSPARENT_KEYWORDS = setOf("do", "else", "finally", "get", "init", "set", "try")
private val SYMBOLS = listOf("!!", "!=", "&&", "*=", "++", "+=", "--", "-=", "->", "..", "/=", "::", "<=", "==", ">=", "?.", "?:", "||")

/** Keywords that end an expression like a name does, the others start or join one. */
val KOTLIN_KEYWORDS = setOf(
	"as", "break", "catch", "class", "continue", "do", "else", "false", "finally", "for", "fun", "if", "import", "in", "interface", "is",
	"null", "object", "package", "return", "super", "this", "throw", "true", "try", "typealias", "val", "var", "when", "while",
)

/**
 * A lexical view of the editor buffer, enough to tell the DSL block the caret is in, the chain before a dot, the call
 * being typed and the imports, without parsing Kotlin: strings (templates included) and comments are single tokens, and
 * every bracket knows its partner.
 */
class KotlinSource(val text: String) {
	val tokens = tokenize(text)

	/** [tokens] without comments, what every index below points into. */
	val code = tokens.filter { it.type != TokenType.COMMENT }
	private val partners = IntArray(code.size) { -1 }.also { partners ->
		val open = ArrayDeque<Int>()
		code.forEachIndexed { index, token ->
			when (token.text) {
				"(", "[", "{" -> open.addLast(index)
				")", "]", "}" -> {
					val opener = when (token.text) {
						")" -> "("
						"]" -> "["
						else -> "{"
					}

					// A stray closer pops nothing; an unclosed opener in between (a half-typed call) is dropped.
					val match = open.indexOfLast { code[it].text == opener }
					if (match >= 0) {
						partners[open[match]] = index
						partners[index] = open[match]
						while (open.size > match) open.removeLast()
					}
				}
			}
		}
	}

	val imports = code.indices.filter { code[it].text == "import" && (it == 0 || code[it - 1].line < code[it].line) }.map { start ->
		var index = start + 1
		val path = StringBuilder()
		while (index < code.size && code[index].line == code[start].line && (code[index].type == TokenType.IDENTIFIER || code[index].text == "." || code[index].text == "*")) {
			if (code[index].text == "as") break
			path.append(code[index].text)
			index++
		}

		val alias = code.getOrNull(index + 1)?.takeIf { code.getOrNull(index)?.text == "as" }?.text
		Import(path.toString(), alias, code[start].line)
	}

	/** Imported names to their path, star imports aside. */
	val importedNames = imports.filter { !it.star }.associate { it.name to it.path }

	val locals = collectLocals()

	fun isImported(path: String) = imports.any { it.path == path || (it.star && it.path.removeSuffix(".*") == path.substringBeforeLast('.')) }

	/** Index in [code] of the last token ending at or before [offset]. */
	fun before(offset: Int): Int {
		var low = 0
		var high = code.size - 1
		var found = -1
		while (low <= high) {
			val middle = (low + high) / 2
			if (code[middle].end <= offset) {
				found = middle
				low = middle + 1
			} else high = middle - 1
		}
		return found
	}

	/** Whether [offset] sits inside a comment or a string, where nothing is completed. */
	fun inCommentOrString(offset: Int) = tokens.any { token ->
		when (token.type) {
			TokenType.COMMENT -> offset > token.start && (offset < token.end || (offset == token.end && token.text.startsWith("//")))
			TokenType.STRING -> offset > token.start && offset < token.end
			else -> false
		}
	}

	/** The site of a completion whose typed prefix starts at [offset]. */
	fun siteAt(offset: Int): Site {
		if (inCommentOrString(offset)) return Site.None
		val previous = before(offset)
		val token = code.getOrNull(previous) ?: return Site.Scope
		val sameLine = token.line == 1 + (0 until offset).count { text[it] == '\n' }

		val statementStart = (previous downTo 0).first { it == 0 || code[it - 1].line < code[it].line }
		if (sameLine && code[statementStart].text == "import") {
			val path = code.subList(statementStart + 1, previous + 1).joinToString("") { it.text }
			return Site.ImportPath(path.substringBeforeLast('.', ""))
		}

		// In `"lives" lessThan 0` the name after the literal is the infix function, what follows it is its argument.
		val infixName = token.type == TokenType.IDENTIFIER && previous > 0 && endsExpression(previous - 1) && code[previous - 1].line == token.line

		return when {
			token.text == "." || token.text == "?." -> chainEndingAt(previous - 1)?.let(Site::Member) ?: Site.None
			token.text == "::" || token.text in DECLARATION_KEYWORDS -> Site.None
			token.text == ":" || token.text == "fun" -> Site.Type
			sameLine && endsExpression(previous) && !infixName -> chainEndingAt(previous)?.let(Site::Infix) ?: Site.Scope
			else -> Site.Scope
		}
	}

	/** The blocks around [offset] that change `this`, outermost first. */
	fun blocksAt(offset: Int): List<Block> = openersBefore(offset).filter { code[it].text == "{" }.mapNotNull(::blockOf)

	/** The call whose argument list holds [offset], if the innermost bracket around it is that list. */
	fun callAt(offset: Int): CallSite? {
		val opener = openersBefore(offset).lastOrNull()?.takeIf { code[it].text == "(" } ?: return null
		val callee = skipTypeArguments(opener - 1)
		val name = code.getOrNull(callee)?.takeIf { it.type == TokenType.IDENTIFIER && it.text !in KOTLIN_KEYWORDS } ?: return null

		// `fun Receiver.name(` and `class Name(` declare parameters, they don't pass arguments.
		val head = (callee - 1 downTo 0).firstOrNull {
			(code[it].type != TokenType.IDENTIFIER || code[it].text in KOTLIN_KEYWORDS) && code[it].text !in setOf(".", "<", ">", ",", "?")
		}
		if (head != null && code[head].text in DECLARATION_KEYWORDS + "fun") return null

		var argument = 0
		var argumentStart = opener + 1
		var index = opener + 1
		while (index < code.size && code[index].end <= offset) {
			when {
				code[index].text == "," -> {
					argument++
					argumentStart = index + 1
				}

				partners[index] > index -> index = partners[index]
			}
			index++
		}

		val named = code.getOrNull(argumentStart)?.takeIf { it.type == TokenType.IDENTIFIER && code.getOrNull(argumentStart + 1)?.text == "=" }?.text
		return CallSite(name.text, receiverOf(callee), argument, named)
	}

	/** The chain whose last token is `code[end]`: `Items.DIAMOND_SWORD.predicate { }` from its closing brace. */
	fun chainEndingAt(end: Int): List<Segment>? {
		val segments = mutableListOf<Segment>()
		var index = end

		while (index >= 0) {
			var call = false
			if (code[index].text == "!!") index--
			if (index >= 0 && code[index].text == "}") {
				index = partners[index] - 1
				call = true
			}
			if (index >= 0 && code[index].text == ")") {
				index = partners[index] - 1
				call = true
			}
			if (index < 0) return null
			index = skipTypeArguments(index)

			val token = code.getOrNull(index) ?: return null
			segments += when (token.type) {
				TokenType.IDENTIFIER -> Segment(token.text, call)
				TokenType.NUMBER -> Segment(token.text, false, numberType(token.text))
				TokenType.STRING -> Segment(token.text, false, "kotlin.String")
				else -> return null
			}

			if (index >= 2 && (code[index - 1].text == "." || code[index - 1].text == "?.")) index -= 2 else break
		}

		return segments.reversed()
	}

	/** Index of the last token of the chain starting at `code[start]`, the initializer of `val x = <chain>`. */
	fun chainEnd(start: Int): Int? {
		var index = start
		while (index < code.size) {
			if (code[index].type == TokenType.SYMBOL) return null
			index++
			if (code.getOrNull(index)?.text == "<") typeArgumentsEnd(index)?.let { index = it + 1 }

			while (index < code.size && (code[index].text == "(" || code[index].text == "!!" || (code[index].text == "{" && code[index].line == code[index - 1].line))) {
				index = if (code[index].text == "!!") index + 1 else partners[index].takeIf { it > index }?.plus(1) ?: return null
			}

			if (code.getOrNull(index)?.text == "." || code.getOrNull(index)?.text == "?.") index++ else return index - 1
		}

		return null
	}

	/** Whether `code[index]` can end an expression: `if (x)` can't, its parentheses hold a condition. */
	private fun endsExpression(index: Int) = code[index].let { token ->
		when (token.type) {
			TokenType.IDENTIFIER -> token.text !in KOTLIN_KEYWORDS || token.text in EXPRESSION_KEYWORDS
			TokenType.NUMBER, TokenType.STRING -> true
			else -> token.text == "]" || token.text == ")" && code.getOrNull(partners[index] - 1)?.text !in CONTROL_KEYWORDS
		}
	}

	private fun openersBefore(offset: Int) = (0..before(offset)).filter { code[it].text in setOf("(", "[", "{") && (partners[it] < 0 || code[partners[it]].start >= offset) }

	private fun receiverOf(callee: Int) = if (callee >= 2 && (code[callee - 1].text == "." || code[callee - 1].text == "?.")) chainEndingAt(callee - 2) else null

	private fun blockOf(brace: Int): Block? {
		var index = brace - 1

		// `fun f(): Type {` puts the return type between the parameters and the brace.
		val colon = (index downTo maxOf(0, index - 6)).firstOrNull { code[it].text == ":" }
		if (colon != null && code.getOrNull(colon - 1)?.text == ")" && (colon + 1..index).all { code[it].type == TokenType.IDENTIFIER || code[it].text in setOf(".", "<", ">", "?", ",") }) {
			index = colon - 1
		}

		val token = code.getOrNull(index) ?: return null
		if (token.text == ")") index = partners[index] - 1
		index = skipTypeArguments(index)

		val callee = code.getOrNull(index)?.takeIf { it.type == TokenType.IDENTIFIER } ?: return null
		if (callee.text in CONTROL_KEYWORDS || callee.text in TRANSPARENT_KEYWORDS || callee.text in KOTLIN_KEYWORDS) return null

		// `fun Receiver.name(…) {` or `fun name(…): Type {`: the body's `this` is the declared receiver, if any.
		val declaration = (index - 1 downTo maxOf(0, index - 4)).firstOrNull { code[it].text == "fun" }
		if (declaration != null && token.text == ")") {
			val receiver = code.subList(declaration + 1, index).lastOrNull { it.type == TokenType.IDENTIFIER }?.text ?: return null
			return Block.Body(receiver)
		}

		val argument = if (callee.text == "with" && token.text == ")") chainEndingAt(brace - 2) else null
		return Block.Call(callee.text, receiverOf(index), argument)
	}

	private fun skipTypeArguments(index: Int): Int {
		if (code.getOrNull(index)?.text != ">") return index
		var depth = 0
		for (cursor in index downTo 0) {
			when (code[cursor].text) {
				">" -> depth++
				"<" -> if (--depth == 0) return cursor - 1
				"(", "{", ";", "=" -> return index
			}
		}
		return index
	}

	/** Index of the `>` closing the type arguments opened at `code[index]`, `null` when that `<` is a comparison. */
	private fun typeArgumentsEnd(index: Int): Int? {
		var depth = 0
		for (cursor in index until code.size) {
			when (code[cursor].text) {
				"<" -> depth++
				">" -> if (--depth == 0) return cursor
				"(", "{", ";", "=" -> return null
			}
		}
		return null
	}

	private fun collectLocals(): List<Local> = buildList {
		code.forEachIndexed { index, token ->
			when (token.text) {
				"val", "var" -> {
					val name = code.getOrNull(index + 1)?.takeIf { it.type == TokenType.IDENTIFIER } ?: return@forEachIndexed
					// `val Foo.bar` declares an extension property, not a local named `Foo`.
					if (code.getOrNull(index + 2)?.text == ".") return@forEachIndexed

					val type = code.getOrNull(index + 3)?.takeIf { code[index + 2].text == ":" && it.type == TokenType.IDENTIFIER }?.text
					val equals = (index + 2 until minOf(code.size, index + 8)).firstOrNull { code[it].text == "=" }
					add(Local(name.text, LocalKind.VALUE, type, equals?.let { chainEnd(it + 1) }, index + 1))
				}

				"fun" -> {
					val parameters = (index + 1 until minOf(code.size, index + 12)).firstOrNull { code[it].text == "(" } ?: return@forEachIndexed
					code.getOrNull(parameters - 1)?.takeIf { it.type == TokenType.IDENTIFIER }?.let { add(Local(it.text, LocalKind.FUNCTION, null, null, parameters - 1)) }

					// `name: Type` pairs of the parameter list, at its own depth only.
					var cursor = parameters + 1
					while (cursor < partners[parameters]) {
						if (code[cursor].type == TokenType.IDENTIFIER && code[cursor + 1].text == ":" && code[cursor - 1].text in setOf("(", ",")) {
							add(Local(code[cursor].text, LocalKind.VALUE, code.getOrNull(cursor + 2)?.text, null, cursor))
						}
						cursor = if (partners[cursor] > cursor) partners[cursor] + 1 else cursor + 1
					}
				}

				"class", "interface", "object" -> code.getOrNull(index + 1)?.takeIf { it.type == TokenType.IDENTIFIER }?.let {
					add(Local(it.text, LocalKind.CLASS, null, null, index + 1))
				}

				"for" -> code.getOrNull(index + 2)?.takeIf { code[index + 1].text == "(" && it.type == TokenType.IDENTIFIER }?.let {
					add(Local(it.text, LocalKind.VALUE, null, null, index + 2))
				}

				"{" -> {
					// `{ a, b -> }` names the lambda's parameters.
					val arrow = (index + 1 until minOf(code.size, index + 12)).firstOrNull { code[it].text == "->" || code[it].text == "{" || code[it].text == "}" }
					if (arrow != null && code[arrow].text == "->") {
						(index + 1 until arrow).filter { code[it].type == TokenType.IDENTIFIER && code[it - 1].text in setOf("{", ",") }
							.forEach { add(Local(code[it].text, LocalKind.VALUE, null, null, it)) }
					}
				}
			}
		}
	}
}

private fun numberType(text: String) = when {
	text.endsWith('L') -> "kotlin.Long"
	text.endsWith('f') || text.endsWith('F') -> "kotlin.Float"
	'.' in text || 'e' in text && !text.startsWith("0x") -> "kotlin.Double"
	else -> "kotlin.Int"
}

private fun tokenize(text: String): List<Token> {
	val tokens = mutableListOf<Token>()
	var index = 0
	var line = 1

	fun add(type: TokenType, end: Int) {
		val value = text.substring(index, end)
		tokens += Token(type, value, index, end, line)
		line += value.count { it == '\n' }
		index = end
	}

	while (index < text.length) {
		val char = text[index]
		when {
			char == '\n' -> {
				line++
				index++
			}

			char.isWhitespace() -> index++
			text.startsWith("//", index) -> add(TokenType.COMMENT, text.indexOf('\n', index).takeIf { it >= 0 } ?: text.length)
			text.startsWith("/*", index) -> add(TokenType.COMMENT, text.blockCommentEnd(index))
			char == '"' -> add(TokenType.STRING, text.stringEnd(index))
			char == '\'' -> add(TokenType.STRING, text.charEnd(index))
			char == '`' -> add(TokenType.IDENTIFIER, (text.indexOf('`', index + 1).takeIf { it >= 0 } ?: (text.length - 1)) + 1)
			char.isLetter() || char == '_' -> add(TokenType.IDENTIFIER, (index until text.length).firstOrNull { !text[it].isLetterOrDigit() && text[it] != '_' } ?: text.length)
			char.isDigit() -> {
				var end = index
				while (end < text.length && (text[end].isLetterOrDigit() || text[end] == '_' || (text[end] == '.' && text.getOrNull(end + 1)?.isDigit() == true))) end++
				add(TokenType.NUMBER, end)
			}

			else -> add(TokenType.SYMBOL, index + (SYMBOLS.firstOrNull { text.startsWith(it, index) }?.length ?: 1))
		}
	}

	return tokens
}

/** Kotlin block comments nest. */
private fun String.blockCommentEnd(start: Int): Int {
	var depth = 0
	var index = start
	while (index < length) {
		when {
			startsWith("/*", index) -> {
				depth++
				index += 2
			}

			startsWith("*/", index) -> {
				index += 2
				if (--depth == 0) return index
			}

			else -> index++
		}
	}
	return length
}

/** Index right after the string literal opening at [start], escapes and `${…}` templates (strings inside them too) included. */
private fun String.stringEnd(start: Int): Int {
	val raw = startsWith("\"\"\"", start)
	var index = start + if (raw) 3 else 1

	while (index < length) {
		when {
			raw && startsWith("\"\"\"", index) -> {
				while (getOrNull(index) == '"') index++
				return index
			}

			!raw && this[index] == '"' -> return index + 1
			!raw && this[index] == '\\' -> index += 2
			!raw && this[index] == '\n' -> return index
			startsWith($$"${", index) -> index = templateEnd(index + 2)
			else -> index++
		}
	}

	return length
}

private fun String.templateEnd(start: Int): Int {
	var depth = 1
	var index = start
	while (index < length) {
		when (this[index]) {
			'{' -> depth++
			'}' -> if (--depth == 0) return index + 1
			'"' -> {
				index = stringEnd(index)
				continue
			}
		}
		index++
	}
	return length
}

private fun String.charEnd(start: Int): Int {
	var index = start + 1
	while (index < length && this[index] != '\'' && this[index] != '\n') index += if (this[index] == '\\') 2 else 1
	return minOf(length, index + 1)
}
