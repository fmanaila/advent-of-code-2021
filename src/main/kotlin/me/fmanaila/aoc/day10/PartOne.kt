package me.fmanaila.aoc.day10

import me.fmanaila.aoc.inputLines

fun main() {
    val score = inputLines("input/day10.txt").asSequence()
        .mapIndexed { index, line ->
            line.fold(mutableListOf<Char>()) { acc, c ->
                if (isOpenBracket(c)) {
                    acc.add(c)
                    acc
                } else {
                    val lastChar = acc.removeLast()
                    if (MATCHING_BRACKETS[lastChar] != c) {
                        println("Illegal '$c' on line $index: $line")
                        return@mapIndexed c
                    }
                    acc
                }
            }
        }
        .mapNotNull { c -> SCORING[c] }
        .sum()

    println(score)
}

private val SCORING = mapOf(
    ')' to 3,
    ']' to 57,
    '}' to 1197,
    '>' to 25137
)