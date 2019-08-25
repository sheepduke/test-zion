package zion.common

import org.apache.commons.csv.CSVFormat

interface IParser {

    fun parse(baseDir: String): List<Trace>
}

val CSV_FORMAT: CSVFormat = CSVFormat.DEFAULT
        .withFirstRecordAsHeader()
        .withIgnoreSurroundingSpaces()
        .withIgnoreEmptyLines()
        .withTrim()