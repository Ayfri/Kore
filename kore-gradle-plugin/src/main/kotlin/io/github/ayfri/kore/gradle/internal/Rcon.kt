package io.github.ayfri.kore.gradle.internal

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder

/** Thrown when the server refuses the password or answers with something unusable. */
class RconException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Minimal Source RCON client, enough to send a single command such as `reload` to a running Minecraft server.
 *
 * Protocol: https://developer.valvesoftware.com/wiki/Source_RCON_Protocol
 */
object Rcon {
	private const val TYPE_AUTH = 3
	private const val TYPE_AUTH_RESPONSE = 2
	private const val TYPE_COMMAND = 2

	/** Header is 4 bytes id + 4 bytes type, body is null-terminated and followed by an empty null-terminated string. */
	private const val HEADER_SIZE = 8
	private const val TRAILER_SIZE = 2

	private const val AUTH_ID = 1
	private const val COMMAND_ID = 2
	private const val AUTH_FAILURE_ID = -1

	/** Answers larger than this are almost certainly a desynchronized stream rather than a real response. */
	private const val MAX_PACKET_SIZE = 4_096 + HEADER_SIZE + TRAILER_SIZE

	fun send(host: String, port: Int, password: String, command: String, timeoutMillis: Int): String {
		require(password.isNotEmpty()) { "An RCON password is required, the server rejects empty ones." }

		Socket().use { socket ->
			socket.connect(InetSocketAddress(host, port), timeoutMillis)
			socket.soTimeout = timeoutMillis

			val output = DataOutputStream(socket.getOutputStream())
			val input = DataInputStream(socket.getInputStream())

			write(output, AUTH_ID, TYPE_AUTH, password)
			val (authId, authType) = readHeader(input)
			if (authId == AUTH_FAILURE_ID) throw RconException("RCON authentication failed for $host:$port, check the password.")
			if (authType != TYPE_AUTH_RESPONSE) throw RconException("Unexpected RCON auth response type $authType from $host:$port.")

			write(output, COMMAND_ID, TYPE_COMMAND, command)
			return readBody(input)
		}
	}

	private fun write(output: DataOutputStream, id: Int, type: Int, body: String) {
		val payload = body.toByteArray(Charsets.US_ASCII)
		val buffer = ByteBuffer.allocate(4 + HEADER_SIZE + payload.size + TRAILER_SIZE).order(ByteOrder.LITTLE_ENDIAN)
		buffer.putInt(HEADER_SIZE + payload.size + TRAILER_SIZE)
		buffer.putInt(id)
		buffer.putInt(type)
		buffer.put(payload)
		buffer.put(0)
		buffer.put(0)
		output.write(buffer.array())
		output.flush()
	}

	/** Reads one packet and returns its id and type, discarding the body. */
	private fun readHeader(input: DataInputStream): Pair<Int, Int> {
		val payload = readPacket(input)
		val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
		return buffer.int to buffer.int
	}

	/** Reads one packet and returns its body, stripping the two trailing null bytes. */
	private fun readBody(input: DataInputStream): String {
		val payload = readPacket(input)
		return String(payload, HEADER_SIZE, payload.size - HEADER_SIZE - TRAILER_SIZE, Charsets.US_ASCII)
	}

	private fun readPacket(input: DataInputStream): ByteArray {
		val lengthBytes = ByteArray(4)
		input.readFully(lengthBytes)
		val length = ByteBuffer.wrap(lengthBytes).order(ByteOrder.LITTLE_ENDIAN).int

		if (length < HEADER_SIZE + TRAILER_SIZE || length > MAX_PACKET_SIZE) {
			throw RconException("Received an out-of-range RCON packet of $length bytes.")
		}

		return ByteArray(length).also(input::readFully)
	}
}
