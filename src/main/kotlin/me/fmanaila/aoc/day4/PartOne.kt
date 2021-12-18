package me.fmanaila.aoc.day4

import me.fmanaila.aoc.inputLines

fun main() {
    val linesIt = inputLines("input/day04.txt")
    val bingoNumbers = readBingoNumbers(linesIt)
    val boards = readBoards(linesIt)
    bingoNumbers.forEach { number ->
        boards.forEach { board ->
            if(board.mark(number)) {
                println("Bingo!")
                println("Score = ${board.score()}")
                println(board.toString())
                return
            }
        }
    }
}