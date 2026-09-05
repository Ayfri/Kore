package io.github.ayfri.kore.gradle

import io.github.ayfri.kore.gradle.internal.Rcon
import io.github.ayfri.kore.gradle.internal.RconException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.concurrent.thread

/**
 * Runs [Rcon] against a stub server speaking the Source RCON protocol, so the packet framing is exercised without a
 * Minecraft server.
 */
class RconTests : FunSpec({
	fun startServer(expectedPassword: String, answer: (String) -> String): FakeRconServer =
		FakeRconServer(expectedPassword, answer).also { it.start() }

	test("a command is sent once authenticated and its answer is returned") {
		val server = startServer("secret") { command -> "ran $command" }

		try {
			Rcon.send("localhost", server.port, "secret", "reload", timeoutMillis = 5_000) shouldBe "ran reload"
			server.receivedCommands shouldBe listOf("reload")
		} finally {
			server.close()
		}
	}

	test("a rejected password fails with an actionable message") {
		val server = startServer("secret") { "" }

		try {
			val exception = shouldThrow<RconException> {
				Rcon.send("localhost", server.port, "wrong", "reload", timeoutMillis = 5_000)
			}
			exception.message shouldContain "authentication failed"
		} finally {
			server.close()
		}
	}

	test("an empty password is rejected before opening a socket") {
		shouldThrow<IllegalArgumentException> { Rcon.send("localhost", 1, "", "reload", timeoutMillis = 5_000) }
	}

	test("an oversized packet is refused instead of being buffered") {
		val server = startServer("secret") { "x".repeat(8_192) }

		try {
			shouldThrow<RconException> { Rcon.send("localhost", server.port, "secret", "reload", timeoutMillis = 5_000) }
		} finally {
			server.close()
		}
	}
})

private class FakeRconServer(private val expectedPassword: String, private val answer: (String) -> String) {
	private val socket = ServerSocket(0)
	private val commands = mutableListOf<String>()

	val port get() = socket.localPort
	val receivedCommands get() = commands.toList()

	fun start() = thread(isDaemon = true) {
		runCatching {
			socket.accept().use { client ->
				val input = DataInputStream(client.getInputStream())
				val output = DataOutputStream(client.getOutputStream())

				val (authId, _, password) = read(input)
				val authenticated = password == expectedPassword
				write(output, if (authenticated) authId else -1, AUTH_RESPONSE_TYPE, "")
				if (!authenticated) return@use

				val (commandId, _, command) = read(input)
				commands += command
				write(output, commandId, AUTH_RESPONSE_TYPE, answer(command))
			}
		}
	}

	fun close() = socket.close()

	private fun read(input: DataInputStream): Triple<Int, Int, String> {
		val lengthBytes = ByteArray(4).also(input::readFully)
		val length = ByteBuffer.wrap(lengthBytes).order(ByteOrder.LITTLE_ENDIAN).int
		val payload = ByteArray(length).also(input::readFully)
		val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
		val id = buffer.int
		val type = buffer.int
		return Triple(id, type, String(payload, 8, length - 10, Charsets.US_ASCII))
	}

	private fun write(output: DataOutputStream, id: Int, type: Int, body: String) {
		val payload = body.toByteArray(Charsets.US_ASCII)
		val buffer = ByteBuffer.allocate(12 + payload.size + 2).order(ByteOrder.LITTLE_ENDIAN)
		buffer.putInt(10 + payload.size)
		buffer.putInt(id)
		buffer.putInt(type)
		buffer.put(payload)
		buffer.put(0)
		buffer.put(0)
		output.write(buffer.array())
		output.flush()
	}

	private companion object {
		const val AUTH_RESPONSE_TYPE = 2
	}
}
