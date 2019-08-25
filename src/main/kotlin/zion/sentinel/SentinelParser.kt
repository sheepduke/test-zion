package zion.sentinel

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import zion.common.IParser
import zion.common.ParseError
import zion.common.Source
import zion.common.Trace
import java.io.FileReader
import java.time.OffsetDateTime

class SentinelParser : IParser {

    override fun parse(baseDir: String): List<Trace> {
        val reader = FileReader("$baseDir/sentinels/routes.csv")
        val parser = CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader()
                .withIgnoreSurroundingSpaces()
                .withIgnoreEmptyLines()
                .withTrim())
        val sentinels = parser.records.map { recordToSentinel(it) }
        return sentinelsToTraces(sentinels)
    }

    private fun recordToSentinel(record: CSVRecord): Entities {
        return try {
            Entities(routeId = record.get("route_id"),
                    node = record.get("node"),
                    index = record.get("index"),
                    time = OffsetDateTime.parse(record.get("time")).toInstant())
        } catch (e: IllegalArgumentException) {
            throw ParseError()
        }
    }

    private fun sentinelsToTraces(sentinels: List<Entities>) : List<Trace> {
        return sentinels.groupBy { it.routeId }
                .values
                .map { item -> item.sortedBy { it.index } }
                .map {
                    val firstNode =  it.first()
                    val lastNode = it.last()
                    Trace(source = Source.SENTINELS,
                            startNode = firstNode.node,
                            endNode = lastNode.node,
                            startTime = firstNode.time,
                            endTime = lastNode.time)
                }
    }
}