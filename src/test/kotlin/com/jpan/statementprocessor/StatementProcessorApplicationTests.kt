package com.jpan.statementprocessor

import com.jpan.statementprocessor.service.CustomerStatementService
import com.jpan.statementprocessor.service.FileDtoMapperService
import com.jpan.statementprocessor.util.*
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
	classes = [StatementProcessorApplication::class],
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class StatementProcessorApplicationTests {

	@Autowired
	lateinit var fileDtoMapperService: FileDtoMapperService

	@Autowired
	lateinit var customerStatementService: CustomerStatementService

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
			assert(ex.message == "processStatementRecords.customerStatementRecordDtos: Reference,Description,Failed Reason\n" +
					"112806,Subscription from Jan Dekker,Transaction reference should be unique.\n" +
					"112806,Subscription from Dani�l Theu�,Transaction reference should be unique.\n" +
					"112806,Subscription for Rik Dekker,Transaction reference should be unique.\n")
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
			assert(ex.message == "processStatementRecords.customerStatementRecordDtos: Reference,Description,Failed Reason\n" +
					"131254,Candy from Vincent de Vries,End Balance must be equal to Start Balance plus/minus Mutation.\n" +
					"192480,Subscription for Erik de Vries,End Balance must be equal to Start Balance plus/minus Mutation.\n")
		}
	}
}
