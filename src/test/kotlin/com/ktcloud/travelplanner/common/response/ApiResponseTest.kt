package com.ktcloud.travelplanner.common.response

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ApiResponseTest {
	@Test
	fun `success wraps response data`() {
		val response = ApiResponse.success("identity")

		assertEquals("identity", response.data)
	}
}
