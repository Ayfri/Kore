package io.github.ayfri.kore.bindings.generation.codegen

/**
 * A reference to an importable Kotlin symbol (a class/interface, or a top-level member such
 * as a function or property). Used both to render a simple name in source text and to collect
 * the file's `import` list.
 */
data class KtRef(val packageName: String, val simpleName: String) {
	val canonicalName get() = if (packageName.isEmpty()) simpleName else "$packageName.$simpleName"
}

enum class KtTypeKind { OBJECT, ENUM, INTERFACE }
enum class KtModifier { DATA, SEALED, CONST, OVERRIDE }

data class KtAnnotationSpec(val type: KtRef, val member: String, val referencedTypes: List<KtRef> = emptyList())

data class KtPropertySpec(
	val name: String,
	val type: KtRef? = null,
	val modifiers: Set<KtModifier> = emptySet(),
	/** Fully-rendered Kotlin initializer expression, e.g. `"\"foo\""` or `"NAMESPACE"`. */
	val initializer: String,
	/** Extra symbols referenced by [initializer] that must be imported. */
	val referencedTypes: List<KtRef> = emptyList(),
)

data class KtFunSpec(
	val name: String,
	val modifiers: Set<KtModifier> = emptySet(),
	val returnType: KtRef? = null,
	/** Fully-rendered Kotlin statement lines, e.g. `listOf("return \"\$NAMESPACE:\${name.lowercase()}\"")`. */
	val statements: List<String> = emptyList(),
)

data class KtTypeSpec(
	val kind: KtTypeKind,
	val name: String,
	val modifiers: Set<KtModifier> = emptySet(),
	val annotations: List<KtAnnotationSpec> = emptyList(),
	val superinterfaces: List<KtRef> = emptyList(),
	val properties: List<KtPropertySpec> = emptyList(),
	val functions: List<KtFunSpec> = emptyList(),
	val enumConstants: List<String> = emptyList(),
	val nestedTypes: List<KtTypeSpec> = emptyList(),
)

data class KtFile(
	val packageName: String,
	val fileComment: String? = null,
	val types: List<KtTypeSpec>,
)
