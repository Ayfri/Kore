package io.github.ayfri.kore.bindings

import java.nio.file.Path
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.io.path.isDirectory
import kotlin.io.path.pathString

fun Path.isZipFormat() = !isDirectory() && toFile().extension == "zip"

fun Path.matches(regex: Regex) = pathString.matches(regex)

// Normalize path for cross-platform consistency, removing leading slashes from ZIP entries
val Path.pathInvariant get() = invariantSeparatorsPathString.trimStart('/')
