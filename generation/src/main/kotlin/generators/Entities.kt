package generators

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import generateEnum
import getFromCacheOrDownloadTxt
import url

suspend fun downloadEntities() {
	val url = url("custom-generated/registries/entity_type.txt")
	val entities = getFromCacheOrDownloadTxt("entities.txt", url).lines()

	generateEntitiesEnum(entities, url)
}

fun generateEntitiesEnum(entities: List<String>, sourceUrl: String) {
	generateEnum(entities, "Entities", sourceUrl, "EntitySummon", additionalCode = { enum ->
		addFunction(
			FunSpec.builder("type")
				.receiver(ClassName("arguments.selector", "SelectorNbtData"))
				.addParameter("entity", ClassName("generated", enum))
				.addStatement("type = entity.asArg()")
				.build()
		)

		addImport("commands", "asArg")
	})
}
