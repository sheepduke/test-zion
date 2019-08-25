package zion.sniffer

import java.time.Instant

data class Route(
        val id: String,
        val time: Instant)

data class NodeTime(
        val id: String,
        val startNode: String,
        val endNode: String,
        val duration: Long
)

data class Sequence(
        val routeId: String,
        val nodeTimeId: String
)