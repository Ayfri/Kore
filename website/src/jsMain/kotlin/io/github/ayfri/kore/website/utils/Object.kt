package io.github.ayfri.kore.website.utils


external class Object {
	companion object {
		fun keys(obj: dynamic): Array<String>
		fun <T : Any> values(obj: dynamic): Array<T>
	}
}