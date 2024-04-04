package com.jpan.statementprocessor.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigInteger

class FailedStatementRecordDto(
    @Schema(name = "Transaction Reference", example = "183398", required = true)
    var reference: BigInteger? = null,

    @Schema(name = "Transaction Description", example = "Tickets for Erik")
    var description: String? = null,

    @Schema(name = "Failed Reason", example = "Transaction reference should be unique.", required = true)
    var failedReason: String? = null
): GenericErrorResponse {
    override fun toString(): String {
        return "{ " +
            "\"reference\": \"$reference\", " +
            "\"description\": \"$description\", " +
            "\"failedReason\": \"$failedReason\" " +
        "}"
    }
}