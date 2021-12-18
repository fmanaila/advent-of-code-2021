package me.fmanaila.aoc.day7

import me.fmanaila.aoc.inputLines
import java.util.stream.IntStream
import kotlin.math.abs

fun main() {
    val positions = inputLines("input/day07.txt").next().split(",").map { it.toInt() }
    val max = positions.maxOrNull()!!
    val min = positions.minOrNull()!!

    val distances = positions.map { crabPosition ->
        (0 .. max).map { targetPosition ->
            abs(crabPosition - targetPosition)
        }
    }.toTypedArray()

    val minCost = IntStream.rangeClosed(min, max)
        .map { targetPosition ->
            distances.sumOf { crabDistances ->
                crabDistances[targetPosition]
            }
        }
        .min()

    println(minCost)
}