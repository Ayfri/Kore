import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File

/** Writes one Kotlin file under `kore/src/commonMain/kotlin/io/github/ayfri/kore/generated`, returning its path. Every generator in this module funnels through here; logging is left to callers. */
fun generateFile(
	name: String,
	sourceUrl: String? = null,
	topLevel: TypeSpec.Builder? = null,
	subPackage: String? = null,
	additionalCode: FileSpec.Builder.(name: String) -> Unit = {},
): File {
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
	fileSpec.writeTo(packageDir)
	return file
}

/** Uniform one-line log for a generated artifact: `Generated <kind> <name> (<detail>) -> <file>`, [detail]/[file] omitted when null. */
fun logGenerated(kind: String, name: String, detail: String? = null, file: File? = null) {
	val detailPart = detail?.let { " ($it)" }.orEmpty()
	val filePart = file?.let { " -> $it" }.orEmpty()
	println("Generated $kind $name$detailPart$filePart")
}
