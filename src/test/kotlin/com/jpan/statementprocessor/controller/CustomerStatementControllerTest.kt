package com.jpan.statementprocessor.controller

import com.jpan.statementprocessor.dto.ErrorResponseDto
import com.jpan.statementprocessor.service.CustomerStatementService
import com.jpan.statementprocessor.service.FileDtoMapperService
import com.jpan.statementprocessor.service.mapper.CsvDtoMapper
import com.jpan.statementprocessor.util.INVALID_CSV
import com.jpan.statementprocessor.util.INVALID_XML
import com.jpan.statementprocessor.util.TestFileUtils.getMultipartFile
import com.jpan.statementprocessor.util.VALID_CSV
import com.jpan.statementprocessor.util.VALID_XML
import com.jpan.statementprocessor.validator.INVALID_EXTENSION_ERROR
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.multipart

private const val INVALID_EXTENSION = "invalid.extension"

private const val STATEMENTS_CUSTOMERS_UPLOAD = "/statements/customers/upload"

@WebMvcTest(CustomerStatementController::class)
class CustomerStatementControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    lateinit var fileDtoMapperServiceMock: FileDtoMapperService

    @MockBean
    lateinit var customerStatementServiceMock: CustomerStatementService

    @Test
    @Throws(Exception::class)
    fun processCsvOk() {

        val validCsv = getMultipartFile(VALID_CSV)

        whenever(fileDtoMapperServiceMock.mapToRecordDtos(validCsv))
            .thenReturn(CsvDtoMapper().mapToDto(validCsv))

        mvc.multipart(STATEMENTS_CUSTOMERS_UPLOAD) {
            file(validCsv)
        }.andExpect {
            status { isOk() }
            content { emptyList<Any>() }
        }
    }

    @Test
    @Throws(Exception::class)
    fun processXmlOk() {

        val validXml = getMultipartFile(VALID_XML)

        whenever(fileDtoMapperServiceMock.mapToRecordDtos(validXml))
            .thenReturn(CsvDtoMapper().mapToDto(validXml))

        mvc.multipart(STATEMENTS_CUSTOMERS_UPLOAD) {
            file(validXml)
        }.andExpect {
            status { isOk() }
            content { emptyList<Any>() }
        }
    }

    @Test
    @Throws(Exception::class)
    fun processCsvNok() {

        val invalidCsv = getMultipartFile(INVALID_CSV)
        val records = CsvDtoMapper().mapToDto(invalidCsv)

        whenever(fileDtoMapperServiceMock.mapToRecordDtos(invalidCsv))
            .thenReturn(records)

        whenever(customerStatementServiceMock.processStatementRecords(records))
            .thenThrow(ConstraintViolationException(setOf()))

        mvc.multipart(STATEMENTS_CUSTOMERS_UPLOAD) {
            file(invalidCsv)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @Throws(Exception::class)
    fun processXmlNok() {

        val invalidXml = getMultipartFile(INVALID_XML)
        val records = CsvDtoMapper().mapToDto(invalidXml)

        whenever(fileDtoMapperServiceMock.mapToRecordDtos(invalidXml))
            .thenReturn(records)

        whenever(customerStatementServiceMock.processStatementRecords(records))
            .thenThrow(ConstraintViolationException(setOf()))

        mvc.multipart(STATEMENTS_CUSTOMERS_UPLOAD) {
            file(invalidXml)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @Throws(Exception::class)
    fun processInvalidExtensionNok() {

        val invalidFile = getMultipartFile(INVALID_EXTENSION)

        mvc.multipart(STATEMENTS_CUSTOMERS_UPLOAD) {
            file(invalidFile)
        }.andExpect {
            status { isBadRequest() }
            content { listOf(ErrorResponseDto(INVALID_EXTENSION_ERROR)) }
        }
    }

    @Test
    @Throws(Exception::class)
    fun processInternalErrorNok() {

        val validXml = getMultipartFile(VALID_XML)

        whenever(fileDtoMapperServiceMock.mapToRecordDtos(validXml))
            .thenThrow(RuntimeException("Unexpected Error"))

        mvc.multipart(STATEMENTS_CUSTOMERS_UPLOAD) {
            file(validXml)
        }.andExpect {
            status { isInternalServerError() }
        }
    }
}