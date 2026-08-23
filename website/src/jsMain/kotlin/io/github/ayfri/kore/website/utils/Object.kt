package io.github.ayfri.kore.website.utils

external class Object {
	companion object {
		fun keys(obj: dynamic): Array<String>
		fun <T : Any> values(obj: dynamic): Array<T>
	}
}

/** Builds a plain JS object, for shapes that have no external interface such as JSON-LD documents. */
fun obj(init: dynamic.() -> Unit): dynamic = Object().apply(init)

/** Builds a plain JS object typed as [T], the way external interfaces modelling option bags are meant to be filled. */
fun <T : Any> jsObject(init: T.() -> Unit): T = Object().unsafeCast<T>().apply(init)
