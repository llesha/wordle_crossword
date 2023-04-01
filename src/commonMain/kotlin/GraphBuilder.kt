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

    fun getAllTripleCombinations(): MutableMap<Triple<Char, Char, Char>, List<String>> {
        val res = mutableMapOf<Triple<Char, Char, Char>, List<String>>()
        for (f in first.keys)
            for (s in second.keys)
                for (t in third.keys) {
                    val num = calcIntersection(f, s, t)
                    if (num.isNotEmpty())
                        res[Triple(f, s, t)] = num
                }
        return res
    }

    fun getTwoLetterQuantity(): MutableMap<String, Int> {
        val res = mutableMapOf<String, Int>()
        for (f in first) {
            for (str in f.value) {
                addOrCreateSet(res, f.key.toString() + str[2])
            }
        }
        return res
    }

    private fun calcIntersection(l1: Char, l2: Char, l3: Char): List<String> {
        return first[l1]!!.filter { it[2] == l2 && it[4] == l3 }
    }

    /**
     * get 4 letters:
     * ab*
     * __c
     * __d
     */
    fun getFourLetters(): Counter<String> {
        val res = Counter<String>()
        for (set in first.values)
            for (firstWord in set) {
                if (first[firstWord.last()] == null)
                    continue
                for (secondWord in first[firstWord.last()]!!)
                    res.add(getFourLetters(firstWord, secondWord))
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

class Counter<T>() {
    constructor(list: List<T>) : this() {
        map[list.first()] = 1
    }

    private val map = mutableMapOf<T, Int>()
    val keys: Set<T>
        get() = map.keys
    val values: MutableCollection<Int>
        get() = map.values

    fun add(value: T) {
        if (!map.containsKey(value))
            map[value] = 0
        map[value] = map[value]!! + 1
    }

    operator fun get(value: T): Int? {
        return map[value]
    }
}
