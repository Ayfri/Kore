package generators

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MAP
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import generateFile

/** [Generator.fileName] is a download-list identifier, not always identical to the real datapack folder name. */
private val FOLDER_NAME_OVERRIDES = mapOf("dimensions" to "dimension")

/**
 * A few `*Argument` types aren't produced by [launchAllSimpleGenerators]'s simple `fileName.txt`-list
 * pattern (they come from [launchArgumentTypeGenerators]/`downloadItemComponentTypes` or are hand-written
 * in `kore`), so their datapack folder can't be derived from a [Generator] entry. Kept to the minimum
 * necessary rather than falling back to a full hand-maintained list.
 */
private val MANUAL_ADDITIONS = mapOf(
	"item_modifier" to "ItemModifierArgument",
	"predicate" to "PredicateArgument",
)

/**
 * Emits `DATAPACK_FOLDER_TO_ARGUMENT`: a map from datapack folder name (e.g. `"enchantment"`,
 * `"worldgen/biome"`, `"tags/block"`) to the simple name of the `*Argument` interface it maps to.
 * Derived directly from the same [Generator] definitions used to generate the `*Argument` types
 * themselves, so it never drifts out of sync with the current Minecraft version - no separate
 * hand-maintained list, no runtime network fetch needed by consumers (e.g. `bindings`).
 */
fun generateDatapackFolderRegistry(allGenerators: List<Generator>) {
	val mapping = sortedMapOf<String, String>()
	mapping += MANUAL_ADDITIONS

	for (generator in allGenerators) {
		val rawFolder = generator.fileName.removeSuffix(".txt")
		val folder = FOLDER_NAME_OVERRIDES[rawFolder] ?: rawFolder
		val parentArgumentType = generator.getParentArgumentType() ?: continue
		if (parentArgumentType.isBlank()) continue
		val simpleName = parentArgumentType.substringBefore(" ").substringAfterLast(".")
		mapping[folder] = "${simpleName}Argument"
	}

	val tagsGenerator = allGenerators.firstOrNull { it.name == "Tags" }
	tagsGenerator?.tagsParents?.keys?.forEach { tagFolder ->
		mapping["tags/$tagFolder"] = "TagArgument"
	}

	val initializer = CodeBlock.builder().apply {
		add("mapOf(\n")
		indent()
		mapping.forEach { (folder, argument) -> add("%S to %S,\n", folder, argument) }
		unindent()
		add(")")
	}.build()

	generateFile("DatapackFolderRegistry") { _ ->
		addProperty(
			PropertySpec.builder("DATAPACK_FOLDER_TO_ARGUMENT", MAP.parameterizedBy(STRING, STRING))
				.initializer(initializer)
				.build()
		)
	}
}
