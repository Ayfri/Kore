import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File

fun generateFile(
	name: String,
	sourceUrl: String? = null,
	topLevel: TypeSpec.Builder? = null,
	subPackage: String? = null,
	additionalCode: FileSpec.Builder.(name: String) -> Unit = {},
) {
	var path = GENERATED_PACKAGE
	if (subPackage != null) path += ".$subPackage"
	val fileBuilder = FileSpec.builder(path, name).apply {
		val sourceLine = sourceUrl?.let { "\nSource: $it" } ?: ""
		addFileComment(
			"""
			${HEADER.removePrefix("// ")}$sourceLine
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
