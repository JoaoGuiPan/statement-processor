package com.jpan.statementprocessor.util

import org.apache.commons.io.IOUtils
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile

const val VALID_CSV = "valid-records.csv"
const val INVALID_CSV = "invalid-records.csv"
const val VALID_XML = "valid-records.xml"
const val INVALID_XML = "invalid-records.xml"

object TestFileUtils {
    fun getMultipartFile(filename: String): MockMultipartFile {
        val bytes = IOUtils.toByteArray(javaClass.getClassLoader().getResourceAsStream(filename))
        return MockMultipartFile("file", filename, MediaType.MULTIPART_FORM_DATA_VALUE, bytes)
    }
}