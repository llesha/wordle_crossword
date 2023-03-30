import kotlin.random.Random

val rnd = Random

class GraphBuilder {
    fun parseWords(words: Array<String>): Graph {
        val graph = Graph()
        for (word in words) {
            addOrCreateSet(graph.first, word, 0)
            addOrCreateSet(graph.second, word, 2)
            addOrCreateSet(graph.third, word, 4)
        }
        println(words.size)
        println(words.toSet().size)
        return graph
    }
}

class Graph {
    val first = mutableMapOf<Char, MutableSet<String>>()
    val second = mutableMapOf<Char, MutableSet<String>>()
    val third = mutableMapOf<Char, MutableSet<String>>()
    val firstTwo = mutableMapOf<Pair<Char, Char>, MutableSet<String>>()
    val lastTwo = mutableMapOf<Pair<Char, Char>, MutableSet<String>>()

    fun getAllTripleCombinations(): MutableList<Pair<Triple<Char, Char, Char>, Int>> {
        val res = mutableListOf<Pair<Triple<Char, Char, Char>, Int>>()
        for (f in first.keys)
            for (s in second.keys)
                for (t in third.keys) {
                    val num = calcIntersection(f, s, t)
                    if (num != 0)
                        res.add(Triple(f, s, t) to num)
                }
        return res
    }

    private fun calcIntersection(l1: Char, l2: Char, l3: Char): Int {
        return first[l1]!!.count { it[2] == l2 && it[4] == l3 }
    }

    /**
     * get 4 letters:
     * ab*
     * __c
     * __d
     */
    fun getFourLetters(): MutableMap<String, Int> {
        val res = mutableMapOf<String, Int>()
        for (set in first.values)
            for (firstWord in set) {
                if (first[firstWord.last()] == null)
                    continue
                for (secondWord in first[firstWord.last()]!!)
                    addOrCreateSet(res, getFourLetters(firstWord, secondWord))
            }
        return res
    }

    /**
     * based on four-lettered corners, get:
     * ab*
     * efc
     * __d
     */
    fun getFShaped(fourLetters: MutableMap<String, Int>): MutableMap<String, Int> {
        val res = mutableMapOf<String, Int>()
        for (corner in fourLetters.keys) {
            if (third[corner[2]] == null)
                continue
            for (middleWord in third[corner[2]]!!) {
                val withB = first[corner[1]]?.filter { it[2] == middleWord[2] }
                if(withB == null || withB.isEmpty())
                    continue
                val withA = first[corner[0]]!!.filter { it[2] == middleWord[0] }
                if(withA.isEmpty())
                    continue
            }
        }
        return res
    }

    private fun wordToThreeLetters(word: String): String {
        return word[0].toString() + word[2].toString() + word[4].toString()
    }

    /**
     * abcde, efghi -> acgi
     */
    private fun getFourLetters(first: String, second: String): String {
        return first[0].toString() + first[2] + second[2] + second[4]
    }
}

fun addOrCreateSet(map: MutableMap<Char, MutableSet<String>>, added: String, index: Int) {
    val letter = added[index]
    if (map[letter] == null)
        map[letter] = mutableSetOf()
    map[letter]!!.add(added)
}

fun addOrCreateList(map: MutableMap<Pair<Char, Char>, MutableList<String>>, key: Pair<Char, Char>, e: String) {
    if (map[key] == null)
        map[key] = mutableListOf()
    map[key]!!.add(e)
}

fun addOrCreateSet(map: MutableMap<String, Int>, added: String) {
    if (map[added] == null)
        map[added] = 0
    map[added] = map[added]!! + 1
}