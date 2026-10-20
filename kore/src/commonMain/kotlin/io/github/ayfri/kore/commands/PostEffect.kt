package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.PostEffectArgument

/** DSL scope for the `/posteffect` command, which applies client-side shaders defined by resource packs. */
class PostEffect(private val fn: Function) {
	/** Applies [postEffect] to [targets]. */
	fun add(targets: EntityArgument, postEffect: PostEffectArgument) = fn.addLine(command("posteffect", literal("add"), targets, postEffect))

	/** Removes every post effect from [targets]. */
	fun clear(targets: EntityArgument) = fn.addLine(command("posteffect", literal("clear"), targets))

	/** Lists the post effects applied to [target]. */
	fun list(target: EntityArgument) = fn.addLine(command("posteffect", literal("list"), target))

	/** Removes [postEffect] from [targets]. */
	fun remove(targets: EntityArgument, postEffect: PostEffectArgument) = fn.addLine(command("posteffect", literal("remove"), targets, postEffect))
}

/** Opens the [PostEffect] DSL. */
fun Function.postEffect(block: PostEffect.() -> Command) = PostEffect(this).block()
