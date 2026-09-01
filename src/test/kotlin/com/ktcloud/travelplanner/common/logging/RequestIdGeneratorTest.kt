package com.ktcloud.travelplanner.common.logging

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import java.util.UUID

class RequestIdGeneratorTest {
	private val generator = RequestIdGenerator()

	@Test
	fun `generate returns a new RFC 4122 UUID v4`() {
		val first = UUID.fromString(generator.generate())
		val second = UUID.fromString(generator.generate())

		assertEquals(4, first.version())
		assertEquals(2, first.variant())
		assertEquals(4, second.version())
		assertNotEquals(first, second)
	}

	@Test
	fun `resolve keeps only canonical UUID v4 values`() {
		val uuidV4 = generator.generate()

		assertEquals(uuidV4, generator.resolveOrGenerate(uuidV4))
		assertNotEquals(uuidV4.uppercase(), generator.resolveOrGenerate(uuidV4.uppercase()))
		assertNotEquals(UUID(0, 0).toString(), generator.resolveOrGenerate(UUID(0, 0).toString()))
		UUID.fromString(generator.resolveOrGenerate("not-a-uuid"))
		UUID.fromString(generator.resolveOrGenerate(null))
	}

	@Test
	fun `header name is stable`() {
		assertEquals("X-Request-Id", RequestId.HEADER_NAME)
	}
}
