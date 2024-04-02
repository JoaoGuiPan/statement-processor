package com.jpan.statementprocessor.service.mapper

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.jpan.statementprocessor.dto.CustomerStatementRecordDto
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service(XmlDtoMapper.SERVICE_ID)
class XmlDtoMapper: FileDtoMapper {

    private val xmlMapper = XmlMapper()

    companion object {
        const val SERVICE_ID = "xmlDtoMapper"
    }

    override fun mapToDto(statement: MultipartFile): List<CustomerStatementRecordDto> {
        val recordDtos = xmlMapper
            .readValue(statement.inputStream, object : TypeReference<List<CustomerStatementRecordDto>?>() {})
        return recordDtos ?: emptyList()
    }
}