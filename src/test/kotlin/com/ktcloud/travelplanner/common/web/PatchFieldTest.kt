package com.ktcloud.travelplanner.common.web

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class PatchFieldTest {
	private val objectMapper = jacksonObjectMapper()

	@Test
	fun `omitted null and present values remain distinguishable`() {
		val omitted = objectMapper.readValue<UpdateRequest>("{}")
		val explicitNull = objectMapper.readValue<UpdateRequest>("""{"nickname":null}""")
		val present = objectMapper.readValue<UpdateRequest>("""{"nickname":"traveler"}""")

		assertSame(PatchField.Absent, omitted.nickname)
		assertEquals(PatchField.Present<String>(null), explicitNull.nickname)
		assertEquals(PatchField.Present("traveler"), present.nickname)
	}

	private data class UpdateRequest(
		val nickname: PatchField<String> = PatchField.Absent,
	)
}
