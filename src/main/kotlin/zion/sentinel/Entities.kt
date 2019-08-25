package zion.sentinel

import java.time.Instant

data class Entities(var routeId: String,
                    var node: String,
                    var index: String,
                    var time: Instant)
