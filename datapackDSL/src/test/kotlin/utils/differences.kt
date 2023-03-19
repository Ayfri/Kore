package utils

fun generateDiffString(expected: String, got: String): String {
	val minLength = minOf(expected.length, got.length)
	var firstDifferenceIndex = -1
	var lastDifferenceIndex = -1

	for (i in 0 until minLength) {
		if (expected[i] != got[i]) {
			firstDifferenceIndex = i
			break
		}
	}

	if (firstDifferenceIndex == -1 && expected.length > got.length) return "Expected: '$expected'\nGot     : '$got'\n" + " ".repeat("Expected: '".length + got.length) + "^".repeat(
		expected.length - got.length
	)

	for (i in (minLength - 1) downTo firstDifferenceIndex) {
		if (expected[i] != got[i]) {
			lastDifferenceIndex = i
			break
		}
	}

	val expectedDiff = expected.substring(firstDifferenceIndex..lastDifferenceIndex)
	val gotDiff = got.substring(firstDifferenceIndex..lastDifferenceIndex)

	val stringBuilder = StringBuilder()
	stringBuilder.append("Expected: '$expected'\nGot     : '$got'\n")
	for (i in 0 until firstDifferenceIndex + "Expected: '".length) {
		stringBuilder.append(" ")
	}
	for (i in firstDifferenceIndex..lastDifferenceIndex) {
		if (expectedDiff.length > gotDiff.length && i == gotDiff.length + firstDifferenceIndex) {
			stringBuilder.append("^".repeat(expectedDiff.length - gotDiff.length))
		}
		stringBuilder.append(if (expected[i] != got[i]) "^" else " ")
	}

	return stringBuilder.toString()
}

fun generateDiffString(expectedRegex: Regex, got: String) = "Expected: '$expectedRegex'\nGot     : '$got'\n"
