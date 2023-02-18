package generators

import generateEnum
import getFromCacheOrDownloadTxt
import removeJSONSuffix
import url

suspend fun downloadParticles() {
	val url = url("custom-generated/lists/particles.txt")
	val particles = getFromCacheOrDownloadTxt("particles.txt", url).lines()

	generateParticlesEnum(particles, url)
}

fun generateParticlesEnum(particles: List<String>, sourceUrl: String) {
	generateEnum(particles.removeJSONSuffix(), "Particles", sourceUrl, "Particle")
}
