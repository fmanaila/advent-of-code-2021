package me.fmanaila.aoc.day7

import me.fmanaila.aoc.inputLines
import java.util.stream.IntStream
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max

fun main() {
    val positions = inputLines("input/day07.txt").next().split(",").map { it.toInt() }
//    val positions = inputLines("examples/day07.txt").next().split(",").map { it.toInt() }
    val max = positions.maxOrNull()!!
    val min = positions.minOrNull()!!

    val distances = positions.map { crabPosition ->
        (0 .. max).map { targetPosition ->
            val distance = abs(crabPosition - targetPosition)
            distance * (distance + 1) / 2
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