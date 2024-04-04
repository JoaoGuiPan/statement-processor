package com.jpan.statementprocessor.validator

import com.jpan.statementprocessor.dto.CustomerStatementRecordDto
import com.jpan.statementprocessor.dto.buildFrom
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger {}

private const val STATEMENT_VALIDATION_MESSAGE = "Statement records contain errors."
private const val END_BALANCE_INVALID = "End Balance must be equal to Start Balance plus/minus Mutation."
private const val REFERENCE_INVALID = "Transaction reference should be unique."

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

        context.disableDefaultConstraintViolation()

        value.forEach {
            if (endBalanceIsNotValid(it)) {
                isValid = false
                logger.debug { "End Balance is not valid on transaction ${it.reference}" }
                val failedRecord = buildFrom(it, END_BALANCE_INVALID)
                addErrorMessage(context, failedRecord.toString())
            }

            if(transactionReferenceIsDuplicated(value, it)) {
                isValid = false
                logger.debug { "Transaction reference ${it.reference} is duplicated" }
                val failedRecord = buildFrom(it, REFERENCE_INVALID)
                addErrorMessage(context, failedRecord.toString())
            }
        }

        return isValid
    }

    private fun addErrorMessage(context: ConstraintValidatorContext, templateErrorMessage: String) {
        context.buildConstraintViolationWithTemplate(templateErrorMessage).addConstraintViolation()
    }

    private fun endBalanceIsNotValid(recordDto: CustomerStatementRecordDto): Boolean {
        val calculatedEndBalance = recordDto.startBalance?.add(recordDto.mutation)
        return calculatedEndBalance?.equals(recordDto.endBalance) == false
    }

    private fun transactionReferenceIsDuplicated(allRecords: List<CustomerStatementRecordDto>,
                                                 recordDto: CustomerStatementRecordDto): Boolean {
        return allRecords.count { it.reference == recordDto.reference } > 1
    }
}