package zion.sentinel

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import zion.common.Source
import zion.common.Trace
import java.io.FileReader
import java.time.*

data class Sentinel(var routeId: Int,
                    var node: String,
                    var index: Int,
                    var time: Instant)

fun recordToSentinel(record: CSVRecord): Sentinel {
    return Sentinel(routeId = record.get("route_id").toInt(),
            node = record.get("node"),
            index = record.get("index").toInt(),
            time = OffsetDateTime.parse(record.get("time")).toInstant())
}

fun sentinelsToTraces(sentinels: List<Sentinel>) : List<Trace> {
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

fun parse(baseDir: String): List<Trace> {
    val reader = FileReader("$baseDir/sentinels/routes.csv")
    val parser = CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader()
            .withIgnoreSurroundingSpaces()
            .withIgnoreEmptyLines()
            .withTrim())
    val sentinels = parser.records.map { recordToSentinel(it) }
    return sentinelsToTraces(sentinels)
}

fun main() {
    parse("input/")
            .forEach { println(it) }
}