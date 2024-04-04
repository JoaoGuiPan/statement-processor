package com.jpan.statementprocessor.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.jpan.statementprocessor.dto.ErrorResponseDto
import com.jpan.statementprocessor.dto.FailedStatementRecordDto
import com.jpan.statementprocessor.dto.GenericErrorResponse
import com.jpan.statementprocessor.service.CustomerStatementService
import com.jpan.statementprocessor.service.FileDtoMapperService
import com.jpan.statementprocessor.validator.ValidFileExtension
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

private val logger = KotlinLogging.logger {}

private const val CUSTOMER_STATEMENT_PROCESSED_SUCCESSFULLY = "Customer Statement processed successfully."
private const val STARTED_PROCESSING_CUSTOMER_STATEMENT = "Started Processing Customer Statement."

@Validated
@RestController
@RequestMapping("statements")
class CustomerStatementController(
    val fileDtoMapperService: FileDtoMapperService,
    val customerStatementService: CustomerStatementService,
    val objectMapper: ObjectMapper
) {

    @Operation(
        summary = "Upload .csv or .xml Customer Statement file",
        description = "Customer Statement is processed and validated: All transaction references should be unique and the end balance of each record has to be valid.")
    @ApiResponses(value = [
        ApiResponse(
            description = "Report containing transactions that failed processing.",
            content = [
                Content(schema = Schema(implementation = FailedStatementRecordDto::class))
            ]
        ),
    ])
    @PostMapping(
        value = ["/customers/upload"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadCustomerStatement(
        @ValidFileExtension
        @RequestParam(required = true)
        @Parameter(
            description = "CSV or XML Customer Statement file to be uploaded",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE // to properly process files in swagger ui
                )
            ]
        ) file: MultipartFile): ResponseEntity<List<FailedStatementRecordDto>> {

        logger.info { STARTED_PROCESSING_CUSTOMER_STATEMENT }

        val recordDtos = fileDtoMapperService.mapToRecordDtos(file)

        customerStatementService.processStatementRecords(recordDtos)

        logger.info { CUSTOMER_STATEMENT_PROCESSED_SUCCESSFULLY }

        return ResponseEntity.ok(emptyList())
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralExceptions(ex: Exception, request: HttpServletRequest?): ResponseEntity<String> {
        logger.error { ex }
        return ResponseEntity.internalServerError().body(ex.message)
    }

    /**
     * handles the ConstraintViolationErrors added by FileExtensionValidator and CustomerStatementRecordsValidator
     */
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationErrors(
        ex: ConstraintViolationException,
        request: HttpServletRequest?
    ): ResponseEntity<List<GenericErrorResponse>> {
        val errorResponse = buildViolationErrorResponse(ex.constraintViolations)
        return ResponseEntity.badRequest().body(errorResponse)
    }

    private fun buildViolationErrorResponse(constraintViolations: MutableSet<ConstraintViolation<*>>): List<GenericErrorResponse> {

        val fileExtensionViolation = constraintViolations.find {
            it.propertyPath.toString() == "uploadCustomerStatement.file"
        }
        if (fileExtensionViolation != null) {
            return listOf(ErrorResponseDto(fileExtensionViolation.message))
        }

        val customerStatementRecordViolation = constraintViolations.find {
            it.propertyPath.toString() == "processStatementRecords.customerStatementRecordDtos"
        }
        if (customerStatementRecordViolation != null) {
            logger.error { "Constraint Violation Errors found in Customer Statement; Generating report." }
            return constraintViolations.map {
                objectMapper.readValue(it.message, FailedStatementRecordDto::class.java)
            }
        }

        return listOf(ErrorResponseDto("Constraint Violation Error."))
    }
}
