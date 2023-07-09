package commands

import arguments.chatcomponents.scoreComponent
import arguments.chatcomponents.textComponent
import arguments.self
import arguments.set
import functions.Function
import net.benwoodworth.knbt.NbtInt
import utils.assertsIs

fun Function.dataTests() {
	data(self()) {
		get("foo") assertsIs "data get entity @s foo"
		get("foo", 1.0) assertsIs "data get entity @s foo 1"

		merge(scoreComponent("foo", self())) assertsIs "data merge entity @s {score:{name:\"@s\",objective:\"foo\"}}"
		merge { this["foo"] = "bar" } assertsIs "data merge entity @s {foo:\"bar\"}"

		modify("foo") { append(self(), "bar") } assertsIs "data modify entity @s foo append from entity @s bar"
		modify("foo") { append(NbtInt(1)) } assertsIs "data modify entity @s foo append value 1"
		modify("foo") { append(true) } assertsIs "data modify entity @s foo append value true"
		modify("foo") { append(textComponent("bar")) } assertsIs "data modify entity @s foo append value \"bar\""

		modify("foo") { insert(0, self(), "bar") } assertsIs "data modify entity @s foo insert 0 from entity @s bar"
		modify("foo") { insert(0, NbtInt(1)) } assertsIs "data modify entity @s foo insert 0 value 1"
		modify("foo") { insert(0, true) } assertsIs "data modify entity @s foo insert 0 value true"
		modify("foo") { insert(0, textComponent("bar")) } assertsIs "data modify entity @s foo insert 0 value \"bar\""

		modify("foo") { merge(1) } assertsIs "data modify entity @s foo merge value 1"
		modify("foo") {
			merge(scoreComponent("foo", self()))
		} assertsIs "data modify entity @s foo merge value {score:{name:\"@s\",objective:\"foo\"}}"

		modify("foo") { prepend(self(), "bar") } assertsIs "data modify entity @s foo prepend from entity @s bar"
		modify("foo") { prepend(NbtInt(1)) } assertsIs "data modify entity @s foo prepend value 1"
		modify("foo") { prepend(true) } assertsIs "data modify entity @s foo prepend value true"
		modify("foo") { prepend(textComponent("bar")) } assertsIs "data modify entity @s foo prepend value \"bar\""

		modify("foo") { set(self(), "bar") } assertsIs "data modify entity @s foo set from entity @s bar"
		modify("foo") { set(NbtInt(1)) } assertsIs "data modify entity @s foo set value 1"
		modify("foo") { set(true) } assertsIs "data modify entity @s foo set value true"
		modify("foo") { set(textComponent("bar")) } assertsIs "data modify entity @s foo set value \"bar\""
		modify("foo") { set(self(), "bar", 1) } assertsIs "data modify entity @s foo set string entity @s bar 1"
		modify("foo") { set(self(), "bar", 0, 1) } assertsIs "data modify entity @s foo set string entity @s bar 0 1"
		modify("foo") { set(self(), "bar", -2) } assertsIs "data modify entity @s foo set string entity @s bar -2"

		modify("foo", "bar") assertsIs "data modify entity @s foo set value \"bar\""

		remove("foo") assertsIs "data remove entity @s foo"

		set("foo", "bar") assertsIs "data modify entity @s foo set value \"bar\""
	}
}
