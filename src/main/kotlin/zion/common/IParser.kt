package zion.common

interface IParser {

    fun parse(baseDir: String): List<Trace>
}