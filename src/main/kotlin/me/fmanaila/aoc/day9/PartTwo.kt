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
    val lowPoints = findLowPoints(matrix)

    val result = lowPoints
        .map { point -> basinSize(point, matrix) }
        .sorted().reversed()
        .toList()
        .also(::println)
        .take(3)
        .fold(1) { acc: Int, basinSize: Int ->
            acc * basinSize
        }

    println(result)
}

private fun findLowPoints(matrix: Array<IntArray>) = matrix.flatMapIndexed { i, row ->
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
            (i to j)
        } else {
            null
        }
    }
        .filterNotNull()
}
    .toList()

fun basinSize(point: Pair<Int, Int>, matrix: Array<IntArray>) : Int {
    return connectedPointsUpwards(point, matrix).count() + 1
}

fun connectedPointsUpwards(point: Pair<Int, Int>, matrix: Array<IntArray>) : Set<Pair<Int, Int>> {
    val i = point.first
    val j = point.second
    val currentDepth = matrix[i][j]
    val connectedPointsUpwards = mutableSetOf<Pair<Int, Int>>() // Set() because algorithm below will produce duplicates
    if (i >= 1) {
        // we have rows above
        val nextDepth = matrix[i - 1][j]
        val nextPoint = i-1 to j
        if(nextDepth > currentDepth && nextDepth < 9) {
            connectedPointsUpwards.add(nextPoint)
            connectedPointsUpwards.addAll(connectedPointsUpwards(nextPoint, matrix))
        }
    }
    if (i < matrix.size - 1) {
        // we have rows below
        val nextDepth = matrix[i + 1][j]
        val nextPoint = i+1 to j
        if(nextDepth > currentDepth && nextDepth < 9) {
            connectedPointsUpwards.add(nextPoint)
            connectedPointsUpwards.addAll(connectedPointsUpwards(nextPoint, matrix))
        }
    }
    if (j >= 1) {
        // we have columns to the left
        val nextDepth = matrix[i][j-1]
        val nextPoint = i to j-1
        if(nextDepth > currentDepth && nextDepth < 9) {
            connectedPointsUpwards.add(nextPoint)
            connectedPointsUpwards.addAll(connectedPointsUpwards(nextPoint, matrix))
        }
    }
    if (j < matrix[i].size - 1) {
        // we have columns to the right
        val nextDepth = matrix[i][j+1]
        val nextPoint = i to j+1
        if(nextDepth > currentDepth && nextDepth < 9) {
            connectedPointsUpwards.add(nextPoint)
            connectedPointsUpwards.addAll(connectedPointsUpwards(nextPoint, matrix))
        }
    }
    return connectedPointsUpwards
}
