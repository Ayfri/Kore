package io.github.ayfri.kore.interop.lanternload

/**
 * A pack version advertised on the Lantern Load status objective as three fake players,
 * `<holder>.major`, `<holder>.minor` and `<holder>.patch`.
 *
 * Docs: https://kore.ayfri.com/docs/guides/lantern-load
 * Lantern Load: https://github.com/LanternMC/load
 */
data class LanternVersion(val major: Int, val minor: Int = 0, val patch: Int = 0) : Comparable<LanternVersion> {
	override fun compareTo(other: LanternVersion) =
		compareValuesBy(this, other, LanternVersion::major, LanternVersion::minor, LanternVersion::patch)

	override fun toString() = "$major.$minor.$patch"

	companion object {
		/** Parses `major[.minor[.patch]]`, ignoring a leading `v` and anything after a `-` pre-release marker. */
		fun of(version: String): LanternVersion {
			val parts = version.removePrefix("v").substringBefore('-').split('.')
			require(parts.size <= 3) { "Invalid Lantern Load version '$version', expected 'major[.minor[.patch]]'." }

			val numbers = parts.map {
				it.toIntOrNull() ?: error("Invalid Lantern Load version '$version', '$it' is not a number.")
			}

			return LanternVersion(numbers[0], numbers.getOrElse(1) { 0 }, numbers.getOrElse(2) { 0 })
		}
	}
}
