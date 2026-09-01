package com.ktcloud.travelplanner.common.logging

import jakarta.servlet.FilterChain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import java.util.UUID

class RequestLoggingFilterTest {
	private val filter = RequestLoggingFilter()

	@AfterEach
	fun clearMdc() {
		MDC.clear()
	}

	@Test
	fun `valid upstream UUID v4 is propagated to request MDC and response`() {
		val upstreamRequestId = UUID.randomUUID().toString()
		val request = MockHttpServletRequest("GET", "/api/profile").apply {
			addHeader(RequestId.HEADER_NAME, upstreamRequestId)
		}
		val response = MockHttpServletResponse()
		var requestIdInsideChain: String? = null
		var mdcInsideChain: String? = null

		filter.doFilter(request, response, FilterChain { servletRequest, _ ->
			requestIdInsideChain = RequestLoggingContext.getRequestId(servletRequest as MockHttpServletRequest)
			mdcInsideChain = RequestLoggingContext.currentRequestId()
		})

		assertEquals(upstreamRequestId, requestIdInsideChain)
		assertEquals(upstreamRequestId, mdcInsideChain)
		assertEquals(upstreamRequestId, response.getHeader(RequestId.HEADER_NAME))
		assertNull(MDC.get(RequestId.MDC_KEY))
	}

	@Test
	fun `invalid upstream value is replaced with UUID v4`() {
		val request = MockHttpServletRequest("GET", "/api/profile").apply {
			addHeader(RequestId.HEADER_NAME, "client-controlled-value")
		}
		val response = MockHttpServletResponse()

		filter.doFilter(request, response, FilterChain { _, _ -> })

		val generated = UUID.fromString(response.getHeader(RequestId.HEADER_NAME))
		assertEquals(4, generated.version())
		assertNotEquals("client-controlled-value", generated.toString())
	}

	@Test
	fun `MDC is restored when downstream throws`() {
		val request = MockHttpServletRequest("GET", "/api/failure")
		val response = MockHttpServletResponse()
		MDC.put(RequestId.MDC_KEY, "outer-request")

		assertThrows(IllegalStateException::class.java) {
			filter.doFilter(request, response, FilterChain { _, _ ->
				throw IllegalStateException("failure")
			})
		}

		assertEquals("outer-request", MDC.get(RequestId.MDC_KEY))
	}
}
