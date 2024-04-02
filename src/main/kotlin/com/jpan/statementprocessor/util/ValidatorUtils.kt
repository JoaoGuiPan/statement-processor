package com.jpan.statementprocessor.util

import com.jpan.statementprocessor.dto.CustomerStatementRecordDto

fun buildRecordMessageTemplate(recordDto: CustomerStatementRecordDto, message: String): String {
    val description = recordDto.description
    return ("${recordDto.reference},"
            + if (description == null) "," else "$description,"
            + "$message\n")
}
