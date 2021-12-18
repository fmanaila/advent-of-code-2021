package me.fmanaila.aoc.day13

import me.fmanaila.aoc.inputLines
import java.util.stream.IntStream
import kotlin.math.min
import kotlin.streams.toList

fun main() {
    val linesIt = inputLines("input/day13.txt")
    val points = mutableListOf<Pair<Int, Int>>()
    // read points
    while(linesIt.hasNext()) {
        val lineSplit = linesIt.next().split(",")
        if(lineSplit.size == 1) {
            break
        }
        points.add(Pair(lineSplit[0].trim().toInt(), lineSplit[1].trim().toInt()))
    }

    val folds = mutableListOf<Pair<Int, Int>>()
    // read folds
    while(linesIt.hasNext()) {
        val foldText = linesIt.next().substringAfterLast(" ") // x=1234 or y=1234
        val foldValue = foldText.substringAfterLast("=")
        if(foldText.startsWith("x")) {
            folds.add(Pair(foldValue.toInt(), 0))
        } else {
            folds.add(Pair(0, foldValue.toInt()))
        }
    }

    // execute fold
    var folded = points.toList()
    var minx = 1000
    var miny = 1000
    for(fold in folds) {
        folded = foldPoints(folded, fold)
        if(fold.first != 0) minx = min(minx, fold.first)
        if(fold.second != 0) miny = min(miny, fold.second)
    }

    // print result
    val matrix = IntStream.rangeClosed(0, miny).mapToObj {
        IntStream.rangeClosed(0, minx).mapToObj { "  " }.toList().toTypedArray()
    }.toList().toTypedArray()

    folded.forEach { (i, j) -> matrix[j][i] = "# " }

    matrix.forEach { line ->
        println(line.toList().joinToString(""))
    }
}

private fun foldPoints(points: List<Pair<Int, Int>>, fold: Pair<Int, Int>) : List<Pair<Int, Int>> {
    return points.map { (i, j) ->
        if(fold.first != 0) {
            val foldValue = fold.first
            if(i > foldValue) (foldValue - (i - foldValue)) to j else i to j
        } else {
            val foldValue = fold.second
            if(j > foldValue) i to (foldValue - (j - foldValue)) else i to j
        }
    }
        .toSet()
        .toList()
}