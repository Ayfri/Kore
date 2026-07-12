package io.github.ayfri.kore.bindings.generation.codegen

/** Renders a Kotlin string literal (escaping backslash, quotes, `$`, and control characters). */
fun kotlinStringLiteral(value: String): String {
	val escaped = buildString {
		for (c in value) {
			when (c) {
				'\\' -> append("\\\\")
				'"' -> append("\\\"")
				'$' -> append("\\$")
				'\n' -> append("\\n")
				'\r' -> append("\\r")
				'\t' -> append("\\t")
				else -> append(c)
			}
		}
	}
	return "\"$escaped\""
}

private fun collectImports(type: KtTypeSpec, selfPackage: String, into: MutableSet<String>) {
	fun add(ref: KtRef) {
		// Same-package symbols (including the file's own nested types) never need an import.
		if (ref.packageName.isNotEmpty() && ref.packageName != selfPackage) into += ref.canonicalName
	}

	type.superinterfaces.forEach(::add)
	type.annotations.forEach { ann ->
		add(ann.type)
		ann.referencedTypes.forEach(::add)
	}
	type.properties.forEach { prop ->
		prop.type?.let(::add)
		prop.referencedTypes.forEach(::add)
	}
	type.functions.forEach { fn ->
		fn.returnType?.let(::add)
	}
	type.nestedTypes.forEach { collectImports(it, selfPackage, into) }
}

private fun renderType(type: KtTypeSpec, sb: StringBuilder, indent: Int) {
	val pad = "\t".repeat(indent)

	type.annotations.forEach { ann ->
		sb.append(pad).append("@").append(ann.type.simpleName)
		if (ann.member.isNotEmpty()) sb.append("(").append(ann.member).append(")")
		sb.append("\n")
	}

	val modifiers = buildList {
		if (KtModifier.SEALED in type.modifiers) add("sealed")
		if (KtModifier.DATA in type.modifiers) add("data")
	}
	val keyword = when (type.kind) {
		KtTypeKind.OBJECT -> "object"
		KtTypeKind.ENUM -> "enum class"
		KtTypeKind.INTERFACE -> "interface"
	}

	sb.append(pad)
	if (modifiers.isNotEmpty()) sb.append(modifiers.joinToString(" ")).append(" ")
	sb.append(keyword).append(" ").append(type.name)

	if (type.superinterfaces.isNotEmpty()) {
		sb.append(" : ").append(type.superinterfaces.joinToString(", ") { it.simpleName })
	}

	val hasBody = type.enumConstants.isNotEmpty() || type.properties.isNotEmpty() ||
		type.functions.isNotEmpty() || type.nestedTypes.isNotEmpty()
	if (!hasBody) {
		sb.append("\n")
		return
	}

	sb.append(" {\n")
	val bodyPad = "\t".repeat(indent + 1)

	if (type.kind == KtTypeKind.ENUM && type.enumConstants.isNotEmpty()) {
		type.enumConstants.forEach { sb.append(bodyPad).append(it).append(",\n") }
		sb.append(bodyPad).append(";\n\n")
	}

	type.properties.forEach { prop ->
		sb.append(bodyPad)
		val mods = buildList {
			if (KtModifier.OVERRIDE in prop.modifiers) add("override")
			if (KtModifier.CONST in prop.modifiers) add("const")
		}
		if (mods.isNotEmpty()) sb.append(mods.joinToString(" ")).append(" ")
		sb.append("val ").append(prop.name)
		prop.type?.let { sb.append(": ").append(it.simpleName) }
		sb.append(" = ").append(prop.initializer).append("\n")
	}
	if (type.properties.isNotEmpty()) sb.append("\n")

	type.functions.forEach { fn ->
		sb.append(bodyPad)
		if (KtModifier.OVERRIDE in fn.modifiers) sb.append("override ")
		sb.append("fun ").append(fn.name).append("()")
		fn.returnType?.let { sb.append(": ").append(it.simpleName) }
		sb.append(" {\n")
		fn.statements.forEach { statement -> sb.append(bodyPad).append("\t").append(statement).append("\n") }
		sb.append(bodyPad).append("}\n")
	}
	if (type.functions.isNotEmpty()) sb.append("\n")

	type.nestedTypes.forEach { nested ->
		renderType(nested, sb, indent + 1)
		sb.append("\n")
	}

	sb.append(pad).append("}\n")
}

/** Renders a [KtFile] model into formatted Kotlin source text. */
fun renderKtFile(file: KtFile): String {
	val imports = mutableSetOf<String>()
	file.types.forEach { collectImports(it, file.packageName, imports) }
	val sortedImports = imports.sorted()

	return buildString {
		file.fileComment?.let { append("// ").append(it).append("\n\n") }
		append("package ").append(file.packageName).append("\n\n")
		if (sortedImports.isNotEmpty()) {
			sortedImports.forEach { append("import ").append(it).append("\n") }
			append("\n")
		}
		file.types.forEach { renderType(it, this, 0) }
	}
}
