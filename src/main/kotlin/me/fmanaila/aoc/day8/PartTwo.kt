package me.fmanaila.aoc.day8

import me.fmanaila.aoc.inputLines
import java.lang.IllegalArgumentException
import java.lang.RuntimeException

fun main() {

    inputLines("input/day08.txt").asSequence()
        .map { line ->
            val splitInput = line.split("|")
            val input = splitInput[0].trim()
            val toDecode = splitInput[1].trim()
            val analyzer = input.split(" ")
                .sortedBy(String::length)
                .fold(Analyzer()) { analyzer, scrambledDigit ->
                    analyzer.addInputDigit(scrambledDigit)
                    analyzer
                }
            if(!analyzer.isSolved()) throw RuntimeException("Failed to solve: $line")
            val solution = analyzer.getSolution()!!
            val decodedNumber = solution.getDecoder().decodeNumber(toDecode.split(" ").toTypedArray())
            println("Input: $line")
            println("Decoded number: $decodedNumber")
            println("Solution: $solution")
            decodedNumber
        }
        .sum()
        .also(::println)
}

val NUMBER_TO_DIGIT = mapOf(
    1 to "cf",
    2 to "acdeg",
    3 to "acdfg",
    4 to "bcdf",
    5 to "abdfg",
    6 to "abdefg",
    7 to "acf",
    8 to "abcdefg",
    9 to "abcdfg",
    0 to "abcefg"
)

val DIGIT_TO_NUMBER = NUMBER_TO_DIGIT.entries.associate{ (k, v) -> v to k }

class Analyzer {

    private var solutions: List<Solution> = emptyList()

    fun addInputDigit(input: String) {
        if(isSolved()) {
//            println("Analysis already complete. Skipping new input.")
            return
        }
        solutions = NUMBER_TO_DIGIT.values
            // find digits matching length of input
            .filter { value -> value.length == input.length }
            // generate all possible scramblings of wires
            .map { value ->
                possibleCombinations(value, input)
            }
            // add all possible combinations to existing solutions
            // solutions will become null/invalid when adding conflicting mappings
            .flatMap { listOfListOfPairs ->
                if(solutions.isEmpty()) {
                    listOfListOfPairs.map { listOfPairs ->
                        listOfPairs.fold(Solution()) { s, p -> s.extendWith(p.first, p.second)!! }
                    }
                } else {
                    listOfListOfPairs.flatMap { listOfPairs ->
                        solutions.mapNotNull { solution ->
                            var s : Solution? = solution
                            for(pair in listOfPairs) {
                                if(s == null) {
                                    return@mapNotNull null
                                }
                                s = s.extendWith(pair)
                            }
                            s
                        }
                    }
                }
            }
    }

    fun isSolved() : Boolean {
        return solutions.size == 1 && solutions[0].isComplete()
    }

    fun getSolution(): Solution? {
        if(isSolved()) {
            return solutions[0]
        }
        return null
    }

    fun howMany() {
        println("All: ${solutions.size} solutions")
        println("${ solutions.filter(Solution::isComplete).count()} solutions complete")
        solutions.forEach(::println)
    }
}


class Solution(private val map: Map<String, String> = emptyMap()) {

    fun extendWith(pair: Pair<String, String>) : Solution? {
        return extendWith(pair.first, pair.second)
    }

    fun extendWith(key: String, value: String) : Solution? {
        if(map.containsKey(key) && !map[key].equals(value)) {
            return null
        }
        if(!map.containsKey(key) && map.containsValue(value)) {
            return null
        }
        return Solution(map + (key to value))
    }

    fun isComplete(): Boolean {
        return map.size == 7
    }

    fun getDecoder(): Decoder {
        if(!isComplete()) {
            throw UnsupportedOperationException("Solution not complete")
        }
        return Decoder(map.entries.associate{ (k, v) -> v to k })
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.appendLine("--------------------------")
        map.toSortedMap().forEach { (k, v) -> sb.appendLine("$k -> $v")}
        sb.appendLine("--------------------------")
        return sb.toString()
    }

}

class Decoder(private val map: Map<String, String>) {
    fun decodeDigit(string: String) : Int {
        val digit: String = string.asSequence().map { c ->
                map[c.toString()] ?: throw IllegalArgumentException("Can't convert '$string' to digit. No mapping for '$c'")
            }
            .sorted()
            .joinToString("")

        return DIGIT_TO_NUMBER[digit]
            ?: throw IllegalArgumentException("Can't convert $digit to digit")
    }

    fun decodeNumber(string: Array<String>) : Int {
        return string.fold(0) { acc, digit ->
            acc * 10 + decodeDigit(digit)
        }
    }
}

fun possibleCombinations(value: String, input: String) : List<List<Pair<String, String>>> {
    if(value.isEmpty()) {
        return listOf(emptyList())
    }

    val ret: MutableList<List<Pair<String, String>>> = mutableListOf()
    val vc = value[0]
    for(ic in input) {
        ret += possibleCombinations(value.substring(1), input.filterNot(ic::equals)).map { it + (vc.toString() to ic.toString()) }
    }

    return ret
}