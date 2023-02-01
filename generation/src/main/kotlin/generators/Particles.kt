package generators

import generateEnum
import getFromCacheOrDownloadTxt
import url

suspend fun downloadParticles() {
	val url = url("data/registries/particle_type.txt")
	val particles = getFromCacheOrDownloadTxt("particles.txt", url).lines()

	generateParticlesEnum(particles, url)
}

fun generateParticlesEnum(particles: List<String>, sourceUrl: String) {
	generateEnum(particles, "Particles", sourceUrl, "Particle")
}
