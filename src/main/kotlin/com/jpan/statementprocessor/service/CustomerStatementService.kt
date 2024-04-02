package com.jpan.statementprocessor.service

import com.jpan.statementprocessor.dto.CustomerStatementRecordDto
import com.jpan.statementprocessor.validator.ValidateStatementRecords
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

private val logger = KotlinLogging.logger {}

@Service
@Validated
class CustomerStatementService {
    fun processStatementRecords(@ValidateStatementRecords customerStatementRecordDtos: List<CustomerStatementRecordDto>) {
        logger.info { "Customer Statement Records are valid." }
        // this would be a good place for further processing, saving to the database, sending notifications, adding to queues, etc
    }
}
