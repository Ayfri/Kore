package io.github.ayfri.kore.bindings.generation


/**
 * Information about a resource type for code generation.
 */
data class ResourceTypeInfo(
	val singularName: String,
	val pluralName: String,
	val argumentInterface: String,
	val argumentPackage: String,
)

/**
 * Gets resource type information for code generation.
 * Auto-generates names for all resource types.
 */
fun getResourceTypeInfo(resourceType: String): ResourceTypeInfo {
	val pluralName = resourceType.pascalCase() + (if (!resourceType.endsWith("s")) "s" else "")
	val singularName = resourceType.pascalCase()
	val argumentInterface = singularName + "Argument"
	val argumentPackage = "io.github.ayfri.kore.generation"

	return ResourceTypeInfo(singularName, pluralName, argumentInterface, argumentPackage)
}
