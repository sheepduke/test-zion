package zion.common

import java.lang.IllegalArgumentException

class ParseError : IllegalArgumentException("Error during parsing")

enum class FailureStrategy {
    IGNORE_TRACE_ITEM,
    THROW_ERROR
}