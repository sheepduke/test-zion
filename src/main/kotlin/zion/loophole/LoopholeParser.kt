package zion.loophole

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import zion.common.*
import java.io.FileReader

class LoopholeParser(private val failureStrategy: FailureStrategy) : IParser {

    private val mapper = ObjectMapper().registerModules(JavaTimeModule())

    override fun parse(baseDir: String): List<Trace> {
        var reader = FileReader("$baseDir/loopholes/routes.json")
        val routes = mapper.readValue<Routes>(reader).routes

        reader = FileReader("$baseDir/loopholes/node_pairs.json")
        val nodePairs = mapper.readValue<NodePairs>(reader).nodePairs

        return generateTraces(routes, nodePairs)
    }

    private fun generateTraces(routes: List<Route>, nodePairs: List<NodePair>): List<Trace> {
        val nodePairMap = nodePairs.associate { it.id to it }
        val result = mutableListOf<Trace>()
        routes.groupBy { it.routeId }
                .values
                .forEach { routeList ->
                    val sortedRoutes = routeList.sortedBy { it.startTime }
                    val firstRoute = sortedRoutes.first()
                    val lastRoute = sortedRoutes.last()
                    val firstNodePair = nodePairMap[firstRoute.nodePairId]
                    val lastNodePair = nodePairMap[lastRoute.nodePairId]
                    if (firstNodePair == null || lastNodePair == null) {
                        if (failureStrategy == FailureStrategy.THROW_ERROR) {
                            throw ParseError()
                        }
                    } else {
                        result.add(Trace(source = Source.LOOPHOLES,
                                startNode = firstNodePair.startNode,
                                endNode = lastNodePair.endNode,
                                startTime = firstRoute.startTime,
                                endTime = lastRoute.endTime))
                    }
                }
        return result
     }
}