package io.github.ayfri.kore.website.utils

import kotlin.js.Date

fun formatDate(isoDateString: String): String {
	val date = Date(isoDateString)

	val day = date.getDate().toString().padStart(2, '0')
	val month = (date.getMonth() + 1).toString().padStart(2, '0')
	val year = date.getFullYear().toString()

	return "$day/$month/$year"
}

fun formatRelativeDate(isoDateString: String): String {
	val date = Date(isoDateString)
	val now = Date()

	val diffMs = now.getTime() - date.getTime()
	val diffDays = (diffMs / (1000 * 60 * 60 * 24)).toInt()
	val diffMonths = (diffDays / 30).toInt()
	val diffYears = (diffDays / 365).toInt()

	return when {
		diffDays < 1 -> "today"
		diffDays == 1 -> "yesterday"
		diffDays < 30 -> "$diffDays days ago"
		diffMonths == 1 -> "1 month ago"
		diffMonths < 12 -> "$diffMonths months ago"
		diffYears == 1 -> "1 year ago"
		else -> "$diffYears years ago"
	}
}
