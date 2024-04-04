package com.jpan.statementprocessor.dto

import com.fasterxml.jackson.annotation.JsonRootName
import com.opencsv.bean.CsvBindByName
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Represents a single record
 *
 * Example CSV File contents:
 * Reference,Account Number,Description,Start Balance,Mutation,End Balance
 * 183398,NL56RABO0149876948,Clothes from Richard de Vries,33.34,+5.55,38.89
 *
 * Example XML File contents:
 * <records>
 *   <record reference="138932">
 *     <accountNumber>NL90ABNA0585647886</accountNumber>
 *     <description>Flowers for Richard Bakker</description>
 *     <startBalance>94.9</startBalance>
 *     <mutation>+14.63</mutation>
 *     <endBalance>109.53</endBalance>
 *   </record>
 * </records>
 *
 */
@JsonRootName("record")
class CustomerStatementRecordDto(
    @CsvBindByName(column = "Reference")
    @Schema(name = "Transaction Reference", example = "183398", required = true)
    var reference: BigInteger? = null,

    @CsvBindByName(column = "Account Number")
    @Schema(name = "Account Number", example = "NL56RABO0149876948", required = true)
    var accountNumber: String? = null,

    @CsvBindByName(column = "Description")
    @Schema(name = "Transaction Description", example = "Tickets for Erik")
    var description: String? = null,

    @CsvBindByName(column = "Start Balance")
    @Schema(name = "Start Balance", example = "50.00", required = true)
    var startBalance: BigDecimal? = null,

    @CsvBindByName(column = "Mutation")
    @Schema(name = "Mutation", example = "-30.00", required = true)
    var mutation: BigDecimal? = null,

    @CsvBindByName(column = "End Balance")
    @Schema(name = "Start Balance", example = "20.00", required = true)
    var endBalance: BigDecimal? = null
)
