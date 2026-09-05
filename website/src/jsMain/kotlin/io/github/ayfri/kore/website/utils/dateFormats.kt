package io.github.ayfri.kore.website.utils

import io.github.ayfri.kore.website.externals.Intl
import io.github.ayfri.kore.website.externals.dateTimeFormatOptions
import io.github.ayfri.kore.website.externals.relativeTimeFormatOptions
import kotlin.js.Date

private const val MILLISECONDS_PER_DAY = 24.0 * 60 * 60 * 1000

private val dayMonthYear = Intl.DateTimeFormat("en-GB", dateTimeFormatOptions(day = "2-digit", month = "2-digit", year = "numeric"))

private val relativeTime = Intl.RelativeTimeFormat("en", relativeTimeFormatOptions(numeric = "auto"))

/** Formats an ISO date as `dd/mm/yyyy`. */
fun formatDate(isoDateString: String) = dayMonthYear.format(Date(isoDateString))

/** Formats how long ago an ISO date is, e.g. `today`, `yesterday`, `5 days ago`, `last month`, `2 years ago`. */
fun formatRelativeDate(isoDateString: String): String {
	val days = ((Date().getTime() - Date(isoDateString).getTime()) / MILLISECONDS_PER_DAY).toInt()

	val (amount, unit) = when {
		days < 30 -> days to "day"
		days < 365 -> days / 30 to "month"
		else -> days / 365 to "year"
	}

	return relativeTime.format(-amount, unit)
}
