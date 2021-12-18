package me.fmanaila.aoc.day10

import me.fmanaila.aoc.inputLines

fun main() {
    val scores = inputLines("input/day10.txt").asSequence()
        .mapNotNull { line ->
            line.fold(mutableListOf<Char>()) { acc, c ->
                if (isOpenBracket(c)) {
                    acc.apply { add(c) }
                } else {
                    if (MATCHING_BRACKETS[acc.removeLast()] != c) {
                        println("Illegal '$c' on line $line")
                        return@mapNotNull null
                    }
                    acc
                }
            }
        }
        .map { remainingBracketsToBeClosed -> remainingBracketsToBeClosed
            .map(MATCHING_BRACKETS::getValue)
            .reversed()
            .fold(0L) { score, c -> score * 5 + SCORING[c]!! }
        }
        .toList()
        .sorted()
        .also(::println)


    println(scores[scores.size/2])
}

private val SCORING = mapOf(
    ')' to 1,
    ']' to 2,
    '}' to 3,
    '>' to 4
)