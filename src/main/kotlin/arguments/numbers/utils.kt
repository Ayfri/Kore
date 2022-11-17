package arguments.numbers

internal val Double.str
	get() = when {
		this == toFloat().toDouble() -> this.toInt().toString()
		else -> toString()
	}

internal val Double.strUnlessZero
	get() = when {
		this == 0.0 -> ""
		else -> str
	}
