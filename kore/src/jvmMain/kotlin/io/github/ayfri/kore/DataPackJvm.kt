package io.github.ayfri.kore

import io.github.ayfri.kore.utils.Path

/** Sets the icon path of the pack displayed in-game from a [java.nio.file.Path]. */
fun DataPack.iconPath(path: java.nio.file.Path) {
	iconPath = Path(path.toAbsolutePath().toFile())
}

/** Sets the output path of the pack from a [java.nio.file.Path]. */
fun DataPack.path(path: java.nio.file.Path) {
	this.path = Path(path.toAbsolutePath().toFile())
}
