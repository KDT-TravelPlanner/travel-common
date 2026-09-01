package com.ktcloud.travelplanner.common.logging

object RequestId {
	const val HEADER_NAME = "X-Request-Id"
	const val MDC_KEY = "requestId"
	const val REQUEST_ATTRIBUTE = "com.ktcloud.travelplanner.common.logging.requestId"
}
