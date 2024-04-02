package com.jpan.statementprocessor.validator

import com.jpan.statementprocessor.dto.CustomerStatementRecordDto
import com.jpan.statementprocessor.util.buildRecordMessageTemplate
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger {}

private const val ERROR_CSV_HEADER = "Reference,Description,Failed Reason\n"
private const val STATEMENT_VALIDATION_MESSAGE = "Statement records contain errors."

@Constraint(validatedBy = [CustomerStatementRecordsValidator::class])
@Target(
    AnnotationTarget.VALUE_PARAMETER
)
@Retention(
    AnnotationRetention.RUNTIME
)
@MustBeDocumented
annotation class ValidateStatementRecords(
    val message: String = STATEMENT_VALIDATION_MESSAGE,
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Any>> = [],
)

class CustomerStatementRecordsValidator: ConstraintValidator<ValidateStatementRecords, List<CustomerStatementRecordDto>> {

    override fun isValid(value: List<CustomerStatementRecordDto>, context: ConstraintValidatorContext): Boolean {

        var isValid = true

        var templateErrorMessage = ERROR_CSV_HEADER

        value.forEach {
            if (endBalanceIsNotValid(it)) {
                logger.debug { "End Balance is not valid on transaction ${it.reference}" }
                isValid = false
                templateErrorMessage += buildRecordMessageTemplate(it, "End Balance must be equal to Start Balance plus/minus Mutation.")
            }
        }

        val duplicateOccurrences = getDuplicateTransactions(value)
        if (duplicateOccurrences.isNotEmpty()) {
            isValid = false
            duplicateOccurrences.forEach {
                logger.debug { "Transaction reference ${it.reference} is duplicated" }
                templateErrorMessage += buildRecordMessageTemplate(it, "Transaction reference should be unique.")
            }
        }

        if (!isValid) {
            addErrorMessage(context, templateErrorMessage)
        }

        return isValid
    }

    private fun addErrorMessage(context: ConstraintValidatorContext, templateErrorMessage: String) {
        context.disableDefaultConstraintViolation()
        context
            .buildConstraintViolationWithTemplate(templateErrorMessage)
            .addConstraintViolation()
    }

    private fun endBalanceIsNotValid(recordDto: CustomerStatementRecordDto): Boolean {
        val calculatedEndBalance = recordDto.startBalance?.add(recordDto.mutation)
        return calculatedEndBalance?.equals(recordDto.endBalance) == false
    }

    private fun getDuplicateTransactions(records: List<CustomerStatementRecordDto>): List<CustomerStatementRecordDto> {
        val duplicateTransactions = ArrayList<CustomerStatementRecordDto>()

        val duplicatesGroupedBy = records.groupingBy {
            it.reference
        }.eachCount().filter { it.value > 1 }

        duplicatesGroupedBy.keys.forEach { reference ->
            records
                .filter { it.reference == reference }
                .forEach { duplicateTransactions.addLast(it) }
        }

        return duplicateTransactions
    }
}