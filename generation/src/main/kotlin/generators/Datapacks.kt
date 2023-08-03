package generators

import generateEnum
import getFromCacheOrDownloadTxt
import url

suspend fun downloadDataPacks() {
	val url = url("custom-generated/lists/datapacks.txt")
	val values = getFromCacheOrDownloadTxt("datapacks.txt", url).lines() + "vanilla"

	generateEnum(values, "DataPacks", url)
}
