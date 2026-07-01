import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
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

private val serializableClassName = ClassName("kotlinx.serialization", "Serializable")

fun serializableWith(serializerLiteral: String): AnnotationSpec =
	AnnotationSpec.builder(serializableClassName).addMember("with = $serializerLiteral::class").build()

fun serializableWith(serializerClass: ClassName): AnnotationSpec =
	AnnotationSpec.builder(serializableClassName).addMember("with = %T::class", serializerClass).build()

fun TypeSpec.Builder.addMinecraftNamespaceProperty(): TypeSpec.Builder = apply {
	addProperty(
		PropertySpec.builder("namespace", String::class)
			.getter(FunSpec.getterBuilder().addStatement("return \"minecraft\"").build())
			.overrides()
			.build()
	)
}
