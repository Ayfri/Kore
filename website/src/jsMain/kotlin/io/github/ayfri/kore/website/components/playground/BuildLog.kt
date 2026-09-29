package io.github.ayfri.kore.website.components.playground

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.js.Date

enum class LogLevel {
	ERROR,
	INFO,
	SUCCESS,
	WARNING,
}

data class LogEntry(val time: String, val level: LogLevel, val message: String, val detail: String? = null)

/** What the page did and what the backend reported, newest last, shown in the bottom panel's build log. */
object BuildLog {
	private const val MAX_ENTRIES = 300

	val entries = mutableStateListOf<LogEntry>()

	/** Errors and warnings logged since the log was last looked at, the badge on its tab. */
	var unseenIssues by mutableStateOf(0)
		private set

	fun add(level: LogLevel, message: String, detail: String? = null) {
		entries += LogEntry(Date().toLocaleTimeString("en-GB"), level, message, detail)
		if (entries.size > MAX_ENTRIES) entries.removeRange(0, entries.size - MAX_ENTRIES)
		if (level == LogLevel.ERROR || level == LogLevel.WARNING) unseenIssues++
	}

	fun clear() {
		entries.clear()
		unseenIssues = 0
	}

	fun markSeen() {
		unseenIssues = 0
	}
}
