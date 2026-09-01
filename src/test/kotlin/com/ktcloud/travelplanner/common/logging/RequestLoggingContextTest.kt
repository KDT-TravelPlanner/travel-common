package com.ktcloud.travelplanner.common.logging

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.springframework.mock.web.MockHttpServletRequest

class RequestLoggingContextTest {
	@AfterEach
	fun clearMdc() {
		MDC.clear()
	}

	@Test
	fun `request and MDC request ids can be read consistently`() {
		val request = MockHttpServletRequest()

		assertNull(RequestLoggingContext.getRequestId(request))
		assertNull(RequestLoggingContext.currentRequestId())

		RequestLoggingContext.setRequestId(request, "request-1")
		MDC.put(RequestId.MDC_KEY, "request-1")

		assertEquals("request-1", RequestLoggingContext.getRequestId(request))
		assertEquals("request-1", RequestLoggingContext.currentRequestId())
	}
}
