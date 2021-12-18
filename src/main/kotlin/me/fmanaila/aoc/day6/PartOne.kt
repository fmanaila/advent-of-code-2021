package me.fmanaila.aoc.day6

import me.fmanaila.aoc.inputLines
import java.util.stream.IntStream
import kotlin.streams.toList

fun main() {
    var fish = inputLines("input/day06.txt").next().split(",").map { it.toInt() }.toList()
    for (i in 1..80) {
       fish = iterate(fish)
    }
    println(fish.count())
}

fun iterate(fish: List<Int>): List<Int> {
    return fish.flatMap { age ->
        if(age == 0) {
            IntStream.of(6, 8).toList()
        } else {
            IntStream.of(age - 1).toList()
        }
    }.toList()
}