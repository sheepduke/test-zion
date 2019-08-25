package zion.sniffer

import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import zion.common.*
import java.io.FileReader
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class SnifferParser(private val failureStrategy: FailureStrategy) : IParser {

    override fun parse(baseDir: String): List<Trace> {
        val baseDir2 = "$baseDir/sniffers/"

        // Read and parse basic data.
        var reader = FileReader("$baseDir2/sequences.csv")
        var parser = CSVParser(reader, CSV_FORMAT)
        val sequences = parser.records.map { recordToSequence(it) }

        reader = FileReader("$baseDir2/routes.csv")
        parser = CSVParser(reader, CSV_FORMAT)
        val routes = parser.records.map { recordToRoute(it) }
                .associate { it.id to it }

        reader = FileReader("$baseDir2/node_times.csv")
        parser = CSVParser(reader, CSV_FORMAT)
        val nodeTimes = parser.records.map { recordToNodeTime(it) }
                .associate { it.id to it }

        // Construct trace list.
        val result = mutableListOf<Trace>()
        sequences.forEach {
            val route = routes[it.routeId]
            val nodeTime = nodeTimes[it.nodeTimeId]
            if (route == null || nodeTime == null) {
                if (failureStrategy == FailureStrategy.THROW_ERROR) {
                    throw ParseError()
                }
            } else {
                result.add(Trace(source = Source.SNIFFERS,
                        startNode = nodeTime.startNode,
                        endNode = nodeTime.endNode,
                        startTime = route.time,
                        endTime = route.time.plusMillis(nodeTime.duration)))
            }
        }

        return result
    }

    /**
     * Convert given CSVRecord to Sequence.
     */
    private fun recordToSequence(record: CSVRecord): Sequence {
        return Sequence(routeId = record["route_id"],
                nodeTimeId = record["node_time_id"])
    }

    /**
     * Convert given CSVRecord to Route.
     */
    private fun recordToRoute(record: CSVRecord): Route {
        var timeString = record["time"] + " " + record["time_zone"]
        timeString = timeString.replace("±", "+")

        val date = ZonedDateTime.parse(timeString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss z"))

        return Route(id = record["route_id"],
                time = date.toInstant())
    }

    /**
     * Convert given CSVRecord to NodeTime.
     */
    private fun recordToNodeTime(record: CSVRecord): NodeTime {
        return NodeTime(id = record["node_time_id"],
                startNode = record["start_node"],
                endNode = record["end_node"],
                duration = record["duration_in_milliseconds"].toLong())
    }
}

fun main() {
    SnifferParser(FailureStrategy.IGNORE_TRACE_ITEM).parse("input")
            .forEach { println(it) }
}