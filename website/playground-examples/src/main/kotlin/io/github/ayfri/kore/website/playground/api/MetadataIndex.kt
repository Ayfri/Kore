package io.github.ayfri.kore.website.playground.api

import kotlin.metadata.*
import kotlin.metadata.jvm.KotlinClassMetadata
import kotlin.metadata.jvm.annotations
import java.io.File
import java.util.zip.ZipFile

private val FUNCTION_TYPE = Regex("kotlin/Function\\d+")

/** Renders metadata types the way a signature reads them, and resolves them to the class the editor matches receivers on. */
private class Types(typeParameters: List<KmTypeParameter>) {
	private val parameters = typeParameters.associateBy { it.id }

	/** A type parameter stands for its first bound, so `fun <T : Argument> T.foo()` is found on any `Argument`. */
	fun fqn(type: KmType): String = when (val classifier = type.classifier) {
		is KmClassifier.Class -> classifier.name.replace('/', '.')
		is KmClassifier.TypeAlias -> classifier.name.replace('/', '.')
		is KmClassifier.TypeParameter -> parameters[classifier.id]?.upperBounds?.firstOrNull()?.let(::fqn) ?: "kotlin.Any"
	}

	/** The name the sources use, the type alias when there is one, for [declarationKey]. */
	fun simpleName(type: KmType) = when (val classifier = (type.abbreviatedType ?: type).classifier) {
		is KmClassifier.Class -> classifier.name.substringAfterLast('/').substringAfterLast('.')
		is KmClassifier.TypeAlias -> classifier.name.substringAfterLast('/').substringAfterLast('.')
		is KmClassifier.TypeParameter -> parameters[classifier.id]?.name ?: "T"
	}

	/** What `this` is inside a trailing `T.() -> R` block. */
	fun lambdaReceiver(type: KmType) = type.takeIf { it.isExtensionFunction() }?.arguments?.firstOrNull()?.type?.let(::fqn)

	fun render(type: KmType): String {
		val shown = type.abbreviatedType ?: type
		val arguments = shown.arguments.map { projection ->
			projection.type?.let { argument ->
				when (projection.variance) {
					KmVariance.IN -> "in "
					KmVariance.OUT -> "out "
					else -> ""
				} + render(argument)
			} ?: "*"
		}

		val text = when {
			shown.isExtensionFunction() -> "${arguments.first()}.(${arguments.drop(1).dropLast(1).joinToString()}) -> ${arguments.last()}"
			shown.isFunction() -> "(${arguments.dropLast(1).joinToString()}) -> ${arguments.last()}"
			else -> simpleName(shown).let { name ->
				val qualified = (shown.classifier as? KmClassifier.Class)?.name?.substringAfterLast('/') ?: name
				qualified + if (arguments.isEmpty()) "" else arguments.joinToString(prefix = "<", postfix = ">")
			}
		}

		return when {
			!shown.isNullable -> text
			shown.isFunction() -> "($text)?"
			else -> "$text?"
		}
	}

	fun declare(typeParameters: List<KmTypeParameter>) = typeParameters.takeIf { it.isNotEmpty() }?.joinToString(prefix = "<", postfix = ">") { parameter ->
		(if (parameter.isReified) "reified " else "") + parameter.name + (parameter.upperBounds.firstOrNull()?.let { " : ${render(it)}" } ?: "")
	}

	private fun KmType.isFunction() = (classifier as? KmClassifier.Class)?.name?.matches(FUNCTION_TYPE) == true
	private fun KmType.isExtensionFunction() = isFunction() && annotations.any { it.className == "kotlin/ExtensionFunctionType" }
}

/**
 * Reads the Kotlin metadata of every class of [libraries], which are on this program's classpath, into
 * [ApiDeclaration]s, keeping only what [sources] also declares publicly in common code.
 */
@OptIn(ExperimentalContextParameters::class)
class MetadataIndex(libraries: List<File>, private val sources: SourceIndex) {
	val declarations = mutableListOf<ApiDeclaration>()

	init {
		libraries.flatMap(::classNames).forEach { name ->
			val metadata = runCatching { Class.forName(name, false, javaClass.classLoader).getAnnotation(Metadata::class.java) }.getOrNull()
				?: return@forEach

			when (val read = KotlinClassMetadata.readLenient(metadata)) {
				is KotlinClassMetadata.Class -> addClass(read.kmClass)
				is KotlinClassMetadata.FileFacade -> addPackage(read.kmPackage, name.substringBeforeLast('.'))
				is KotlinClassMetadata.MultiFileClassPart -> addPackage(read.kmPackage, name.substringBeforeLast('.'))
				else -> Unit
			}
		}
	}

	private fun classNames(library: File) = when {
		library.isDirectory -> library.walkTopDown().map { it.relativeTo(library).invariantSeparatorsPath }.toList()
		else -> ZipFile(library).use { zip -> zip.entries().asSequence().map { it.name }.toList() }
	}.filter { it.endsWith(".class") && !it.startsWith("META-INF") && it != "module-info.class" }.map { it.removeSuffix(".class").replace('/', '.') }

	private fun addPackage(kmPackage: KmPackage, owner: String) {
		kmPackage.functions.forEach { addFunction(it, owner, member = false, emptyList()) }
		kmPackage.properties.forEach { addProperty(it, owner, member = false, emptyList()) }
		kmPackage.typeAliases.filter { it.visibility == Visibility.PUBLIC }.forEach { alias ->
			val source = sources["$owner.${alias.name}"] ?: return@forEach
			val types = Types(alias.typeParameters)
			declarations += ApiDeclaration(
				name = alias.name,
				kind = "typealias",
				owner = owner,
				returns = types.fqn(alias.expandedType),
				type = types.render(alias.underlyingType),
				typeParameters = types.declare(alias.typeParameters),
				doc = source.doc,
				deprecated = source.deprecated,
				source = source.source,
			)
		}
	}

	private fun addClass(kmClass: KmClass) {
		val fqn = kmClass.name.replace('/', '.')
		val kind = when (kmClass.kind) {
			ClassKind.ANNOTATION_CLASS -> "annotation"
			ClassKind.CLASS -> "class"
			ClassKind.COMPANION_OBJECT, ClassKind.OBJECT -> "object"
			ClassKind.ENUM_CLASS -> "enum"
			ClassKind.INTERFACE -> "interface"
			else -> return
		}

		if (kmClass.visibility != Visibility.PUBLIC) return
		val source = sources[fqn] ?: return
		val types = Types(kmClass.typeParameters)
		val instantiable = kind == "class" && kmClass.modality != Modality.ABSTRACT && kmClass.modality != Modality.SEALED

		declarations += ApiDeclaration(
			name = fqn.substringAfterLast('.'),
			kind = kind,
			owner = fqn.substringBeforeLast('.'),
			typeParameters = types.declare(kmClass.typeParameters),
			modifiers = listOfNotNull(
				"abstract".takeIf { kmClass.modality == Modality.ABSTRACT && kind == "class" },
				"companion".takeIf { kmClass.kind == ClassKind.COMPANION_OBJECT },
				"data".takeIf { kmClass.isData },
				"sealed".takeIf { kmClass.modality == Modality.SEALED },
				"value".takeIf { kmClass.isValue },
			),
			constructors = kmClass.constructors.filter { instantiable && it.visibility == Visibility.PUBLIC }.mapNotNull { constructor ->
				val names = constructor.valueParameters.map { it.name }
				sources[declarationKey(fqn, null, "<init>", names)]?.let { parameters(constructor.valueParameters, types, it) }
			},
			supertypes = kmClass.supertypes.map(types::fqn).filter { it != "kotlin.Any" },
			entries = kmClass.kmEnumEntries.map { it.name },
			// `@Serializable` adds a companion to every class, only one the sources declare holds anything to complete.
			companion = kmClass.companionObject?.let { "$fqn.$it" }?.takeIf { sources[it] != null },
			doc = source.doc,
			deprecated = source.deprecated,
			source = source.source,
		)

		kmClass.functions.forEach { addFunction(it, fqn, member = true, kmClass.typeParameters) }
		kmClass.properties.forEach { addProperty(it, fqn, member = true, kmClass.typeParameters) }
	}

	private fun addFunction(function: KmFunction, owner: String, member: Boolean, outer: List<KmTypeParameter>) {
		if (function.visibility != Visibility.PUBLIC || function.kind != MemberKind.DECLARATION) return
		val types = Types(outer + function.typeParameters)
		val receiver = function.receiverParameterType
		val source = sources[declarationKey(owner, receiver?.let(types::simpleName), function.name, function.valueParameters.map { it.name })] ?: return

		declarations += ApiDeclaration(
			name = function.name,
			kind = "function",
			owner = owner,
			member = member,
			receiver = receiver?.let(types::fqn),
			receiverType = receiver?.let(types::render),
			context = function.contextParameters.map { types.fqn(it.type) },
			returns = types.fqn(function.returnType),
			type = types.render(function.returnType),
			lambda = function.valueParameters.lastOrNull()?.type?.let(types::lambdaReceiver),
			typeParameters = types.declare(function.typeParameters),
			modifiers = listOfNotNull(
				"infix".takeIf { function.isInfix },
				"inline".takeIf { function.isInline },
				"operator".takeIf { function.isOperator },
				"suspend".takeIf { function.isSuspend },
			),
			params = parameters(function.valueParameters, types, source),
			doc = source.doc,
			deprecated = source.deprecated,
			source = source.source,
		)
	}

	private fun addProperty(property: KmProperty, owner: String, member: Boolean, outer: List<KmTypeParameter>) {
		if (property.visibility != Visibility.PUBLIC || property.kind != MemberKind.DECLARATION) return
		val types = Types(outer + property.typeParameters)
		val receiver = property.receiverParameterType
		val source = sources[declarationKey(owner, receiver?.let(types::simpleName), property.name, null)] ?: return

		declarations += ApiDeclaration(
			name = property.name,
			kind = "property",
			owner = owner,
			member = member,
			receiver = receiver?.let(types::fqn),
			receiverType = receiver?.let(types::render),
			context = property.contextParameters.map { types.fqn(it.type) },
			returns = types.fqn(property.returnType),
			type = types.render(property.returnType),
			typeParameters = types.declare(property.typeParameters),
			modifiers = listOfNotNull("const".takeIf { property.isConst }, "var".takeIf { property.isVar }),
			doc = source.doc,
			deprecated = source.deprecated,
			source = source.source,
		)
	}

	private fun parameters(parameters: List<KmValueParameter>, types: Types, source: SourceEntry) = parameters.map { parameter ->
		val known = source.parameters[parameter.name]
		ApiParameter(
			name = parameter.name,
			type = types.render(parameter.varargElementType ?: parameter.type),
			default = known?.default?.takeIf { parameter.declaresDefaultValue },
			doc = known?.doc,
			vararg = parameter.varargElementType != null,
		)
	}
}
