package com.ktcloud.travelplanner.common.logging

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.MDC

object RequestLoggingContext {
	fun setRequestId(request: HttpServletRequest, requestId: String) {
		request.setAttribute(RequestId.REQUEST_ATTRIBUTE, requestId)
	}

	fun getRequestId(request: HttpServletRequest): String? =
		request.getAttribute(RequestId.REQUEST_ATTRIBUTE) as? String

	fun currentRequestId(): String? = MDC.get(RequestId.MDC_KEY)
}
