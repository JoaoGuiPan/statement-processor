package com.jpan.statementprocessor.dto

fun buildFrom(recordDto: CustomerStatementRecordDto, failedReason: String) = FailedStatementRecordDto(
    recordDto.reference,
    recordDto.description,
    failedReason
)