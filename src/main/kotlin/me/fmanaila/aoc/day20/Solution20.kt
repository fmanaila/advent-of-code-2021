package me.fmanaila.aoc.day20

import me.fmanaila.aoc.inputLines
import java.util.stream.IntStream
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.streams.asSequence

fun main() {
    val linesIt = inputLines("input/day20.txt")
    val enhancer = Enhancer(pixelsToBinary(linesIt.next()))
    linesIt.next() // empty line

    val matrix = linesIt.asSequence()
        .map { pixelsToBinary(it) }
        .fold(Matrix(arrayOf(), 0)) { acc, line ->
            acc.addLine(line)
        }

    val iterations = 50
    val litPixels = litPixelsAfterNIterations(matrix, enhancer, iterations)

    println("$litPixels lit pixels after $iterations iterations")
}

private fun litPixelsAfterNIterations(
    matrix: Matrix,
    enhancer: Enhancer,
    iterations: Int
) = matrix.enhance(enhancer, iterations)
    .data.flatMap { it.toList() }
    .sum()

private fun pixelsToBinary(data: String): IntArray {
    return data.map { c -> if(c == '#') 1 else 0 }.toIntArray()
}

class Enhancer(private val transformationArray: IntArray) {
    fun valueForRegion(region: Array<IntArray>): Int {
        if(region.size != 3) {
            throw IllegalArgumentException("Region size must be 3x3")
        }
        val transformationIndex = region.flatMap { it.toList() }
            .also { list -> if(list.size != 9) throw IllegalArgumentException("Region is more than 9 value") }
            .reversed()
            .foldIndexed(0) { index, acc, v ->
                acc + v * 2.0.pow(index).roundToInt()
            }
        return transformationArray[transformationIndex]
    }
}

class Matrix(val data: Array<IntArray>, val fowValue: Int) {
    fun addLine(line: IntArray): Matrix {
        return Matrix(data + line, fowValue)
    }

    fun enhance(enhancer: Enhancer, times: Int = 1): Matrix {
        return IntStream.rangeClosed(1, times).asSequence().fold(this) { acc, _ ->
            acc.expand().enhance(enhancer)
        }
    }

    private fun enhance(enhancer: Enhancer): Matrix {
        val newData = data.indices.map { i ->
            data[i].indices.map { j ->
                enhancer.valueForRegion(getRegionAround(i, j))
            }.toIntArray()
        }.toTypedArray()

        val newFowValue = enhancer.valueForRegion(getRegionAround(-2, -2))

        return Matrix(newData, newFowValue)
    }

    /**
     * Expands matrix by 1 in every direction, using [fowValue] as value
     * for new pixels
     */
    private fun expand(): Matrix {
        val newData = mutableListOf(
            IntArray(data[0].size + 2) { fowValue }
        )

        data.map { line ->
            val lineAsList = line.toMutableList()
            lineAsList.add(0, fowValue)
            lineAsList.add(fowValue)
            lineAsList.toIntArray()
        }.forEach(newData::add)

        newData.add(IntArray(data[0].size + 2) { fowValue })

        return Matrix(newData.toTypedArray(), fowValue)
    }

    /**
     * Returns the value associated with the [i] and [j] parameters.
     * If [i] or [j] are out of bounds, will return the [fowValue]
     */
    operator fun get(i: Int, j: Int): Int {
        if (i < 0 || i > data.lastIndex || j < 0 || j > data[0].lastIndex) {
            return fowValue
        }
        return data[i][j]
    }

    private fun getRegionAround(i: Int, j: Int): Array<IntArray> {
        return arrayOf(
            intArrayOf(this[i-1, j-1], this[i-1, j], this[i-1, j+1]),
            intArrayOf(this[  i, j-1], this[  i, j], this[  i, j+1]),
            intArrayOf(this[i+1, j-1], this[i+1, j], this[i+1, j+1]),
        )
    }
}
