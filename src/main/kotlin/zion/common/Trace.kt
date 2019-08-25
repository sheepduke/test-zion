package zion.common

import java.time.Instant

data class Trace(
        val source: Source,
        val startNode: String,
        val endNode: String,
        val startTime: Instant,
        val endTime: Instant)