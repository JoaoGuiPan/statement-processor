package com.jpan.statementprocessor.service.mapper.factory

import com.jpan.statementprocessor.service.mapper.FileDtoMapper
import com.jpan.statementprocessor.util.getExtension
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class FileDtoMapperFactory(
    val fileDtoMappers: Map<String, FileDtoMapper>
) {
    fun getMapper(serviceId: String) = fileDtoMappers[serviceId]

    fun getMapper(statement: MultipartFile): FileDtoMapper {
        val extension = statement.getExtension()
        return getMapper(extension + "DtoMapper")!! // extensions are known, so this will never be null
    }
}