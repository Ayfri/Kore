package arguments.numbers

internal val Double.str
	get() = when {
		this.rem(1) == 0.0 -> this.toInt().toString()
		else -> toString()
	}

internal val Double.strUnlessZero
	get() = when {
		this == 0.0 -> ""
		else -> str
	}
