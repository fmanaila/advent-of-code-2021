package me.fmanaila.aoc.day6

import me.fmanaila.aoc.inputLines
import java.util.*

fun main() {
    val fish = inputLines("input/day06.txt").next().split(",").map { it.toInt() }.toList()
    val howManyWithAge = arrayListOf<Long>(0,0,0,0,0,0,0,0,0)
    fish.forEach { age -> howManyWithAge[age]++ }
    for(i in 1..256) {
        Collections.rotate(howManyWithAge, -1)
        howManyWithAge[6] += howManyWithAge[8]
    }
    println(howManyWithAge.sum())
}

fun bruteForceVersion() {
    var fish = inputLines("input/day06.txt").next().split(",").map { it.toInt() }.toList()
    val babyMap = fish.toSet().map { age ->
        val x = howMany(age, 256)
        println("Age $age makes $x lanternfish")
        Pair(age, x)
    }.toMap()
    println(fish.sumOf { age -> babyMap[age]!! })
    // 1767323539209
}

fun howMany(age: Int, daysLeft: Int): Long {
    if(age >= daysLeft) {
        return 1
    }
    return howMany(7, daysLeft - age) + howMany(9, daysLeft - age)
}


