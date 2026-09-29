package io.github.ayfri.kore.website.playground.api

import org.jetbrains.kotlin.CoreEnvironmentDeprecation
import org.jetbrains.kotlin.cli.extensionsStorage
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.kdoc.psi.impl.KDocTag
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import java.io.File

/** Defaults longer than this read as noise in a completion popup, they show as `…`. */
private const val MAX_DEFAULT_LENGTH = 32

/** A parameter's default value source text and its KDoc. */
class SourceParameter(val default: String?, val doc: String?)

/** What only the sources know about a declaration: its KDoc as Markdown, its parameters' defaults and docs, where it lives. */
class SourceEntry(
	val doc: String?,
	val parameters: Map<String, SourceParameter>,
	val deprecated: Boolean,
	val source: String?,
)

/**
 * The key metadata and sources agree on: owner, receiver simple name, name, and parameter names for callables.
 *
 * `io.github.ayfri.kore.commands/Function.tellraw(targets,message)` for a function, no parentheses for a property.
 */
fun declarationKey(owner: String, receiver: String?, name: String, parameters: List<String>?) =
	"$owner/${receiver.orEmpty()}.$name" + (parameters?.joinToString(",", "(", ")") ?: "")

/**
 * Parses the common sources with the compiler's PSI, no resolution, keeping every public declaration by [declarationKey]
 * (by fully qualified name for classes and type aliases). A declaration the metadata has and the sources lack is
 * jvmMain-only or synthesized, which is how the index leaves those out.
 */
@OptIn(CompilerConfiguration.Internals::class, CoreEnvironmentDeprecation::class, ExperimentalCompilerApi::class)
class SourceIndex(repository: File, roots: List<String>) {
	private val entries = HashMap<String, SourceEntry>()

	init {
		val disposable = Disposer.newDisposable()
		val configuration = CompilerConfiguration().apply { extensionsStorage = CompilerPluginRegistrar.ExtensionStorage() }
		val environment = KotlinCoreEnvironment.createForProduction(disposable, configuration, EnvironmentConfigFiles.JVM_CONFIG_FILES)
		val factory = KtPsiFactory(environment.project, false)

		roots.map { File(repository, it) }.forEach { root ->
			root.walkTopDown().filter { it.extension == "kt" }.forEach { file ->
				val path = file.relativeTo(repository).invariantSeparatorsPath
				// The generated registries are gitignored, so there is no source to link them to.
				val link = path.takeUnless { "/generated/" in it }
				val ktFile = factory.createFile(file.name, file.readText().replace("\r\n", "\n"))
				ktFile.declarations.forEach { visit(it, ktFile.packageFqName.asString(), ktFile.text, link) }
			}
		}

		Disposer.dispose(disposable)
	}

	operator fun get(key: String) = entries[key]

	private fun visit(declaration: KtDeclaration, owner: String, text: String, link: String?) {
		if (declaration is KtEnumEntry || declaration.isHidden()) return
		val kdoc = declaration.docComment?.let(::KDocParts)

		fun put(key: String, parameters: List<KtParameter> = emptyList(), doc: String? = kdoc?.text) {
			val line = text.take(declaration.textOffset).count { it == '\n' } + 1
			entries[key] = SourceEntry(
				doc = doc,
				parameters = parameters.associate { it.name.orEmpty() to it.toApi(kdoc) },
				deprecated = declaration.annotationEntries.any { it.shortName?.asString() == "Deprecated" },
				source = link?.let { "$it#L$line" },
			)
		}

		when (declaration) {
			is KtClassOrObject -> {
				val fqName = declaration.fqName?.asString() ?: return
				put(fqName)

				val constructors = listOfNotNull(declaration.primaryConstructor) + declaration.secondaryConstructors
				constructors.filterNot { it.isHidden() }.forEach { put(declarationKey(fqName, null, "<init>", it.valueParameters.map { p -> p.name.orEmpty() }), it.valueParameters) }
				if (declaration is KtClass && constructors.isEmpty()) put(declarationKey(fqName, null, "<init>", emptyList()))

				declaration.primaryConstructorParameters.filter { it.hasValOrVar() && !it.isHidden() }.forEach {
					put(declarationKey(fqName, null, it.name.orEmpty(), null), doc = kdoc?.properties?.get(it.name) ?: kdoc?.parameters?.get(it.name))
				}

				declaration.declarations.forEach { visit(it, fqName, text, link) }
			}

			is KtNamedFunction -> put(
				declarationKey(owner, declaration.receiverTypeReference?.simpleName(), declaration.name ?: return, declaration.valueParameters.map { it.name.orEmpty() }),
				declaration.valueParameters,
			)

			is KtProperty -> put(declarationKey(owner, declaration.receiverTypeReference?.simpleName(), declaration.name ?: return, null))
			is KtTypeAlias -> put("$owner.${declaration.name}")
			else -> Unit
		}
	}
}

private fun KtModifierListOwner.isHidden() =
	hasModifier(KtTokens.PRIVATE_KEYWORD) || hasModifier(KtTokens.INTERNAL_KEYWORD) || hasModifier(KtTokens.PROTECTED_KEYWORD)

/** `Scores<SelectorScore>?` and `io.github.ayfri.kore.functions.Function` become `Scores` and `Function`, as metadata names them. */
private fun KtTypeReference.simpleName() = text.replace(Regex("<.*>"), "").trim('(', ')', '?', ' ').substringAfterLast('.')

private fun KtParameter.toApi(kdoc: KDocParts?) = SourceParameter(
	default = defaultValue?.text?.replace(Regex("\\s+"), " ")?.let { if (it.length > MAX_DEFAULT_LENGTH) "…" else it },
	doc = kdoc?.parameters?.get(name) ?: kdoc?.properties?.get(name),
)

/** A KDoc as Markdown: the description followed by its return, throws, see and since tags, and the per-parameter and per-property descriptions. */
private class KDocParts(kdoc: KDoc) {
	val parameters = mutableMapOf<String, String>()
	val properties = mutableMapOf<String, String>()
	val text: String?

	init {
		val tags = mutableListOf<String>()

		PsiTreeUtil.findChildrenOfType(kdoc, KDocTag::class.java).forEach { tag ->
			val subject = tag.getSubjectName()
			val content = tag.getContent().markdown()

			when (tag.name) {
				"param" -> subject?.let { parameters[it] = content }
				"property" -> subject?.let { properties[it] = content }
				"return" -> tags += "**Returns** $content"
				"see" -> tags += "**See** ${subject?.let { "`$it` " }.orEmpty()}$content"
				"since" -> tags += "**Since** $content"
				"throws", "exception" -> tags += "**Throws** `$subject` $content"
			}
		}

		text = (listOf(kdoc.getDefaultSection().getContent().markdown()) + tags).filter { it.isNotBlank() }.joinToString("\n\n").ifBlank { null }
	}
}

/** `[Foo]` becomes `` `Foo` ``, `[text][Foo]` becomes `text`, Markdown links stay, and an inline `@see` starts its own paragraph. */
private fun String.markdown() = trim()
	.replace(Regex("""\[([^\]]+)]\[[^\]]+]"""), "$1")
	.replace(Regex("""\[([^\[\]]+)](?![(\[])"""), "`$1`")
	.replace(Regex("""\s@see\s+"""), "\n\n**See** ")
