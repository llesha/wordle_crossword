import java.io.File
import java.io.PrintWriter
import java.util.*
import kotlin.test.Test

class TestGraphCreation {
    @Test
    fun test() {
        val words = File("./src/commonMain/resources/nouns.txt").readLines().toTypedArray()
        println(words)
        val graph = GraphBuilder().parseWords(words)
        val res = graph.getFourLetters()
        getFShaped(graph, res)
    }

    @Test
    fun toSet() {
        val opened = Scanner(File("res.txt").inputStream())
        val set = mutableSetOf<String>()
        while (opened.hasNextLine()) {
            set.add(opened.nextLine())
        }
        set.remove("я")
        opened.close()
        val closed = PrintWriter(File("res_set.txt").outputStream())
        for (i in set) {
            closed.write(i)
            closed.write("\n")
        }
        closed.close()
    }

    @Test
    fun calcLines() {
        val file = File("res_set.txt")
        println(file.readLines().size)
    }

    @Test
    fun makeSquardle() {
        val words = File("./src/commonMain/resources/nouns.txt").readLines().toTypedArray()
        val graph = GraphBuilder().parseWords(words)
        val threeWordKeys = graph.getAllTripleCombinations()
        for (i in 0..5) {
            var iter = 0
            var cross = ""
            var reducedCrossword: String?
            do {
                val rndString = makeRandom()
                reducedCrossword = makeCrossword(rndString, graph, threeWordKeys)
                iter++
            } while (reducedCrossword == null)
            println(reducedCrossword)
            println(iter)
//            val column1 = threeWordKeys[Triple(reducedCrossword[0], reducedCrossword[3], reducedCrossword[6])]
//            val column2 = threeWordKeys[Triple(reducedCrossword[1], reducedCrossword[4], reducedCrossword[7])]
//            val column3 = threeWordKeys[Triple(reducedCrossword[2], reducedCrossword[5], reducedCrossword[8])]
//            println(reducedCrossword)
//            cross += threeWordKeys[Triple(reducedCrossword[0], reducedCrossword[1], reducedCrossword[2])]
//            cross += "${column1!![1]} ${column2!![1]} ${column3!![1]}\n"
//            cross += threeWordKeys[Triple(reducedCrossword[3], reducedCrossword[4], reducedCrossword[5])]
//            cross += "${column1[3]} ${column2[3]} ${column3[3]}\n"
//            cross += threeWordKeys[Triple(reducedCrossword[6], reducedCrossword[7], reducedCrossword[8])]
//            println(cross)
            iter = 0
            cross = ""
        }
    }

    fun makeRandom(): String {
        var res = ""
        for (i in 0 until 5) {
            res += Char(rnd.nextInt('а'.code, 'я'.code))
        }
        return res
    }

    fun makeCrossword(
        key: String,
        graph: Graph,
        threeWordKeys: MutableMap<Triple<Char, Char, Char>, List<String>>
    ): String? {
        val rightUpper = getCornerLetter(key[0], graph, key[1], key[4]) ?: return null
        val rightMiddle = (getMiddleLetter(key[2], graph, key[3], rightUpper) ?: return null).random(rnd)
        val leftDown = getCornerLetter(key[0], graph, key[2], key[4]) ?: return null
        val downMiddle = (getMiddleLetter(key[1], graph, key[3], leftDown) ?: return null).random(rnd)

        val possibleRightCorner = rightUpper.map { it[4] }.toSet()
        var rightLetter = '-'
        for (i in possibleRightCorner) {
            if (Triple(i, rightMiddle[4], key[4]) in threeWordKeys) {
                rightLetter = i
                break
            }
        }

        val possibleLeftCorner = leftDown.map { it[4] }.toSet()
        var leftLetter = '-'
        for (i in possibleLeftCorner) {
            if (Triple(i, downMiddle[4], key[4]) in threeWordKeys) {
                leftLetter = i
                break
            }
        }
        if(leftLetter == '-' || rightLetter == '-')
            return null
        val column1 = threeWordKeys[Triple(key[0], key[2], leftLetter)]!!.random(rnd)
        val column3 = threeWordKeys[Triple(rightLetter, rightMiddle[4], key[4])]!!.random(rnd)

        val res = threeWordKeys[Triple(key[0], key[1], rightLetter)]!!.random(rnd) + "\n" +
                column1[1] + " " + downMiddle[1] + " " + column3[1] + "\n" +
                rightMiddle + "\n" +
                column1[3] + " " + downMiddle[3] + " " + column3[3] + "\n" +
                threeWordKeys[Triple(leftLetter, downMiddle[4], key[4])]!!.random(rnd)
        return res
    }

    /**
     * дк*   * - н р т
     * лмк
     * *лч   * - н р с б
     */
    fun getCornerLetter(firstLetter: Char, graph: Graph, middleLetter: Char, downRightCorner: Char): List<String>? {
        val rightUpper = graph.first[firstLetter]
            ?.filter { it[2] == middleLetter }
            ?.filter { word -> word[4] in (graph.third[downRightCorner]?.map { it[0] }?.toSet() ?: return null) }
            ?: return null
        if (rightUpper.isEmpty())
            return null
        return rightUpper
    }

    fun getMiddleLetter(
        firstLetter: Char,
        graph: Graph,
        middleMiddleLetter: Char,
        corner: List<String>
    ): List<String>? {
        val rightMiddle = graph.first[firstLetter]
            ?.filter { it[2] == middleMiddleLetter }
            ?.filter { word -> word[4] in corner.map { it[2] } }
            ?: return null
        if (rightMiddle.isEmpty())
            return null
        return rightMiddle
    }
}

/**
 * based on four-lettered corners, get:
 * ab*
 * efc
 * __d
 */
fun getFShaped(graph: Graph, fourLetters: Counter<String>) {
    val file = PrintWriter(File("res.txt").outputStream())
    for (corner in fourLetters.keys) {
        if (graph.third[corner[2]] == null)
            continue
        // this is a complicated code that I do not want to make understandable
        // so here is this comment
        for (middleWord in graph.third[corner[2]]!!) {
            val withB = graph.first[corner[1]]
                ?.filter { it[2] == middleWord[2] }
                ?.map { it[4] }?.toSet()
                ?: continue
            if (withB.isEmpty())
                continue
            val withA = graph.first[corner[0]]!!.filter { it[2] == middleWord[0] }.map { it[4] }.toSet()
            if (withA.isEmpty())
                continue
            val withD = graph.third[corner[3]] ?: continue
            for (word in withD) {
                if (word[0] in withA && word[2] in withB) {
                    file.write(corner[0].toString() + corner[1] + middleWord[0] + middleWord[2] + corner[3])
                    file.write("\n")
                }
            }
        }
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