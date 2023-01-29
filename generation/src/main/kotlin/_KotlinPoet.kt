import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

fun TypeSpec.Builder.setSealed() = apply {
	addModifiers(KModifier.SEALED)
}

fun FunSpec.Builder.overrides() = apply {
	addModifiers(KModifier.OVERRIDE)
}

fun PropertySpec.Builder.overrides() = apply {
	addModifiers(KModifier.OVERRIDE)
}
