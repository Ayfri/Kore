package io.github.ayfri.kore.helpers.conversations

/**
 * Marks the [Conversation] builder, the [ConversationChoice] it nests and the receiver of its `step` and `waitUntil` blocks.
 *
 * Inside `choice("Quest") { option("Accept") { } }`, a call such as `line("Hi")` made from the choice block fails to compile
 * instead of silently landing on the enclosing conversation, and so does `step { line("Hi") }`.
 */
@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class ConversationDsl
