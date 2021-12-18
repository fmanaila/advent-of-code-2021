package me.fmanaila.aoc.day8

import me.fmanaila.aoc.inputLines

fun main() {
//    val count = inputLines("examples/day08.txt").asSequence()
    val count = inputLines("input/day08.txt").asSequence()
        .map { line -> line.split("|")[1] }
        .flatMap { output -> output.trim().split(" ") }
        .filter { digit ->
            digit.length in intArrayOf(2,4,3,7)
        }
        .count()

    println(count)
}