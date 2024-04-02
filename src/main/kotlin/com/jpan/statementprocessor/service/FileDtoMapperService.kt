package com.jpan.statementprocessor.service

import com.jpan.statementprocessor.dto.CustomerStatementRecordDto
import com.jpan.statementprocessor.service.mapper.factory.FileDtoMapperFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

private val logger = KotlinLogging.logger {}

@Service
class FileDtoMapperService(
    val fileDtoMapperFactory: FileDtoMapperFactory
) {
    fun mapToRecordDtos(statement: MultipartFile): List<CustomerStatementRecordDto> {
        val dtoMapper = fileDtoMapperFactory.getMapper(statement)
        val recordDtos = dtoMapper.mapToDto(statement)

        if (recordDtos.isNotEmpty()) {
            logger.debug {
                "Customer Statement Records successfully extracted from file, with the following references: ${
                    recordDtos.map { it.reference }.joinToString(",")
                }"
            }
        }

        return recordDtos
    }
}