import java.io.File
import java.io.PrintWriter
import kotlin.test.Test

class TestGraphCreation {
    @Test
    fun test() {
        val words = File("./src/commonMain/resources/nouns.txt").readLines().toTypedArray()
        println(words)
        val graph = GraphBuilder().parseWords(words)
        val first = graph.getFirstFiveLetters()
        println(first.size)
        val last = graph.getLastFiveLetters()
        println(last.size)
        intersectTwoMaps(first, last)
    }
}

fun intersectTwoMaps(
    first: MutableMap<Pair<Char, Char>, MutableList<String>>,
    last: MutableMap<Pair<Char, Char>, MutableList<String>>,
    graph: Graph
) {
    val file = PrintWriter(File("res.txt").outputStream())

    for (key in first.keys) {
        if (last[key] == null)
            continue
        println("$key, ${first[key]!!.size}, ${last[key]!!.size}")

        for (i in first[key]!!) {
            for (j in last[key]!!) {
                file.write(i + j.substring(1, j.lastIndex))
                file.write("\n")
            }
        }
    }
    file.close()
}