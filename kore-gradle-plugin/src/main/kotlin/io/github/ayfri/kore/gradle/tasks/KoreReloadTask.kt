package io.github.ayfri.kore.gradle.tasks

import io.github.ayfri.kore.gradle.internal.Rcon
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.UntrackedTask

/**
 * Sends a command to a running server over RCON, `reload` by default.
 *
 * A missing password or an unreachable server is reported and skipped unless [failOnError] is set, so a watch loop
 * keeps working while the game is closed.
 */
@UntrackedTask(because = "Talks to a running server, there is no output to track.")
abstract class KoreReloadTask : DefaultTask() {
	/** Named `rconEnabled` because `Task.enabled` already exists and controls whether the task runs at all. */
	@get:Input
	abstract val rconEnabled: Property<Boolean>

	@get:Input
	abstract val host: Property<String>

	@get:Input
	abstract val port: Property<Int>

	@get:Input
	@get:Optional
	abstract val password: Property<String>

	@get:Input
	abstract val command: Property<String>

	@get:Input
	abstract val timeoutMillis: Property<Int>

	@get:Input
	abstract val failOnError: Property<Boolean>

	@TaskAction
	fun reload() {
		if (!rconEnabled.get()) {
			logger.info("RCON is disabled, skipping reload.")
			return
		}

		val password = password.orNull
		if (password.isNullOrEmpty()) {
			report("No RCON password set, skipping reload. Set `kore.rcon.password` or the RCON_PASSWORD environment variable.")
			return
		}

		val address = "${host.get()}:${port.get()}"
		runCatching { Rcon.send(host.get(), port.get(), password, command.get(), timeoutMillis.get()) }
			.onSuccess { logger.lifecycle("Sent `${command.get()}` to $address: ${it.trim().ifEmpty { "no output" }}") }
			.onFailure { report("Could not send `${command.get()}` to $address: ${it.message}", it) }
	}

	private fun report(message: String, cause: Throwable? = null) {
		if (failOnError.get()) throw GradleException(message, cause)
		logger.lifecycle(message)
	}
}
