package me.fmanaila.aoc.day11

import me.fmanaila.aoc.inputLines
import java.util.stream.IntStream

fun main() {
    val matrix = inputLines("input/day11.txt").asSequence()
        .map { line ->
            line.asSequence().map(Char::toString).map(String::toInt).toList().toIntArray()
        }
        .toList()
        .toTypedArray()

    val maxFlashes = matrix.size * matrix[0].size

    var i = 0;
    while (true) {
        i++
        val flashes = step(matrix)
        if(flashes == maxFlashes) {
            println("All flashed at step $i")
            printMatrix(matrix)
            return
        }
    }

}

private fun printMatrix(matrix: Array<IntArray>) {
    matrix.forEach { row ->
        println(row.toList())
    }
}

private fun step(matrix: Array<IntArray>): Int {
    for(i in matrix.indices) {
        for(j in matrix.indices) {
            matrix[i][j]++
        }
    }

    val flashes = flash(matrix)

    for(i in matrix.indices) {
        for(j in matrix.indices) {
            if(matrix[i][j] > 9) matrix[i][j] = 0
        }
    }

    return flashes
}

private fun flash(matrix: Array<IntArray>) : Int {
    val toFlashList = matrix.flatMapIndexed { i, row ->
        row.mapIndexed { j, oct -> if (oct == 10) (i to j) else null }
    }
        .filterNotNull()
        .toList()
//    println("To flash: $toFlashList")
    return toFlashList.sumOf { toFlash ->
        checkAndFlashOctopus(toFlash, matrix)
    }
}

private fun checkAndFlashOctopus(pair: Pair<Int, Int>, matrix: Array<IntArray>) : Int {
    val (i, j) = pair
    if(matrix[i][j] != 10) {
        return 0
    }
//    println("Flashing $i, $j")
    matrix[i][j]++
    val neighbors = neighbors(i to j, matrix)
    neighbors
        .filter { (i, j) ->
            matrix[i][j] < 10
        }
        .forEach { (i, j) -> matrix[i][j]++ }
//    printMatrix(matrix)
    return neighbors.sumOf { n -> checkAndFlashOctopus(n, matrix) } + 1
}

private fun neighbors(pair: Pair<Int, Int>, matrix: Array<IntArray>) : List<Pair<Int, Int>> {
    val neighbors = mutableListOf<Pair<Int, Int>>()
    val (i, j) = pair
    val rowsAbove = i > 0
    val rowsBelow = i < matrix.size - 1
    val columnsLeft = j > 0
    val columnsRight = j < matrix[i].size - 1
    // 1 2 3
    // 4 # 5
    // 6 7 8
    if(rowsAbove) {
        if(columnsLeft) {
            neighbors.add(i-1 to j-1) // 1
        }
        neighbors.add(i-1 to j) // 2
        if(columnsRight) {
            neighbors.add(i-1 to j+1) // 3
        }
    }
    if(columnsLeft) {
        neighbors.add(i to j-1) // 4
    }
    if(columnsRight) {
        neighbors.add(i to j+1) // 5
    }
    if(rowsBelow) {
        if(columnsLeft) {
            neighbors.add(i+1 to j-1) // 6
        }
        neighbors.add(i+1 to j) // 7
        if(columnsRight) {
            neighbors.add(i+1 to j+1) // 8
        }
    }
    return neighbors
}
