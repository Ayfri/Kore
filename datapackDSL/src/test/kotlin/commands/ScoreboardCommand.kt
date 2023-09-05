package commands

import arguments.DisplaySlots
import arguments.types.literals.self
import assertions.assertsIs
import functions.Function

fun Function.scoreboardTests() {
	scoreboard {
		objectives {
			add("test", "dummy", "Test") assertsIs "scoreboard objectives add test dummy \"Test\""
			list() assertsIs "scoreboard objectives list"
			modify("test", displayName = "Test") assertsIs "scoreboard objectives modify test displayname \"Test\""
			modify("test", RenderType.HEARTS) assertsIs "scoreboard objectives modify test rendertype hearts"
			remove("test") assertsIs "scoreboard objectives remove test"
			setDisplay(DisplaySlots.list, "test") assertsIs "scoreboard objectives setdisplay list test"
		}

		players {
			add(self(), "test", 5) assertsIs "scoreboard players add @s test 5"
			enable(self(), "test") assertsIs "scoreboard players enable @s test"
			get(self(), "test") assertsIs "scoreboard players get @s test"
			list() assertsIs "scoreboard players list"
			list(self()) assertsIs "scoreboard players list @s"
			operation(self(), "test", Operation.ADD, self(), "test") assertsIs "scoreboard players operation @s test += @s test"
			remove(self(), "test", 5) assertsIs "scoreboard players remove @s test 5"
			reset(self()) assertsIs "scoreboard players reset @s"
			reset(self(), "test") assertsIs "scoreboard players reset @s test"
			set(self(), "test", 5) assertsIs "scoreboard players set @s test 5"
		}

		objective("test") {
			add("dummy", "Test") assertsIs "scoreboard objectives add test dummy \"Test\""
			create("dummy", "Test") assertsIs "scoreboard objectives add test dummy \"Test\""
			modify(displayName = "Test") assertsIs "scoreboard objectives modify test displayname \"Test\""
			modify(RenderType.HEARTS) assertsIs "scoreboard objectives modify test rendertype hearts"
			remove() assertsIs "scoreboard objectives remove test"
			setDisplay(DisplaySlots.list) assertsIs "scoreboard objectives setdisplay list test"

			add(self(), 5) assertsIs "scoreboard players add @s test 5"
			enable(self()) assertsIs "scoreboard players enable @s test"
			get(self()) assertsIs "scoreboard players get @s test"
			operation(self(), Operation.ADD, self(), "test") assertsIs "scoreboard players operation @s test += @s test"
			remove(self(), 5) assertsIs "scoreboard players remove @s test 5"
			reset(self()) assertsIs "scoreboard players reset @s test"
			set(self(), 5) assertsIs "scoreboard players set @s test 5"

			player(self()) {
				add(5) assertsIs "scoreboard players add @s test 5"
				enable() assertsIs "scoreboard players enable @s test"
				get() assertsIs "scoreboard players get @s test"
				operation(Operation.ADD, self(), "test") assertsIs "scoreboard players operation @s test += @s test"
				operation(Operation.ADD, this) assertsIs "scoreboard players operation @s test += @s test"
				remove(5) assertsIs "scoreboard players remove @s test 5"
				reset() assertsIs "scoreboard players reset @s test"
				set(5) assertsIs "scoreboard players set @s test 5"
			}
		}
	}

	scoreboard.objectives {
		list() assertsIs "scoreboard objectives list"
	}

	scoreboard.objective("test") {
		get(self()) assertsIs "scoreboard players get @s test"
	}

	scoreboard.objective(self(), "test") {
		get() assertsIs "scoreboard players get @s test"
	}

	scoreboard.players {
		get(self(), "test") assertsIs "scoreboard players get @s test"
	}

	scoreboard.player(self()) {
		get("test") assertsIs "scoreboard players get @s test"
	}

	scoreboard.players.get(self(), "test") assertsIs "scoreboard players get @s test"
	scoreboard.objectives.list() assertsIs "scoreboard objectives list"
}
