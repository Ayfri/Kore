import com.squareup.kotlinpoet.ClassName

fun argumentClassName(argument: String): ClassName {
	val packageSuffix = when {
		"." in argument -> ".${argument.substringBeforeLast(".")}"
		else -> ""
	}

	return ClassName(
		packageName = "arguments.types.resources.$packageSuffix",
		"${argument.substringAfterLast(".")}Argument"
	)
}
