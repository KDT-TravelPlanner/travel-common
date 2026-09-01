package com.ktcloud.travelplanner.common.logging

import java.util.UUID

class RequestIdGenerator {
	fun generate(): String = UUID.randomUUID().toString()

	/** Keeps a canonical UUID v4 request id and replaces every other value. */
	fun resolveOrGenerate(candidate: String?): String {
		val parsed = candidate?.let(::parseCanonicalUuidV4)
		return parsed?.toString() ?: generate()
	}

	private fun parseCanonicalUuidV4(candidate: String): UUID? {
		val parsed = try {
			UUID.fromString(candidate)
		} catch (_: IllegalArgumentException) {
			return null
		}

		return parsed.takeIf {
			it.toString() == candidate && it.version() == UUID_VERSION_4 && it.variant() == RFC_4122_VARIANT
		}
	}

	companion object {
		@Deprecated("Use RequestId.HEADER_NAME", ReplaceWith("RequestId.HEADER_NAME"))
		const val HEADER_NAME = RequestId.HEADER_NAME

		@Deprecated("Use RequestId.MDC_KEY", ReplaceWith("RequestId.MDC_KEY"))
		const val MDC_KEY = RequestId.MDC_KEY

		const val UUID_VERSION_4 = 4
		const val RFC_4122_VARIANT = 2
	}
}
