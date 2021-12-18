package me.fmanaila.aoc.day13

import me.fmanaila.aoc.inputLines

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

    val folded = foldPoints(points, folds[0])
    println(folded)
    println(folded.size)
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