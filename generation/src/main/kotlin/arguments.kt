import com.squareup.kotlinpoet.ClassName

/** Splits `"worldgen.Structure"` into its subpackage prefix (`"worldgen."`, or `""`) and simple name (`"Structure"`). */
fun splitArgumentTypePrefix(qualifiedName: String): Pair<String, String> {
	val prefix = if ("." in qualifiedName) qualifiedName.substringBeforeLast(".") + "." else ""
	return prefix to qualifiedName.substringAfterLast(".")
}

/** Resolves the `*Argument` interface a generated enum/enum-tree should implement. A `" M"` suffix routes to a hand-written interface. */
fun argumentClassName(argument: String): ClassName {
	val manual = argument.endsWith(" M")
	val argumentName = argument.substringBefore(" ")

	val packageSuffix = when {
		"." in argument -> ".${argument.substringBeforeLast(".")}"
		else -> ""
	}

	if (manual) {
		return ClassName(
			packageName = "$CODE_PACKAGE.arguments.types.resources.$packageSuffix".removeSuffix(".types"),
			"${argumentName.substringAfterLast(".")}Argument"
		)
	}

	return ClassName(
		packageName = "$GENERATED_PACKAGE.arguments.$packageSuffix",
		"${argumentName.substringAfterLast(".")}Argument"
	)
}
