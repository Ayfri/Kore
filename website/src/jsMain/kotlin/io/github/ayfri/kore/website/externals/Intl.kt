package io.github.ayfri.kore.website.externals

import io.github.ayfri.kore.website.utils.jsObject
import kotlin.js.Date

/** Bindings for the parts of the ECMA-402 `Intl` namespace the website formats its dates and counts with. */
external object Intl {
	class DateTimeFormat(locales: String, options: DateTimeFormatOptions = definedExternally) {
		fun format(date: Date): String
	}

	class RelativeTimeFormat(locales: String, options: RelativeTimeFormatOptions = definedExternally) {
		fun format(value: Int, unit: String): String
	}

	class NumberFormat(locales: String, options: NumberFormatOptions = definedExternally) {
		fun format(value: Number): String
	}
}

external interface DateTimeFormatOptions {
	var day: String?
	var month: String?
	var year: String?
}

fun dateTimeFormatOptions(day: String? = null, month: String? = null, year: String? = null) =
	jsObject<DateTimeFormatOptions> {
		this.day = day
		this.month = month
		this.year = year
	}

external interface RelativeTimeFormatOptions {
	/** `"auto"` renders `-1 day` as `yesterday`, `"always"` keeps it as `1 day ago`. */
	var numeric: String?
}

fun relativeTimeFormatOptions(numeric: String? = null) = jsObject<RelativeTimeFormatOptions> {
	this.numeric = numeric
}

external interface NumberFormatOptions {
	/** `"compact"` is what turns `1234` into `1.2K`. */
	var notation: String?
	var maximumFractionDigits: Int?
}

fun numberFormatOptions(notation: String? = null, maximumFractionDigits: Int? = null) = jsObject<NumberFormatOptions> {
	this.notation = notation
	this.maximumFractionDigits = maximumFractionDigits
}
