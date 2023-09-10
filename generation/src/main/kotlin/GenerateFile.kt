import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File

fun generateFile(
	name: String,
	sourceUrl: String,
	topLevel: TypeSpec.Builder,
	additionalCode: FileSpec.Builder.(enumName: String) -> Unit = {},
) {
	val fileBuilder = FileSpec.builder(GENERATED_PACKAGE, name).apply {
		addFileComment(
			"""
			${HEADER.removePrefix("// ")}
			Source: $sourceUrl
			Minecraft version : $minecraftVersion
		""".trimIndent()
		)

		addType(topLevel.build())
		additionalCode(name)
	}

	val packageDir = File(libDir, CODE_FOLDER)
	val fileSpec = fileBuilder.build()
	val file = File(packageDir, fileSpec.toJavaFileObject().toUri().path)

	println("Generating $file")
	fileSpec.writeTo(packageDir)
	println("Generated $file")
}
