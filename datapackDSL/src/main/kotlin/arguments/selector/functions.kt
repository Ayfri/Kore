package arguments.selector

import arguments.scores.Scores
import arguments.scores.SelectorScore

fun SelectorNbtData.scores(block: Scores<SelectorScore>.() -> Unit) {
	scores = Scores<SelectorScore>().apply(block)
}
