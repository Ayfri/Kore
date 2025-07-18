import com.squareup.kotlinpoet.ClassName

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
