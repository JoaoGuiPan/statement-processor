package com.jpan.statementprocessor.service.mapper

import com.jpan.statementprocessor.dto.CustomerStatementRecordDto
import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service(CsvDtoMapper.SERVICE_ID)
class CsvDtoMapper: FileDtoMapper {

    companion object {
        const val SERVICE_ID = "csvDtoMapper"
    }

    override fun mapToDto(statement: MultipartFile): List<CustomerStatementRecordDto> {
        var customerStatementRecordDtos: MutableList<CustomerStatementRecordDto>

        statement.inputStream.bufferedReader().use {
            val cb: CsvToBean<CustomerStatementRecordDto> = CsvToBeanBuilder<CustomerStatementRecordDto>(it)
                .withType(CustomerStatementRecordDto::class.java)
                .withIgnoreEmptyLine(true)
                .build()
            customerStatementRecordDtos = cb.parse()
        }

        return customerStatementRecordDtos
    }
}