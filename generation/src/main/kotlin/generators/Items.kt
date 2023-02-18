package generators

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import generateEnum
import getFromCacheOrDownloadTxt
import url

suspend fun downloadItems() {
	val url = url("custom-generated/registries/item.txt")
	val items = getFromCacheOrDownloadTxt("items.txt", url).lines()

	generateItemsEnum(items, url)
}

fun generateItemsEnum(items: List<String>, sourceUrl: String) {
	generateEnum(items, "Items", sourceUrl, "Item") {
		addProperty(
			PropertySpec.builder("nbtData", ClassName("net.benwoodworth.knbt", "NbtCompound").copy(nullable = true))
				.addModifiers(KModifier.OVERRIDE)
				.mutable()
				.initializer("null")
				.build()
		)
	}
}
