package generators

import GENERATED_PACKAGE
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import generateFile
import getFromCacheOrDownloadTxt
import overrides
import setSealed
import snakeCase
import url

suspend fun downloadGamerules() {
	val url = url("custom-generated/commands/syntaxes/gamerule.txt")
	val gamerules = getFromCacheOrDownloadTxt("gamerules.txt", url).lines()

	generateGamerulesEnums(gamerules, url)
}

private const val INTERFACE_NAME = "Gamerules"
private val gamerulesClass = ClassName(GENERATED_PACKAGE, INTERFACE_NAME)

private fun String.toGameruleName() = substringAfter("gamerule").substringBefore("<").snakeCase().trim().uppercase()

private fun addGameruleChild(name: String) = TypeSpec.interfaceBuilder(name).apply {
	addAnnotation(
		AnnotationSpec.builder(ClassName("kotlinx.serialization", "Serializable"))
			.addMember("with = Serializer::class")
			.build()
	)

	addSuperinterface(gamerulesClass)
	setSealed()
}.build()

private fun addGamerule(name: String, parent: String) = TypeSpec.objectBuilder(name)
	.addModifiers(KModifier.DATA)
	.addSuperinterface(ClassName(GENERATED_PACKAGE, INTERFACE_NAME, parent))
	.build()

fun generateGamerulesEnums(gamerules: List<String>, sourceUrl: String) {
	val booleanGamerules = gamerules.filter { it.endsWith("<value: bool>") }.map(String::toGameruleName)
	val integerGamerules = gamerules.filter { it.endsWith("<value: integer>") }.map(String::toGameruleName)

	val topLevelInterface = TypeSpec.interfaceBuilder(INTERFACE_NAME).apply {
		addAnnotation(
			AnnotationSpec.builder(ClassName("kotlinx.serialization", "Serializable"))
				.addMember("with = $INTERFACE_NAME.Companion.Serializer::class")
				.build()
		)

		setSealed()

		addProperty(
			PropertySpec.builder("name", String::class)
				.getter(FunSpec.getterBuilder().addStatement("return toString()").build())
				.build()
		)

		addType(addGameruleChild("Int"))
		addType(addGameruleChild("Boolean"))

		booleanGamerules.forEach { addType(addGamerule(it, "Boolean")) }
		integerGamerules.forEach { addType(addGamerule(it, "Int")) }

		addType(
			TypeSpec.companionObjectBuilder().apply {
				addProperty(
					PropertySpec.builder("values", List::class.asClassName().parameterizedBy(gamerulesClass))
						.getter(
							FunSpec.getterBuilder()
								.addStatement(
									"return %T::class.sealedSubclasses.map { it.sealedSubclasses.map { it.objectInstance!! } }.flatten()",
									gamerulesClass
								)
								.build()
						)
						.build()
				)

				addFunction(
					FunSpec.builder("fromString")
						.addParameter("name", String::class)
						.returns(gamerulesClass.copy(nullable = true))
						.addStatement("return values.firstOrNull { it.name.equals(name, ignoreCase = true) }")
						.build()
				)

				addFunction(
					FunSpec.builder("camelCase")
						.receiver(String::class)
						.returns(String::class)
						.addStatement("val words = lowercase().split(\"_\")")
						.addStatement("return words[0] + words.drop(1).joinToString(\"\") { word -> word.replaceFirstChar { it.titlecase(Locale.ENGLISH) } }")
						.build()
				)

				addType(
					TypeSpec.classBuilder("Serializer").apply {
						addSuperinterface(
							ClassName("kotlinx.serialization", "KSerializer").parameterizedBy(gamerulesClass)
						)

						addProperty(
							PropertySpec.builder("descriptor", ClassName("kotlinx.serialization.descriptors", "SerialDescriptor"))
								.overrides()
								.getter(
									FunSpec.getterBuilder()
										.addStatement(
											"return %T(%S, %M)",
											ClassName("kotlinx.serialization.descriptors", "PrimitiveSerialDescriptor"),
											INTERFACE_NAME,
											ClassName("kotlinx.serialization.descriptors", "PrimitiveKind").member("STRING")
										)
										.build()
								)
								.build()
						)

						addFunction(
							FunSpec.builder("deserialize")
								.addParameter("decoder", ClassName("kotlinx.serialization.encoding", "Decoder"))
								.returns(gamerulesClass)
								.addStatement(
									"return fromString(decoder.decodeString()) ?: throw %T(%S)",
									IllegalArgumentException::class,
									"Unknown '\${name}' gamerule"
								)
								.overrides()
								.build()
						)

						addFunction(
							FunSpec.builder("serialize")
								.addParameter("encoder", ClassName("kotlinx.serialization.encoding", "Encoder"))
								.addParameter("value", gamerulesClass)
								.addStatement("encoder.encodeString(value.name.camelCase())")
								.overrides()
								.build()
						)
					}.build()
				)
			}.build()
		)
	}

	generateFile(INTERFACE_NAME, sourceUrl, topLevelInterface) {
		addImport("java.util", "Locale")
		addAnnotation(
			AnnotationSpec.builder(Suppress::class)
				.addMember("%S", "ClassName")
				.addMember("%S", "SERIALIZER_TYPE_INCOMPATIBLE")
				.build()
		)
	}
}
