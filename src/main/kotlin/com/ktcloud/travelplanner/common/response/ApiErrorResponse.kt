package com.ktcloud.travelplanner.common.response

import com.fasterxml.jackson.annotation.JsonInclude

data class FieldErrorResponse(
	val field: String,
	val reason: String,
)

data class ApiErrorResponse(
	val code: String,
	val message: String,
	val requestId: String,
	@JsonInclude(JsonInclude.Include.NON_NULL)
	val fieldErrors: List<FieldErrorResponse>? = null,
) {
	companion object {
		fun of(
			code: String,
			message: String,
			requestId: String,
			fieldErrors: List<FieldErrorResponse>? = null,
		): ApiErrorResponse = ApiErrorResponse(
			code = code,
			message = message,
			requestId = requestId,
			fieldErrors = fieldErrors?.sortedBy(FieldErrorResponse::field),
		)
	}
}
