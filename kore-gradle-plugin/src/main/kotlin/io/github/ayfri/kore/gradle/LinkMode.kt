package io.github.ayfri.kore.gradle

/** How a generated pack is placed inside a target `datapacks` or `resourcepacks` folder. */
enum class LinkMode {
	/** Copies the whole pack. Works everywhere, but the game only sees changes after the next link. */
	COPY,

	/**
	 * Creates a directory symlink pointing at the generated pack, so a rebuild is visible without linking again.
	 * Falls back to [COPY] when the filesystem refuses the link, which is the common case on Windows without
	 * Developer Mode or administrator rights.
	 */
	SYMLINK,
}
