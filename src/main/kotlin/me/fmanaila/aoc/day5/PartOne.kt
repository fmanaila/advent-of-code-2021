package me.fmanaila.aoc.day5

import me.fmanaila.aoc.inputLines
import java.lang.IllegalArgumentException
import java.util.regex.Pattern

fun main() {
    val lines = inputLines("input/day05.txt").asSequence()
        .map { lineOfText -> parseLine(lineOfText) }
        .toList()

    var points = 0
    for(i in 0..999) {
        for(j in 0..999) {
            val point = Point(i, j)
            var covered = 0
            for(line in lines) {
                if(line.covers(point)) {
                    covered++
                }
                if(covered > 1) {
                    points++
                    println("$points points covered by more than 1 line")
                    break
                }
            }
        }
    }
    println(points)
}

private val LINE_REGEX = Pattern.compile("(\\d+),(\\d+) -> (\\d+),(\\d+)")

fun parseLine(lineOfText: String): Line {
    val m = LINE_REGEX.matcher(lineOfText)
    if(!m.find()) {
        throw IllegalArgumentException("Cannot parse line from $lineOfText")
    }
    return Line(
        Point(m.group(1).toInt(), m.group(2).toInt()),
        Point(m.group(3).toInt(), m.group(4).toInt())
    )
}
