package com.jpan.statementprocessor.service.mapper

import com.jpan.statementprocessor.dto.CustomerStatementRecordDto
import org.springframework.web.multipart.MultipartFile

interface FileDtoMapper {
    fun mapToDto(statement: MultipartFile): List<CustomerStatementRecordDto>
}