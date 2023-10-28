import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File

fun generateFile(
	name: String,
	sourceUrl: String,
	topLevel: TypeSpec.Builder? = null,
	additionalCode: FileSpec.Builder.(name: String) -> Unit = {},
) {
	val fileBuilder = FileSpec.builder(GENERATED_PACKAGE, name).apply {
		addFileComment(
			"""
			${HEADER.removePrefix("// ")}
			Source: $sourceUrl
			Minecraft version : $minecraftVersion
		""".trimIndent()
		)

		topLevel?.let { addType(it.build()) }
		additionalCode(name)
	}

	val packageDir = File(libDir, CODE_FOLDER)
	val fileSpec = fileBuilder.build()
	val file = File(packageDir, fileSpec.toJavaFileObject().toUri().path)

	println("Generating $file")
	fileSpec.writeTo(packageDir)
	println("Generated $file")
}
