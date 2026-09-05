package io.github.ayfri.kore.gradle

import org.gradle.api.provider.Property

/** Connection settings used by the `koreReload` task to send a command to a running server. */
abstract class RconOptions {
	/** Whether `koreReload` tries to connect at all. Defaults to `true`, a missing password is reported and skipped. */
	abstract val enabled: Property<Boolean>

	abstract val host: Property<String>

	abstract val port: Property<Int>

	/** RCON password, as set by `rcon.password` in `server.properties`. Reads the `RCON_PASSWORD` environment variable by default. */
	abstract val password: Property<String>

	/** Command sent once connected, without the leading slash. */
	abstract val command: Property<String>

	abstract val timeoutMillis: Property<Int>

	/**
	 * Whether an unreachable or rejecting server fails the build. Defaults to `false` so a watch loop keeps running
	 * while the game is closed.
	 */
	abstract val failOnError: Property<Boolean>
}
