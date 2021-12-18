package me.fmanaila.aoc.day4

class Board(private val boardArray: Array<IntArray>) {
    // array containing all possible solutions of bingo for this board
    // each element is a set of numbers that, if called from now on, will result in bingo
    private val solutions: Array<MutableSet<Int>> = createSolutions()

    //numbers called so far
    private val calledNumbers: MutableList<Int> = mutableListOf()

    private fun createSolutions(): Array<MutableSet<Int>> {
        val solutions = mutableListOf<MutableSet<Int>>()
        // add each row as possible solution
        boardArray.forEach { row -> solutions += row.toMutableSet() }
        // add each column as possible solution
        for(i in 0..4) {
            val columnSolution = boardArray.map { row -> row[i] }.toMutableSet()
            solutions.add(columnSolution)
        }
        return solutions.toTypedArray()
    }

    /**
     * Returns true if bingo.
     */
    fun mark(num: Int): Boolean {
        calledNumbers.add(num)
        // remove this number from each of the solutions
        solutions.forEach { solution ->
            if(solution.remove(num) && solution.isEmpty()) {
                // if no numbers remaining in this solution, then bingo!
                return true // BINGO!
            }
        }
        return false
    }

    fun score(): Int {
        val sumOfUncalledNumbers = uncalledNumbers().sum()
        return sumOfUncalledNumbers * calledNumbers.last()
    }

    private fun uncalledNumbers() = allBoardNumbers().filterNot { calledNumbers.contains(it) }

    private fun allBoardNumbers() = boardArray.flatMap { ints: IntArray -> ints.asIterable() }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.appendLine("Called numbers: $calledNumbers.")
        sb.appendLine()
        boardArray.forEach { row ->
            sb.appendLine(row.joinToString(
                " ",
                transform = { n -> n.toString().padStart(3, ' ')}
            ))
        }
        return sb.toString()
    }

}