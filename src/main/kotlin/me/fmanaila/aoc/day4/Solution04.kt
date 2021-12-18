package me.fmanaila.aoc.day4


internal fun readBoards(linesIt: Iterator<String>): List<Board> {
    val boards = mutableListOf<Board>()
    while (linesIt.hasNext()) {
        linesIt.next() // blank line
        val boardArray = arrayOf(
            linesIt.next().chunked(3).map { it.trim().toInt() }.toIntArray(),
            linesIt.next().chunked(3).map { it.trim().toInt() }.toIntArray(),
            linesIt.next().chunked(3).map { it.trim().toInt() }.toIntArray(),
            linesIt.next().chunked(3).map { it.trim().toInt() }.toIntArray(),
            linesIt.next().chunked(3).map { it.trim().toInt() }.toIntArray(),
        )
        boards.add(Board(boardArray))
    }
    return boards.toList()
}

internal fun readBingoNumbers(linesIt: Iterator<String>) =
    linesIt.next().split(",").map { it.trim().toInt() }

