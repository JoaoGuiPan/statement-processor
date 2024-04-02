package com.jpan.statementprocessor.util

import kotlin.io.path.createTempFile
import org.apache.commons.io.FilenameUtils
import org.springframework.web.multipart.MultipartFile
import java.io.File


fun MultipartFile.isExtension(extensions: List<String>) = FilenameUtils.isExtension(this.originalFilename?.lowercase(), extensions)

fun MultipartFile.getExtension(): String = FilenameUtils.getExtension(this.originalFilename?.lowercase())

fun getTempCsvFile(failedRecords: String): File {
    val tempFile = createTempFile().toFile()
    tempFile.writeText(failedRecords)
    return tempFile
}

fun getTempCsvFile(failedRecords: List<String>): File {
    val recordString = if (failedRecords.isNotEmpty()) failedRecords.first() else ""
    return getTempCsvFile(recordString)
}
