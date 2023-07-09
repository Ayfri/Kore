package generators

import generateEnum
import getFromCacheOrDownloadTxt
import url

suspend fun downloadBlockEntityTypes() {
	val url = url("custom-generated/registries/block_entity_type.txt")
	val blockEntityTypes = getFromCacheOrDownloadTxt("block_entity_type.txt", url).lines()

	generateBlockEntityTypeEnum(blockEntityTypes, url)
}

fun generateBlockEntityTypeEnum(blockEntityTypes: List<String>, sourceUrl: String) {
	generateEnum(blockEntityTypes, "BlockEntityTypes", sourceUrl, "BlockEntityType")
}
