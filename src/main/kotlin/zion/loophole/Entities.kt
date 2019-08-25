package zion.loophole

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class NodePair(
        @JsonProperty("id")
        var id: String,

        @JsonProperty("start_node")
        var startNode: String,

        @JsonProperty("end_node")
        var endNode: String)

data class NodePairs(
        @JsonProperty("node_pairs")
        var nodePairs: List<NodePair>)

data class Route(
        @JsonProperty("route_id")
        var routeId: String,

        @JsonProperty("node_pair_id")
        var nodePairId: String,

        @JsonProperty("start_time")
        var startTime: Instant,

        @JsonProperty("end_time")
        var endTime: Instant)

data class Routes(
        var routes: List<Route> = emptyList())
