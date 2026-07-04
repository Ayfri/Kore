package io.github.ayfri.kore.serializers

/**
 * Marks a sealed class/interface whose polymorphic serializer is built reflection-free.
 * The KSP processor emits a `SealedClassSerializer<T>` factory for it into generated code;
 * the family's `data object FooSerializer` feeds that factory to its
 * [NamespacedPolymorphicSerializer] / [EnumLikeSerializer] constructor.
 *
 * Placement: on the sealed base type.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class GeneratedSealedSerializer
