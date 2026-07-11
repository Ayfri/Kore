package io.github.ayfri.kore

import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.spawner.registerSpawner
import io.github.ayfri.kore.utils.testDataPack
import io.kotest.core.spec.style.FunSpec

fun spawnerTests() = testDataPack("spawner_tests") {
	val zombieSpawner = registerSpawner("zombie_wave", EntityTypes.ZOMBIE) {
		position = vec3(100, 64, 200)
	}

	function("spawn_zombie") {
		zombieSpawner.spawn() assertsIs "summon minecraft:zombie 100.0 64.0 200.0"
		lines.size assertsIs 1
	}

	function("spawn_at") {
		zombieSpawner.spawnAt(vec3(50, 70, 50)) assertsIs "summon minecraft:zombie 50.0 70.0 50.0"
		lines.size assertsIs 1
	}

	function("spawn_multiple") {
		zombieSpawner.spawnMultiple(3)
		lines[0] assertsIs "summon minecraft:zombie 100.0 64.0 200.0"
		lines[1] assertsIs "summon minecraft:zombie 100.0 64.0 200.0"
		lines[2] assertsIs "summon minecraft:zombie 100.0 64.0 200.0"
		lines.size assertsIs 3
	}

	generatedFunctions.any { it.name == OopConstants.spawnerSpawnFunctionName("zombie_wave") } assertsIs true
}.apply {
	generate()
}

class SpawnerTests : FunSpec({
	test("spawner") {
		spawnerTests()
	}
})
