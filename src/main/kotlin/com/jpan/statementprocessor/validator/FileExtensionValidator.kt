package com.jpan.statementprocessor.validator

import com.jpan.statementprocessor.util.isExtension
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.web.multipart.MultipartFile
import kotlin.reflect.KClass

@Constraint(validatedBy = [FileExtensionValidator::class])
@Target(
    AnnotationTarget.VALUE_PARAMETER
)
@Retention(
    AnnotationRetention.RUNTIME
)
@MustBeDocumented
annotation class ValidFileExtension(
    val message: String = "Supported file extensions are .csv or .xml",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Any>> = []
)

class FileExtensionValidator: ConstraintValidator<ValidFileExtension, MultipartFile> {
    override fun isValid(value: MultipartFile, context: ConstraintValidatorContext?)
        =  value.isExtension(mutableListOf("csv", "xml"))
}