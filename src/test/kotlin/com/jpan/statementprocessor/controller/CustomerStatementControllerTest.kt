package com.jpan.statementprocessor.controller

import com.jpan.statementprocessor.service.CustomerStatementService
import com.jpan.statementprocessor.service.FileDtoMapperService
import com.jpan.statementprocessor.service.mapper.CsvDtoMapper
import com.jpan.statementprocessor.util.INVALID_CSV
import com.jpan.statementprocessor.util.INVALID_XML
import com.jpan.statementprocessor.util.TestFileUtils.getMultipartFile
import com.jpan.statementprocessor.util.VALID_CSV
import com.jpan.statementprocessor.util.VALID_XML
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.multipart

private const val INVALID_EXTENSION = "invalid.extension"

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

        mvc.multipart("/statements/customer/upload") {
            file(validCsv)
        }.andExpect {
            status { isOk() }
            content { string(CUSTOMER_STATEMENT_PROCESSED_SUCCESSFULLY) }
        }
    }

    @Test
    @Throws(Exception::class)
    fun processXmlOk() {

        val validXml = getMultipartFile(VALID_XML)

        whenever(fileDtoMapperServiceMock.mapToRecordDtos(validXml))
            .thenReturn(CsvDtoMapper().mapToDto(validXml))

        mvc.multipart("/statements/customer/upload") {
            file(validXml)
        }.andExpect {
            status { isOk() }
            content { string(CUSTOMER_STATEMENT_PROCESSED_SUCCESSFULLY) }
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

        mvc.multipart("/statements/customer/upload") {
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

        mvc.multipart("/statements/customer/upload") {
            file(invalidXml)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @Throws(Exception::class)
    fun processInvalidExtensionNok() {

        val invalidFile = getMultipartFile(INVALID_EXTENSION)

        mvc.multipart("/statements/customer/upload") {
            file(invalidFile)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @Throws(Exception::class)
    fun processInternalErrorNok() {

        val validXml = getMultipartFile(VALID_XML)

        whenever(fileDtoMapperServiceMock.mapToRecordDtos(validXml))
            .thenThrow(RuntimeException("Unexpected Error"))

        mvc.multipart("/statements/customer/upload") {
            file(validXml)
        }.andExpect {
            status { isInternalServerError() }
        }
    }
}