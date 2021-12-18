package me.fmanaila.aoc.day9

import me.fmanaila.aoc.inputLines
import kotlin.math.min

fun main() {
    val matrix = inputLines("input/day09.txt").asSequence()
        .map { line ->
            line.asSequence().map(Char::toString).map(String::toInt).toList().toIntArray()
        }
        .toList()
        .toTypedArray()

    matrix.flatMapIndexed { i, row ->
            row.mapIndexed { j, depth ->
                    var minDepth = Int.MAX_VALUE;
                    if (i >= 1) {
                        // we have rows above
                        minDepth = min(minDepth, matrix[i - 1][j])
                    }
                    if (i < matrix.size - 1) {
                        // we have rows below
                        minDepth = min(minDepth, matrix[i + 1][j])
                    }
                    if (j >= 1) {
                        // we have columns to the left
                        minDepth = min(minDepth, matrix[i][j - 1])
                    }
                    if (j < row.size - 1) {
                        // we have columns to the right
                        minDepth = min(minDepth, matrix[i][j + 1])
                    }
                    if (minDepth > depth) {
                        println("[$i, $j] $depth min! ")
                        depth
                    } else {
                        null
                    }
                }
                .filterNotNull()
        }
        .sumOf { d -> d + 1 }
        .also(::println)
}