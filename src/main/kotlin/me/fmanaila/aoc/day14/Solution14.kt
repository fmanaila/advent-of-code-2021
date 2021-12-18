package me.fmanaila.aoc.day14

import me.fmanaila.aoc.inputLines
import java.util.stream.IntStream
import kotlin.streams.asSequence

fun main() {
    partOne()
    partTwo()
}

fun partOne() {
    val result = solveForNSteps(10)
    println("Part One: ${result.scores.values.maxOrNull()!! - result.scores.values.minOrNull()!!}")
}

fun partTwo() {
    val result = solveForNSteps(40)
    println("Part Two: ${result.scores.values.maxOrNull()!! - result.scores.values.minOrNull()!!}")
}

private fun solveForNSteps(iterations: Int): Step {
    val linesIt = inputLines("input/day14.txt")

    val input = linesIt.next()
    linesIt.next()
    val expansions = linesIt.asSequence().map { line ->
        line.split("->").map(String::trim)
    }
        .map { splitLine -> splitLine[0] to splitLine[1][0] }
        .fold(mapOf<String, Char>()) { acc, pair ->
            acc + pair
        }

    val step = Step(toPairs(input), scores(input))

    val result = IntStream.rangeClosed(1, iterations).asSequence().fold(step) { acc, _ ->
        expand(acc, expansions).also(::println)
    }
    return result
}

fun scores(input: String): Map<Char, Long> {
    return input.asSequence().fold(mapOf<Char, Long>()) { acc, c ->
        acc + (c to acc.getOrDefault(c, 0) + 1)
    }
}

fun toPairs(input: String) : Map<String, Long> {
    // split input in neighboring char pairs and how many times they occur
    // e.g. NNNCB -> NN(2), NC(1), CB(1)
    val map = mutableMapOf<String, Long>()
    for(i in 1 until input.length) {
        val key = input.substring(i-1, i+1)
        map.merge(key, 1, Long::plus)
    }
    return map
}

fun expand(step: Step, expansions: Map<String, Char>) : Step {
    val scores = step.scores.toMutableMap()
    val pairs = step.pairs.flatMap { (pair, times) ->
        // 1. apply the corresponding extension to every par
        // if there's an extension XY -> A and XY appears N times
        // this will generate N pairs XA and N pairs AY
        val insert = expansions[pair] ?: return@flatMap listOf(pair to times)
        // additionally, A will appear an additional N times
        scores.merge(insert, times, Long::plus)
        listOf(
            (pair.first() + insert.toString()) to times,
            (insert + pair.last().toString()) to times
        )
    }.fold(mapOf<String, Long>()) { acc, (pair, times) ->
        acc + (pair to acc.getOrDefault(pair, 0) + times)
    }
    return Step(pairs, scores)
}

/**
 * pairs = key is a string of 2 chars (e.g. AB) and value is how many times it appears in the polymer string
 * scores = key is a char and value is how many times it appears in the polymer string
 */
data class Step(val pairs: Map<String, Long>, val scores: Map<Char, Long>)
