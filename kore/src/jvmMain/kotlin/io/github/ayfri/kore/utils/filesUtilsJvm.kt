package io.github.ayfri.kore.utils

import kotlinx.io.files.Path
import java.io.File

fun Path(file: File) = Path(file.absolutePath)

fun Path.toJavaFile() = File(this.toString())
