package utils

fun generateDiffString(expected: String, got: String): String {
	val minLength = minOf(expected.length, got.length)
	var firstDifferenceIndex = (0..<minLength).firstOrNull { expected[it] != got[it] } ?: -1

	if (firstDifferenceIndex == -1 && expected.length > got.length) return "Expected: '$expected'\nGot     : '$got'\n" + " ".repeat("Expected: '".length + got.length) + "^".repeat(
		expected.length - got.length
	)

	if (expected.length < got.length) {
		firstDifferenceIndex = (0..<minLength).firstOrNull { expected[it] != got[it] } ?: -1
		if (firstDifferenceIndex == -1) firstDifferenceIndex = minLength
		return "Expected: '$expected'\nGot     : '$got'\n" + " ".repeat("Expected: '".length + firstDifferenceIndex) + "^".repeat(
			got.length - expected.length
		)
	}

	val lastDifferenceIndex = ((minLength - 1) downTo firstDifferenceIndex).firstOrNull { expected[it] != got[it] } ?: -1

	val expectedDiff = expected.substring(firstDifferenceIndex..lastDifferenceIndex)
	val gotDiff = got.substring(firstDifferenceIndex..lastDifferenceIndex)

	val stringBuilder = StringBuilder()
	stringBuilder.append("Expected: '$expected'\nGot     : '$got'\n")
	for (i in 0..<firstDifferenceIndex + "Expected: '".length) {
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

private const val reset = "\u001B[0m"
private const val red = "\u001B[31m"
private const val bold = "\u001B[1m"
private const val redBold = "$red$bold"
fun generateDiffStringJson(expected: String, got: String): String {
	val expectedLines = expected.lines()
	val gotLines = got.lines()

	val diff = mutableListOf<String>()

	var i = 0
	while (i < expectedLines.size && i < gotLines.size) {
		if (expectedLines[i] != gotLines[i]) {
			diff.add("${redBold}Line $i differs:$reset Expected: $redBold${expectedLines[i].trim()}$reset Got: $redBold${gotLines[i].trim()}$reset")
		}
		i++
	}

	if (i < expectedLines.size) {
		diff.add("${redBold}Expected has ${expectedLines.size - i} extra lines$reset")
	} else if (i < gotLines.size) {
		diff.add("${redBold}Got has ${gotLines.size - i} extra lines$reset")
	}

	return diff.joinToString("\n")
}
