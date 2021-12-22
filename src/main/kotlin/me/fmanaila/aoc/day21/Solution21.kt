package me.fmanaila.aoc.day21

import java.util.stream.IntStream
import kotlin.math.max
import kotlin.math.min
import kotlin.streams.asSequence

fun main() {
    check(partOne(4, 8) == 739785L)
    println("Part One: ${partOne(6, 9)}") // 925605

    check(partTwo(4, 8) == 444356092776315)
    println("Part Two: ${partTwo(6, 9)}") // 486638407378784 - 413013330504401
}

private fun partOne(position1: Int, position2: Int): Long {
    println("--------- PART ONE ($position1, $position2) ---------")
    val targetScore = 1000
    val p1 = QuantumPlayer(position1, targetScore)
    val p2 = QuantumPlayer(position2, targetScore)

    var game = QuantumGame(p1, p2, DeterministicQDice())
    while(!game.isOver()) {
        game = game.playRound()
    }
    val dice = game.dice as DeterministicQDice
    // there should be only one game
    val playerXScore = game.playerOnTurn.getScores().firstNotNullOf { it.key }
    val playerYScore = game.nextPlayer.getScores().firstNotNullOf { it.key }
    println("Dice rolled ${dice.rolls} times")
    println("Score is $playerXScore to $playerYScore")
    return min(playerXScore, playerYScore) * dice.rolls
}

private fun partTwo(position1: Int, position2: Int): Long {
    println("--------- PART TWO ---------")
    val targetScore = 21
    val p1 = QuantumPlayer(position1, targetScore)
    val p2 = QuantumPlayer(position2, targetScore)

    var game = QuantumGame(p1, p2, D3QuantumDice)
    while(!game.isOver()) {
        game = game.playRound()
    }
    println("Games won by each side (in no particular order): ${game.playerOnTurn.wins} - ${game.nextPlayer.wins}")
    return max(game.playerOnTurn.wins, game.nextPlayer.wins)
}

private class QuantumGame(val playerOnTurn: QuantumPlayer, val nextPlayer: QuantumPlayer, val dice: QuantumDice) {
    fun playRound(): QuantumGame {
        val rolledDice = dice.roll()
        val p = playerOnTurn.play(nextPlayer.countGamesInProgress(), rolledDice.possibleDiceOutcomes())
        return QuantumGame(nextPlayer, p, rolledDice)
    }

    fun isOver(): Boolean {
        return !playerOnTurn.hasGamesInProgress() || !nextPlayer.hasGamesInProgress()
    }
}

interface QuantumDice {

    // keys = sum of dice outcomes in 3 rolls
    // values = how many times it can happen in 3 rolls
    fun possibleDiceOutcomes(): Map<Int, Int>

    fun roll(): QuantumDice
}

object D3QuantumDice : QuantumDice {
    // keys = dice outcome
    // values = how many times it can happen in 3 rolls
    override fun possibleDiceOutcomes(): Map<Int, Int> {
        return listOf(1, 2, 3).flatMap { i ->
            listOf(1, 2, 3).flatMap { j ->
                listOf(1, 2, 3).map { k ->
                    i + j + k
                }
            }
        }
            .groupBy { it }
            .mapValues { it.value.size }
    }

    override fun roll(): QuantumDice {
        return this
    }
}

class DeterministicQDice : QuantumDice {
    val rolls: Long
    private val value: Int

    constructor() {
        this.rolls = 0
        this.value = -2
    }

    private constructor(rolls: Long = 0, value: Int = 1) {
        this.rolls = rolls
        this.value = value
    }

    override fun possibleDiceOutcomes(): Map<Int, Int> {
        if(rolls == 0L) throw IllegalStateException("roll() first!")
        return mapOf(
            value + wrapAt100(value + 1) + wrapAt100(value + 2) to 1
        )
    }

    private fun wrapAt100(value: Int): Int {
        val rest = value % 100
        return if(rest != 0) rest else 100
    }

    override fun roll(): QuantumDice {
        return DeterministicQDice(rolls + 3, wrapAt100(value + 3))
    }
}

private class QuantumPlayer {
    // mat[score][pos] = how many universes are there, where player has [score] points
    // and is on [pos] position on the board
    private val mat: Array<LongArray>
    private val targetScore: Int
    val wins: Long

    constructor(position: Int, targetScore: Int) {
        this.mat = Array(targetScore + 10) { LongArray(11) { 0L } }
        mat[0][position] = 1
        this.wins = 0
        this.targetScore = targetScore
    }

    private constructor(mat: Array<LongArray>, totalWins: Long, targetScore: Int) {
        this.mat = mat
        this.wins = totalWins
        this.targetScore = targetScore
    }

    // keys = score
    // value = how many games with that score
    fun getScores(): Map<Int, Long> {
        return mat.mapIndexed { score, scores ->
            score to scores.sum()
        }.filter { it.second != 0L }.toMap()
    }

    fun hasGamesInProgress(): Boolean {
        return mat.filterIndexed { score, _ ->
            score < targetScore
        }.find { scores ->
            scores.find { it != 0L } != null
        } != null
    }

    fun countGamesInProgress(): Long {
        return mat.filterIndexed { score, _ ->
            score < targetScore
        }.sumOf { scores ->
            scores.sum()
        }
    }

    fun play(gamesInProgressByOtherPlayer: Long, possibleDiceOutcomes: Map<Int, Int>): QuantumPlayer {
        val newMat = Array(targetScore + 10) { LongArray(11) { 0 } }
        var newWins = 0L
        for((diceValue, times) in possibleDiceOutcomes) {
            for(oldScore in 0 until targetScore) {
                for(oldPos in mat[oldScore].indices) {
                    val newPos = getNextPosition(oldPos, diceValue)
                    val newScore = oldScore + newPos
                    val possibleGamesWithCurrentScoreAndPosition = mat[oldScore][oldPos]
                    if(newScore >= targetScore) {
                        newWins += times * possibleGamesWithCurrentScoreAndPosition * gamesInProgressByOtherPlayer
                    }
                    newMat[newScore][newPos] += times * possibleGamesWithCurrentScoreAndPosition
                }
            }
        }
        return QuantumPlayer(newMat, newWins + wins, targetScore)
    }

    private fun getNextPosition(oldPos: Int, diceRoll: Int): Int {
        val rest = (oldPos + diceRoll) % 10
        return if (rest != 0) rest else 10
    }

    fun print() {
        println("Wins: $wins")
        printMatrix(mat)
    }

    private fun printMatrix(mat: Array<LongArray>) {
        val header = IntStream.rangeClosed(1, 10).asSequence()
            .joinToString(" ") { "    pos ${it.toString().padStart(2)}" }
        println("                       $header")

        mat.forEachIndexed { index, line ->
            println("Score ${index.toString().padStart(4)}: " + line.joinToString(" ") { it.toString().padStart(10) })
        }
        println("-".repeat(50))
    }
}