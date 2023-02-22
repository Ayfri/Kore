package features.advancements.states

fun int(value: Int) = StateInt(value)
fun boolean(value: Boolean) = StateBoolean(value)
fun string(value: String) = StateString(value)

fun state(value: StateData) = State(value = value)
fun state(value: Boolean) = State(value = StateBoolean(value))
fun state(value: Int) = State(value = StateInt(value))
fun state(value: String) = State(value = StateString(value))

fun state(min: StateData, max: StateData) = State(min, max)
fun state(min: Int, max: Int) = State(int(min), int(max))
fun state(min: Boolean, max: Boolean) = State(boolean(min), boolean(max))
fun state(min: String, max: String) = State(string(min), string(max))

fun Int.toState() = StateInt(this)
fun Boolean.toState() = StateBoolean(this)
fun String.toState() = StateString(this)

fun Pair<StateData, StateData>.toState() = State(first, second)

@JvmName("toStateInt")
fun Pair<Int, Int>.toState() = State(int(first), int(second))

@JvmName("toStateBoolean")
fun Pair<Boolean, Boolean>.toState() = State(boolean(first), boolean(second))

@JvmName("toStateString")
fun Pair<String, String>.toState() = State(string(first), string(second))
