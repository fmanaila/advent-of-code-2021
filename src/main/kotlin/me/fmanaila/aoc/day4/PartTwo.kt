package me.fmanaila.aoc.day4

import me.fmanaila.aoc.inputLines

fun main() {
    val linesIt = inputLines("input/day04.txt")
    val bingoNumbers = readBingoNumbers(linesIt)
    val boards = readBoards(linesIt).toMutableList()

    val winningBoards = mutableListOf<Board>()
    bingoNumbers.forEach { number ->
        val boardsIterator = boards.iterator()
        while(boardsIterator.hasNext()) {
            val board = boardsIterator.next()
            if(board.mark(number)) {
                winningBoards.add(board)
                boardsIterator.remove()
            }
        }
    }

    val lastBoard = winningBoards.last()
    println("Score: ${lastBoard.score()}")
    println(lastBoard.toString())
}