import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File

fun generateFile(
	name: String,
	sourceUrl: String,
	topLevel: TypeSpec.Builder,
	additionalCode: FileSpec.Builder.(enumName: String) -> Unit = {}
) {
	val fileBuilder = FileSpec.builder("generated", name).apply {
		addFileComment(
			"""
			${header.removePrefix("// ")}
			Source: $sourceUrl
			Minecraft version : $minecraftVersion
		""".trimIndent()
		)

		addType(topLevel.build())
		additionalCode(name)
	}

	val packageDir = File(libDir, "src/main/kotlin")
	val file = File(packageDir, "${fileBuilder.packageName}/${fileBuilder.name}.kt")

	println("Generating ${file.canonicalFile}")
	fileBuilder.build().writeTo(packageDir)
	println("Generated ${file.canonicalFile}")
}
