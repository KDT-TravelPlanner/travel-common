package com.ktcloud.travelplanner.common.response

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class ApiErrorResponseTest {
	private val objectMapper = jacksonObjectMapper()

	@Test
	fun `field errors are sorted to keep the response deterministic`() {
		val response = ApiErrorResponse.of(
			code = "VALIDATION_ERROR",
			message = "요청값이 올바르지 않습니다.",
			requestId = "request-1",
			fieldErrors = listOf(
				FieldErrorResponse("nickname", "필수입니다."),
				FieldErrorResponse("email", "형식이 올바르지 않습니다."),
			),
		)

		assertEquals(listOf("email", "nickname"), response.fieldErrors?.map { it.field })
	}

	@Test
	fun `field errors are omitted from non-validation JSON`() {
		val response = ApiErrorResponse.of(
			code = "RESOURCE_NOT_FOUND",
			message = "찾을 수 없습니다.",
			requestId = "request-2",
		)

		val json = objectMapper.writeValueAsString(response)

		assertFalse(json.contains("fieldErrors"))
	}
}
