package io.github.ayfri.kore.utils

/**
 * Receives every message Kore and its modules log while building, merging or importing a pack. Raise [level] to
 * silence them, or replace [handler] to route them to your own logger.
 * ```
 * KoreLogger.level = KoreLogger.Level.WARN // hides progress lines, keeps warnings
 * KoreLogger.handler = { level, message -> logger.atLevel(level.name).log(message) }
 * ```
 */
object KoreLogger {
	enum class Level { INFO, WARN, OFF }

	var level = Level.INFO
	var handler: (Level, String) -> Unit = { level, message ->
		println(if (level == Level.WARN) "\u001B[33mWarning: $message\u001B[0m" else message)
	}

	fun info(message: String) = log(Level.INFO, message)
	fun warn(message: String) = log(Level.WARN, message)

	private fun log(level: Level, message: String) {
		if (level >= this.level) handler(level, message)
	}
}
