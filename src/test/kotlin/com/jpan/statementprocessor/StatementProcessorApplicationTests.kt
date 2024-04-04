package com.jpan.statementprocessor

import com.fasterxml.jackson.databind.ObjectMapper
import com.jpan.statementprocessor.dto.FailedStatementRecordDto
import com.jpan.statementprocessor.service.CustomerStatementService
import com.jpan.statementprocessor.service.FileDtoMapperService
import com.jpan.statementprocessor.util.*
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigInteger

@SpringBootTest(
	classes = [StatementProcessorApplication::class],
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class StatementProcessorApplicationTests {

	@Autowired
	lateinit var fileDtoMapperService: FileDtoMapperService

	@Autowired
	lateinit var customerStatementService: CustomerStatementService

	@Autowired
	lateinit var objectMapper: ObjectMapper

	@Test
	@Throws(Exception::class)
	fun processStatementCsvOk() {
		val validCsv = TestFileUtils.getMultipartFile(VALID_CSV)

		val recordDtos = fileDtoMapperService.mapToRecordDtos(validCsv)

		assert(recordDtos.size == 2)

		customerStatementService.processStatementRecords(recordDtos)
	}

	@Test
	@Throws(Exception::class)
	fun processStatementCsvNok() {
		val invalidCsv = TestFileUtils.getMultipartFile(INVALID_CSV)

		val recordDtos = fileDtoMapperService.mapToRecordDtos(invalidCsv)

		assert(recordDtos.size == 10)

		try {
			customerStatementService.processStatementRecords(recordDtos)
		} catch (ex: ConstraintViolationException) {
			assert(
				getFailedRecordsFromException(ex).containsAll(112806, 112806, 112806)
			)
		}
	}

	@Test
	@Throws(Exception::class)
	fun processStatementXmlOk() {
		val validXml = TestFileUtils.getMultipartFile(VALID_XML)

		val recordDtos = fileDtoMapperService.mapToRecordDtos(validXml)

		assert(recordDtos.size == 3)

		customerStatementService.processStatementRecords(recordDtos)
	}

	@Test
	@Throws(Exception::class)
	fun processStatementXmlNok() {
		val invalidXml = TestFileUtils.getMultipartFile(INVALID_XML)

		val recordDtos = fileDtoMapperService.mapToRecordDtos(invalidXml)

		assert(recordDtos.size == 10)

		try {
			customerStatementService.processStatementRecords(recordDtos)
		} catch (ex: ConstraintViolationException) {
			assert(
				getFailedRecordsFromException(ex).containsAll(131254, 192480)
			)
		}
	}

	private fun getFailedRecordsFromException(ex: ConstraintViolationException) = ex.constraintViolations.map {
		objectMapper.readValue(it.message, FailedStatementRecordDto::class.java)
	}

	private fun List<FailedStatementRecordDto>.containsAll(vararg references: Long) =
		this.map { it.reference }.containsAll(references.map { BigInteger.valueOf(it) })
}
