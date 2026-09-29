package io.github.ayfri.kore.website.components.playground.language

/** Past this many hops through `val` initializers a type is given up on, which also breaks `val a = b; val b = a`. */
private const val MAX_INFERENCE_DEPTH = 4

/** Stdlib functions returning their receiver, so a chain through them keeps its type. */
private val SELF_RETURNING = setOf("also", "apply", "takeIf", "takeUnless")

/** What an expression is: instances of some classes, or a class named as itself, `Items` in `Items.STONE`. */
sealed interface TypeRef {
	data class Instance(val types: Set<String>) : TypeRef
	data class Static(val fqn: String) : TypeRef
}

/** A declaration that fits where it is offered, [distance] 0 meaning the innermost block's `this` provides it. */
class Candidate(val declaration: ApiDeclaration, val distance: Int)

/**
 * Stands in for the compiler: infers `this` in each DSL block from the lambdas' declared receivers, then the type of the
 * chain before a dot, from [KoreApi] alone. Kore snippets are mostly nested builder blocks, which is what this follows;
 * anything it can't infer (generics, stdlib types, `it`) resolves to nothing rather than to a guess.
 */
class KoreResolver(val api: KoreApi, val source: KotlinSource) {
	/** The classes `this` can be at [offset], outermost first, a block contributing every receiver its overloads give. */
	fun receiversAt(offset: Int): List<Set<String>> = source.blocksAt(offset).fold(emptyList()) { receivers, block ->
		val types = when (block) {
			is Block.Body -> setOfNotNull(api.classifier(block.receiverType, source.importedNames)?.fqn)
			is Block.Call -> lambdaReceivers(block, receivers)
		}

		if (types.isEmpty()) receivers else receivers + listOf(types)
	}

	fun typeOf(chain: List<Segment>, receivers: List<Set<String>>, depth: Int = 0): TypeRef? {
		if (depth > MAX_INFERENCE_DEPTH || chain.isEmpty()) return null
		return chain.drop(1).fold(first(chain.first(), receivers, depth) ?: return null) { type, segment -> next(type, segment, receivers) ?: return null }
	}

	/** What a name means with no receiver, ordered from the innermost `this` out to top-level declarations. */
	fun inScope(name: String, receivers: List<Set<String>>) = candidates(api.byName[name].orEmpty(), receivers).map { it.declaration }

	/** Every declaration usable without a receiver: members and extensions of each `this`, then top-level ones. */
	fun scope(receivers: List<Set<String>>): List<Candidate> {
		val types = receivers.flatten().toSet()
		val implicit = api.callablesOn(types) + api.memberExtensions(types, types)
		return candidates(implicit + api.topLevel, receivers)
	}

	/** What a dot after an expression of [type] offers, most derived first, an override hiding what it overrides. */
	fun members(type: TypeRef, receivers: List<Set<String>>) = when (type) {
		is TypeRef.Instance -> api.callablesOn(type.types) + api.memberExtensions(receivers.flatten().toSet(), type.types)
		is TypeRef.Static -> api.staticMembers(type.fqn)
	}.distinctBy { it.overrideKey }

	/**
	 * Declarations a missing import could bring in for [name] at [offset], one per path: the ones fitting the blocks
	 * around it first, then the ones the DSL uses most. Those fitting nowhere get [Int.MAX_VALUE] as distance.
	 */
	fun importCandidates(name: String, offset: Int): List<Candidate> {
		val closures = closures(receiversAt(offset))
		return api.byName[name].orEmpty().filter { it.importPath != null }
			.map { Candidate(it, distance(it, closures) ?: Int.MAX_VALUE) }
			.sortedWith(compareBy({ it.distance }, { -api.popularity(it.declaration.receiver ?: it.declaration.fqn) }))
			.distinctBy { it.declaration.fqn }
	}

	/** The candidates among [declarations] that fit the blocks around, ranked by [distance], an override hiding what it overrides. */
	private fun candidates(declarations: List<ApiDeclaration>, receivers: List<Set<String>>): List<Candidate> {
		val closures = closures(receivers)
		return declarations.mapNotNull { declaration -> distance(declaration, closures)?.let { Candidate(declaration, it) } }
			.sortedBy { it.distance }
			.distinctBy { it.declaration.overrideKey }
	}

	private fun closures(receivers: List<Set<String>>) = receivers.map { level -> level.flatMap(api::supertypes).toSet() }

	/**
	 * How many blocks out the `this` providing [declaration] is, [closures] size for top-level ones, one more when its
	 * context parameters aren't all met, and `null` when it needs a receiver no block gives.
	 */
	private fun distance(declaration: ApiDeclaration, closures: List<Set<String>>): Int? {
		fun level(fqn: String) = closures.indexOfLast { fqn in it }.takeIf { it >= 0 }?.let { closures.size - 1 - it }

		val receiver = declaration.receiver
		val base = when {
			declaration.member == true -> {
				val owner = level(declaration.owner) ?: return null
				receiver?.let { minOf(owner, level(it) ?: return null) } ?: owner
			}

			receiver == "kotlin.Any" -> closures.size.takeIf { it > 0 } ?: return null
			receiver != null -> level(receiver) ?: return null
			// `Textures.Trims.Items` is written through its outer class, the top-level `Items` is the one meant.
			declaration.isClassifier && declaration.owner in api.classifiers -> closures.size + 1
			else -> closures.size
		}

		return if (declaration.context.orEmpty().all { level(it) != null }) base else closures.size + 1
	}

	private fun lambdaReceivers(block: Block.Call, receivers: List<Set<String>>): Set<String> {
		val explicit = block.receiver?.let { typeOf(it, receivers) }
		when (block.name) {
			"apply", "run" -> if (explicit is TypeRef.Instance) return explicit.types
			"with" -> return (block.argument?.let { typeOf(it, receivers) } as? TypeRef.Instance)?.types.orEmpty()
		}

		val called = when {
			block.receiver == null -> inScope(block.name, receivers)
			explicit != null -> members(explicit, receivers).filter { it.name == block.name }
			else -> emptyList()
		}

		called.mapNotNull { it.lambda }.toSet().takeIf { it.isNotEmpty() }?.let { return it }

		// `Items.STONE { }` calls the `invoke` operator of what the name is.
		val value = typeOf(block.receiver.orEmpty() + Segment(block.name, false), receivers) as? TypeRef.Instance ?: return emptySet()
		return api.callablesOn(value.types).filter { it.name == "invoke" }.mapNotNull { it.lambda }.toSet()
	}

	private fun first(segment: Segment, receivers: List<Set<String>>, depth: Int): TypeRef? {
		segment.literal?.let { return TypeRef.Instance(setOf(it)) }
		if (segment.name == "this") return receivers.lastOrNull()?.let(TypeRef::Instance)

		source.locals.lastOrNull { it.name == segment.name && it.kind == LocalKind.VALUE }?.let { return localType(it, depth) }

		val classifier = api.classifier(segment.name, source.importedNames)?.takeIf { segment.name.first().isUpperCase() }
		return when {
			classifier != null && segment.call -> TypeRef.Instance(setOf(classifier.fqn))
			classifier != null -> TypeRef.Static(classifier.fqn)
			else -> returns(inScope(segment.name, receivers).called(segment.call))
		}
	}

	/** `scoreboard.` reads the `scoreboard` property, `scoreboard { }` calls the function of the same name. */
	private fun List<ApiDeclaration>.called(call: Boolean) = filter { if (call) it.isFunction else it.kind == "property" }.ifEmpty { this }

	private fun next(type: TypeRef, segment: Segment, receivers: List<Set<String>>): TypeRef? {
		if (type is TypeRef.Instance && segment.name in SELF_RETURNING) return type
		if (type is TypeRef.Static && api.classifiers[type.fqn]?.entries?.contains(segment.name) == true) {
			val entry = TypeRef.Instance(setOf(type.fqn))
			return if (segment.call) invoked(entry) else entry
		}

		val found = members(type, receivers).filter { it.name == segment.name }
		found.firstOrNull { it.isClassifier }?.let { return if (segment.call) TypeRef.Instance(setOf(it.fqn)) else TypeRef.Static(it.fqn) }

		val value = returns(found.called(segment.call)) ?: return null
		return if (segment.call && found.none { it.isFunction }) invoked(value) else value
	}

	private fun localType(local: Local, depth: Int): TypeRef? {
		local.type?.let { name -> return api.classifier(name, source.importedNames)?.let { TypeRef.Instance(setOf(it.fqn)) } }
		val chain = local.initializer?.let(source::chainEndingAt) ?: return null
		return typeOf(chain, receiversAt(source.code[local.token].start), depth + 1)
	}

	private fun invoked(type: TypeRef.Instance) = returns(api.callablesOn(type.types).filter { it.name == "invoke" })

	private fun returns(declarations: List<ApiDeclaration>) =
		declarations.mapNotNull { it.returns }.filter { it != "kotlin.Unit" }.toSet().takeIf { it.isNotEmpty() }?.let(TypeRef::Instance)
}
