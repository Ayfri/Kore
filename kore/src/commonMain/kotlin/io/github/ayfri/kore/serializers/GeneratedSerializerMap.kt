package io.github.ayfri.kore.serializers

/**
 * Marks a non-sealed base type whose concrete subtypes (across any package) get a KSP-generated
 * `Map<KClass<out T>, KSerializer<out T>>` lookup, so the runtime serializer for a value is found
 * by a plain map lookup on `value::class` instead of `KClass.createType()` + `serializer(KType)` reflection.
 *
 * Unlike [GeneratedSealedSerializer], the annotated type does not need to be `sealed` - the processor
 * walks every file in the compilation for concrete subtypes instead of enumerating direct sealed subclasses.
 *
 * Placement: on the base type. The generated file lives next to the KSP-generated sealed serializers.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class GeneratedSerializerMap
