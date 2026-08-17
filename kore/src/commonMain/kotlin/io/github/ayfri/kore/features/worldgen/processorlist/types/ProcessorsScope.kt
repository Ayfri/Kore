package io.github.ayfri.kore.features.worldgen.processorlist.types

/**
 * Builder scope for declaring structure processors.
 *
 * Every processor builder (e.g. [gravity], [blockRot], [rules]) is an extension on this interface, so they only
 * resolve inside a `processorList { }` block or a nested block such as [capped]'s delegate.
 *
 * @property processors The processors appended so far, in application order.
 */
interface ProcessorsScope {
	val processors: MutableList<ProcessorType>
}

/**
 * Standalone [ProcessorsScope] backing the nested processor blocks, such as [capped]'s delegate.
 *
 * @property processors The processors appended so far, in application order.
 */
class ProcessorsBuilder : ProcessorsScope {
	override val processors = mutableListOf<ProcessorType>()
}

/** Collects the processors appended in [block] into a list. */
internal fun buildProcessors(block: ProcessorsScope.() -> Unit) = ProcessorsBuilder().apply(block).processors
