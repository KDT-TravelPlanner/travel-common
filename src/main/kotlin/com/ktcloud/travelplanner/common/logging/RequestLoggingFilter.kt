package com.ktcloud.travelplanner.common.logging

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.web.filter.OncePerRequestFilter
import kotlin.math.max

/** Register this filter explicitly in a service that does not provide its own request logging filter. */
class RequestLoggingFilter : OncePerRequestFilter() {
	private val requestIdGenerator = RequestIdGenerator()
	private val requestLogger = LoggerFactory.getLogger(RequestLoggingFilter::class.java)

	override fun doFilterInternal(
		request: HttpServletRequest,
		response: HttpServletResponse,
		filterChain: FilterChain,
	) {
		val requestId = requestIdGenerator.resolveOrGenerate(request.getHeader(RequestId.HEADER_NAME))
		val previousRequestId = MDC.get(RequestId.MDC_KEY)
		val startedAt = System.nanoTime()
		var failed = false

		RequestLoggingContext.setRequestId(request, requestId)
		MDC.put(RequestId.MDC_KEY, requestId)
		response.setHeader(RequestId.HEADER_NAME, requestId)

		try {
			filterChain.doFilter(request, response)
		} catch (throwable: Throwable) {
			failed = true
			throw throwable
		} finally {
			val status = if (failed && response.status < HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR
			} else {
				response.status
			}
			val durationMs = max(0, (System.nanoTime() - startedAt) / NANOSECONDS_PER_MILLISECOND)

			try {
				requestLogger.info(
					"event={} requestId={} method={} status={} durationMs={}",
					HTTP_REQUEST_COMPLETED_EVENT,
					requestId,
					request.method,
					status,
					durationMs,
				)
			} finally {
				if (previousRequestId == null) {
					MDC.remove(RequestId.MDC_KEY)
				} else {
					MDC.put(RequestId.MDC_KEY, previousRequestId)
				}
			}
		}
	}

	private companion object {
		const val HTTP_REQUEST_COMPLETED_EVENT = "HTTP_REQUEST_COMPLETED"
		const val NANOSECONDS_PER_MILLISECOND = 1_000_000
	}
}
