package com.jpan.statementprocessor.util

import org.apache.commons.io.FilenameUtils
import org.springframework.web.multipart.MultipartFile


fun MultipartFile.isExtension(extensions: List<String>) = FilenameUtils.isExtension(this.originalFilename?.lowercase(), extensions)

fun MultipartFile.getExtension(): String = FilenameUtils.getExtension(this.originalFilename?.lowercase())
