package com.jpan.statementprocessor.controller

import com.jpan.statementprocessor.service.CustomerStatementService
import com.jpan.statementprocessor.service.FileDtoMapperService
import com.jpan.statementprocessor.util.getTempCsvFile
import com.jpan.statementprocessor.validator.ValidFileExtension
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.multipart.MultipartFile

private val logger = KotlinLogging.logger {}

const val CUSTOMER_STATEMENT_PROCESSED_SUCCESSFULLY = "Customer Statement processed successfully."

@Validated
@RestController
@RequestMapping("statements")
class CustomerStatementController(
    val fileDtoMapperService: FileDtoMapperService,
    val customerStatementService: CustomerStatementService,
) {

    @Operation(
        summary = "Upload .csv or .xml Customer Statement file",
        description = "Customer Statement is processed and validated: All transaction references should be unique and the end balance of each record has to be valid.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "All Customer Statement records are valid."),
        ApiResponse(responseCode = "400", description = "CSV Report containing transactions that failed processing."),
        ApiResponse(responseCode = "500", description = "Internal Server Error, exception stack trace."),
    ])
    @PostMapping(
        value = ["/customer/upload"],
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
        ) file: MultipartFile): ResponseEntity<String> {

        val recordDtos = fileDtoMapperService.mapToRecordDtos(file)

        customerStatementService.processStatementRecords(recordDtos)

        logger.debug { CUSTOMER_STATEMENT_PROCESSED_SUCCESSFULLY }

        return ResponseEntity.ok(CUSTOMER_STATEMENT_PROCESSED_SUCCESSFULLY)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralExceptions(ex: Exception, request: HttpServletRequest?): ResponseEntity<Array<StackTraceElement>> {
        logger.error { ex }
        return ResponseEntity
            .internalServerError()
            .body(ex.stackTrace)
    }

    @ExceptionHandler(HandlerMethodValidationException::class)
    fun handleMethodValidationErrors(ex: HandlerMethodValidationException, request: HttpServletRequest?) =
        ResponseEntity
            .badRequest()
            .body(ex.allErrors.map { it.defaultMessage })

    /**
     * handles the ConstraintViolationErrors added by CustomerStatementRecordsValidator
     */
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationErrors(ex: ConstraintViolationException, request: HttpServletRequest?): ResponseEntity<InputStreamResource> {
        logger.error { "Constraint Violation Errors found in Customer Statement; Generating CSV report." }
        val csvFile = getTempCsvFile(ex.constraintViolations.map { it.message })
        return ResponseEntity.badRequest()
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"failed-transactions.csv\"")
            .body(InputStreamResource(csvFile.inputStream()))
    }
}
