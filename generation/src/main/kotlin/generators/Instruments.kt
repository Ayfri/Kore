package generators

import generateEnum
import getFromCacheOrDownloadTxt
import url

suspend fun downloadInstruments() {
	val url = url("custom-generated/registries/instrument.txt")
	val instruments = getFromCacheOrDownloadTxt("instrument.txt", url).lines()

	generateInstrumentEnum(instruments, url)
}

fun generateInstrumentEnum(instruments: List<String>, sourceUrl: String) {
	generateEnum(instruments, "Instruments", sourceUrl, "Instrument")
}
